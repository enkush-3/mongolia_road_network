package algorithm.mongolia_road_network.service;

import algorithm.mongolia_road_network.model.Edge;
import algorithm.mongolia_road_network.model.Node;
import algorithm.mongolia_road_network.model.Response;
import algorithm.mongolia_road_network.service.Algorithm.AStarAlgorithm;
import algorithm.mongolia_road_network.service.Algorithm.Algorithm;
import algorithm.mongolia_road_network.service.Algorithm.BreadthFirstSearchAlgorithm;
import algorithm.mongolia_road_network.service.Algorithm.DijkstraAlgorithm;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Service
public class GraphService {

    @Value("${shapefile.path:/app/shapefiles/gis_osm_roads_free_1.shp}")
    private String shapefilePath;

    private final Map<Integer, List<Edge>> edges = new HashMap<>();
    private final Map<Integer, Node> idToNode = new HashMap<>();
    private final Map<String, Integer> coordToId = new HashMap<>();
    private int nodeIdCounter = 0;

    private final Map<String, Algorithm> algorithms = new HashMap<>();

    private static final Set<String> ALLOWED_ROADS = Set.of(
            "motorway", "trunk", "primary", "secondary", "tertiary", "unclassified", "residential", "service"
    );

    @PostConstruct
    public void init() throws Exception {
        // Алгоритмуудыг бүртгэх
        algorithms.put("dijkstra", new DijkstraAlgorithm());
        algorithms.put("astar", new AStarAlgorithm(idToNode));
        algorithms.put("bfs", new BreadthFirstSearchAlgorithm());

        // Shapefile замыг шалгах
        if (shapefilePath == null || shapefilePath.isEmpty()) {
            throw new IllegalStateException("Shapefile path is not configured. Set 'shapefile.path' property.");
        }
        File shapefile = new File(shapefilePath);
        if (!shapefile.exists()) {
            throw new FileNotFoundException("Shapefile not found at: " + shapefile.getAbsolutePath());
        }

        // Shapefile унших
        ShapefileDataStore dataStore = new ShapefileDataStore(shapefile.toURI().toURL());
        SimpleFeatureSource featureSource = dataStore.getFeatureSource();

        CoordinateReferenceSystem sourceCRS = featureSource.getSchema().getCoordinateReferenceSystem();
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326", true);
        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);

