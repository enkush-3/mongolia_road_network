package algorithm.mongolia_road_network.service.Algorithm;

import algorithm.mongolia_road_network.model.Edge;
import algorithm.mongolia_road_network.model.Node;
import algorithm.mongolia_road_network.model.Response;

import java.util.*;

public class AStarAlgorithm extends Algorithm {

    private final Map<Integer, Node> idToNode;

    public AStarAlgorithm(Map<Integer, Node> idToNode) {
        this.idToNode = idToNode;
    }

    @Override
    public Response findPath(int startId, int endId, Map<Integer, List<Edge>> edges) {

        if (!edges.containsKey(startId) || !idToNode.containsKey(endId)) {
            return null;
        }

        PriorityQueue<Integer> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(n -> fScore.getOrDefault(n, Double.MAX_VALUE))
        );

        Set<Integer> closedSet = new HashSet<>();
        Map<Integer, Integer> cameFrom = new HashMap<>();

        gScore.clear();
        fScore.clear();

        gScore.put(startId, 0.0);
        fScore.put(startId, heuristic(startId, endId));

        openSet.add(startId);

        while (!openSet.isEmpty()) {
            int current = openSet.poll();

            if (current == endId) {
                return reconstructPath(cameFrom, current, edges);
            }

            closedSet.add(current);

            List<Edge> neighbors = edges.get(current);
            if (neighbors == null) continue;

            for (Edge edge : neighbors) {
                int neighbor = edge.getDestination();
                if (closedSet.contains(neighbor)) continue;

                double tentativeG = gScore.get(current) + edge.getWeight();

                if (tentativeG < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);
                    fScore.put(neighbor, tentativeG + heuristic(neighbor, endId));

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return null;
    }

    private final Map<Integer, Double> gScore = new HashMap<>();
    private final Map<Integer, Double> fScore = new HashMap<>();

    private double heuristic(int a, int b) {
        Node n1 = idToNode.get(a);
        Node n2 = idToNode.get(b);
        return haversine(n1.getLat(), n1.getLon(), n2.getLat(), n2.getLon());
    }

    private Response reconstructPath(Map<Integer, Integer> cameFrom,
                                     int current,
                                     Map<Integer, List<Edge>> edges) {

        List<Integer> path = new ArrayList<>();
        double totalDistance = 0.0;

        path.add(current);
        while (cameFrom.containsKey(current)) {
            int prev = cameFrom.get(current);
            totalDistance += getEdgeWeight(prev, current, edges);
            current = prev;
            path.add(current);
        }

        Collections.reverse(path);
        return new Response(path, totalDistance);
    }

    private double getEdgeWeight(int from, int to, Map<Integer, List<Edge>> edges) {
        for (Edge e : edges.getOrDefault(from, List.of())) {
            if (e.getDestination() == to) {
                return e.getWeight();
            }
        }
        return 0.0;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