        try (SimpleFeatureIterator iterator = featureSource.getFeatures().features()) {
            while (iterator.hasNext()) {
                SimpleFeature feature = iterator.next();
                Geometry rawGeom = (Geometry) feature.getDefaultGeometry();
                if (rawGeom == null) continue;

                Geometry geom = JTS.transform(rawGeom, transform);

                Object onewayAttr = feature.getAttribute("oneway");
                Object maxspeedAttr = feature.getAttribute("maxspeed");
                Object accessAttr = feature.getAttribute("access");
                Object fclassAttr = feature.getAttribute("fclass");
                Object surfaceAttr = feature.getAttribute("surface");
                Object bridgeAttr = feature.getAttribute("bridge");
                Object tunnelAttr = feature.getAttribute("tunnel");
                Object turn_restrAttr = feature.getAttribute("turn_restr");

                boolean isOneWay = onewayAttr != null && List.of("yes", "true", "1").contains(onewayAttr.toString().toLowerCase());

                if (geom instanceof LineString) {
                    processLineString((LineString) geom, isOneWay,
                            maxspeedAttr, accessAttr, fclassAttr, surfaceAttr, bridgeAttr, tunnelAttr, turn_restrAttr);
                } else if (geom instanceof MultiLineString) {
                    MultiLineString mls = (MultiLineString) geom;
                    for (int i = 0; i < mls.getNumGeometries(); i++) {
                        Geometry g = mls.getGeometryN(i);
                        if (g instanceof LineString) {
                            processLineString((LineString) g, isOneWay,
                                    maxspeedAttr, accessAttr, fclassAttr, surfaceAttr, bridgeAttr, tunnelAttr, turn_restrAttr);
                        }
                    }
                }
            }
        } finally {
            dataStore.dispose();
        }
    }

    private void processLineString(LineString line, boolean isOneWay,
                                   Object maxspeedAttr,
                                   Object accessAttr,
                                   Object fclassAttr,
                                   Object surfaceAttr,
                                   Object bridgeAttr,
                                   Object tunnelAttr,
                                   Object turnRestrAttr) {

        Coordinate[] coords = line.getCoordinates();
        if (coords == null || coords.length < 2) return;

        Double maxspeed_mps = parseMaxspeedToMetersPerSecond(maxspeedAttr);
        String fclass = fclassAttr != null ? fclassAttr.toString().toLowerCase() : null;
        String surface = surfaceAttr != null ? surfaceAttr.toString().toLowerCase() : null;
        boolean bridge = bridgeAttr != null && bridgeAttr.toString().equalsIgnoreCase("yes");
        boolean tunnel = tunnelAttr != null && tunnelAttr.toString().equalsIgnoreCase("yes");
        String turnRestr = turnRestrAttr != null ? turnRestrAttr.toString() : null;
        String access = accessAttr != null ? accessAttr.toString().toLowerCase() : null;

        if (fclass == null || !ALLOWED_ROADS.contains(fclass)) return;
        if (access != null && (access.equals("no") || access.equals("private") || access.equals("foot"))) return;

        for (int i = 0; i < coords.length - 1; i++) {
            Coordinate start = coords[i];
            Coordinate end = coords[i + 1];

            int startId = getNodeId(start.y, start.x);
            int endId = getNodeId(end.y, end.x);

            double distanceMeters = haversine(start.y, start.x, end.y, end.x);
            double weight = distanceMeters;

            edges.computeIfAbsent(startId, k -> new ArrayList<>())
                    .add(new Edge(endId, weight, fclass, surface, bridge, tunnel, turnRestr, access, maxspeed_mps));

            if (!isOneWay) {
                edges.computeIfAbsent(endId, k -> new ArrayList<>())
                        .add(new Edge(startId, weight, fclass, surface, bridge, tunnel, turnRestr, access, maxspeed_mps));
            }
        }
    }

    Double parseMaxspeedToMetersPerSecond(Object maxspeedAttr) {
        if (maxspeedAttr == null) return null;
        try {
            String s = maxspeedAttr.toString().trim().toLowerCase();
            if (s.contains("mph")) {
                s = s.replaceAll("[^0-9\\.\\-]", "");
                if (s.isEmpty()) return null;
                double val = Double.parseDouble(s);
                return val * 0.44704;
            } else if (s.contains("km") || s.contains("kph") || s.contains("km/h") || s.contains("kmh")) {
                s = s.replaceAll("[^0-9\\.\\-]", "");
                if (s.isEmpty()) return null;
                double val = Double.parseDouble(s);
                return val * (1000.0 / 3600.0);
            } else {
                s = s.replaceAll("[^0-9\\.\\-]", "");
                if (s.isEmpty()) return null;
                double val = Double.parseDouble(s);
                return val * (1000.0 / 3600.0);
            }
        } catch (Exception e) {
            return null;
        }
    }

    int getNodeId(double lat, double lon) {
        String key = String.format(Locale.ROOT, "%.7f,%.7f", lat, lon);
        return coordToId.computeIfAbsent(key, k -> {
            int newId = nodeIdCounter++;
            idToNode.put(newId, new Node(newId, lat, lon));
            return newId;
        });
    }

    public int findNearestNode(double lat, double lon) {
        int nearestNodeId = -1;
        double minDistance = Double.MAX_VALUE;
        for (Node node : idToNode.values()) {
            double distance = haversine(lat, lon, node.getLat(), node.getLon());
            if (distance < minDistance) {
                minDistance = distance;
                nearestNodeId = node.getId();
            }
        }
        return nearestNodeId;
    }

    public Response findPath(int startId, int endId, String algorithmName) {
        Algorithm algorithm = algorithms.get(algorithmName.toLowerCase());
        if (algorithm == null) {
            throw new IllegalArgumentException("Unknown algorithm: " + algorithmName);
        }
        return algorithm.findPath(startId, endId, edges);
    }

    public Node getNodeById(int id) {
        return idToNode.get(id);
    }

    public Map<Integer, List<Edge>> getEdges() {
        return edges;
    }

    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}