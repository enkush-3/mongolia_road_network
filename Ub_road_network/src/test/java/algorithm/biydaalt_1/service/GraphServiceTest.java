package algorithm.biydaalt_1.service;

import algorithm.biydaalt_1.model.Edge;
import algorithm.biydaalt_1.model.Node;
import algorithm.biydaalt_1.service.Algorithm.Algorithm;
import algorithm.biydaalt_1.service.Algorithm.BreadthFirstSearchAlgorithm;
import algorithm.biydaalt_1.service.Algorithm.DepthFirstSearchAlgorithm;
import algorithm.biydaalt_1.service.Algorithm.DijkstraAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GraphServiceTest {

    @InjectMocks
    private GraphService graphService;

    @BeforeEach
    void setUp() {

        Map<String, Algorithm> algorithms = new HashMap<>();
        algorithms.put("dijkstra", new DijkstraAlgorithm());
        algorithms.put("dfs", new DepthFirstSearchAlgorithm());
        algorithms.put("bfs", new BreadthFirstSearchAlgorithm());
        ReflectionTestUtils.setField(graphService, "algorithms", algorithms);

        ReflectionTestUtils.setField(graphService, "edges", new HashMap<Integer, List<Edge>>());
        ReflectionTestUtils.setField(graphService, "idToNode", new HashMap<Integer, Node>());
        ReflectionTestUtils.setField(graphService, "coordToId", new HashMap<String, Integer>());
        ReflectionTestUtils.setField(graphService, "nodeIdCounter", 0);

        int id0 = graphService.getNodeId(40.7128, -74.0060);
        int id1 = graphService.getNodeId(34.0522, -118.2437);
        int id3 = graphService.getNodeId(40.7129, -74.0061);
        int id4 = graphService.getNodeId(40.7128, -74.0059);
        int id5 = graphService.getNodeId(34.0523, -118.2436);

        Map<Integer, List<Edge>> privateEdges = (Map<Integer, List<Edge>>) ReflectionTestUtils.getField(graphService, "edges");

        privateEdges.computeIfAbsent(id0, k -> new ArrayList<>()).add(new Edge(id1, 100.0, "motorway", "asphalt", false, false, null, "yes", 30.0));
        privateEdges.computeIfAbsent(id1, k -> new ArrayList<>()).add(new Edge(id0, 100.0, "motorway", "asphalt", false, false, null, "yes", 30.0));
        privateEdges.computeIfAbsent(id0, k -> new ArrayList<>()).add(new Edge(id3, 0.1, "residential", "asphalt", false, false, null, "yes", 10.0));
        privateEdges.computeIfAbsent(id3, k -> new ArrayList<>()).add(new Edge(id4, 0.2, "residential", "asphalt", false, false, null, "yes", 10.0));
        privateEdges.computeIfAbsent(id4, k -> new ArrayList<>()).add(new Edge(id0, 0.15, "residential", "asphalt", false, false, null, "yes", 10.0));
        privateEdges.computeIfAbsent(id1, k -> new ArrayList<>()).add(new Edge(id5, 0.5, "service", "concrete", false, false, null, "yes", 5.0));
    }

    @Test
    void testFindNearestNode() {

        int nearestId = graphService.findNearestNode(40.7128001, -74.0060001);
        assertEquals(graphService.getNodeId(40.7128, -74.0060), nearestId, "Should find Node 0 as the nearest");

        int nearestId3 = graphService.findNearestNode(40.71285, -74.00605);
        Node[] candidates = new Node[]{
                graphService.getNodeById(graphService.getNodeId(40.7128, -74.0060)),
                graphService.getNodeById(graphService.getNodeId(40.7129, -74.0061)),
                graphService.getNodeById(graphService.getNodeId(40.7128, -74.0059))
        };
        double minDist = Double.MAX_VALUE;
        int expectedId = -1;
        for (Node n : candidates) {
            double dist = haversine(40.71285, -74.00605, n.getLat(), n.getLon());
            if (dist < minDist) {
                minDist = dist;
                expectedId = n.getId();
            }
        }
        assertEquals(expectedId, nearestId3, "Should find the correct nearest Node dynamically");

        int nearestIdLondon = graphService.findNearestNode(51.5075, 0.1279);
        Map<Integer, Node> allNodes = (Map<Integer, Node>) ReflectionTestUtils.getField(graphService, "idToNode");
        double minDistance = Double.MAX_VALUE;
        int expectedLondonId = -1;
        for (Node n : allNodes.values()) {
            double dist = haversine(51.5075, 0.1279, n.getLat(), n.getLon());
            if (dist < minDistance) {
                minDistance = dist;
                expectedLondonId = n.getId();
            }
        }
        assertEquals(expectedLondonId, nearestIdLondon, "Should find nearest London Node dynamically");

        GraphService emptyGraphService = new GraphService();
        ReflectionTestUtils.setField(emptyGraphService, "idToNode", new HashMap<Integer, Node>());
        assertEquals(-1, emptyGraphService.findNearestNode(0, 0), "Should return -1 if no nodes exist");
    }


    @Test
    void testGetNodeById() {
        int nodeId = graphService.getNodeId(40.7128, -74.0060);
        Node node = graphService.getNodeById(nodeId);
        assertNotNull(node);
        assertEquals(nodeId, node.getId());
        assertEquals(40.7128, node.getLat());
        assertEquals(-74.0060, node.getLon());

        assertNull(graphService.getNodeById(999), "Should return null for a non-existent ID");
    }

    @Test
    void testParseMaxspeedToMetersPerSecond() {
        assertEquals(60 * 1000.0 / 3600.0, graphService.parseMaxspeedToMetersPerSecond("60 kmh"), 0.001);
        assertEquals(30 * 0.44704, graphService.parseMaxspeedToMetersPerSecond("30 mph"), 0.001);
        assertEquals(80 * 1000.0 / 3600.0, graphService.parseMaxspeedToMetersPerSecond("80"), 0.001);
        assertNull(graphService.parseMaxspeedToMetersPerSecond(null));
        assertNull(graphService.parseMaxspeedToMetersPerSecond("invalid speed"));
        assertNull(graphService.parseMaxspeedToMetersPerSecond(""));
    }


    @Test
    void testGetNodeIdAndCoordToIdMapping() {
        int idA1 = graphService.getNodeId(10.0, 20.0);
        int idA2 = graphService.getNodeId(10.0, 20.0);
        assertEquals(idA1, idA2, "Same coordinates should return the same node ID");

        int idB = graphService.getNodeId(10.1, 20.0);
        assertNotEquals(idA1, idB, "Different coordinates should return different node IDs");

        Node nodeA = graphService.getNodeById(idA1);
        assertNotNull(nodeA);
        assertEquals(10.0, nodeA.getLat());
        assertEquals(20.0, nodeA.getLon());
    }

    @Test
    void testFindPathDelegation() {
        int startId = graphService.getNodeId(40.7128, -74.0060);
        int endId = graphService.getNodeId(34.0522, -118.2437);

        List<Integer> dijkstraPath = graphService.findPath(startId, endId, "dijkstra");
        assertNotNull(dijkstraPath);
        assertTrue(dijkstraPath.size() > 0);
        assertEquals(startId, dijkstraPath.get(0));
        assertEquals(endId, dijkstraPath.get(dijkstraPath.size() - 1));

        List<Integer> bfsPath = graphService.findPath(startId, endId, "bfs");
        assertNotNull(bfsPath);

        List<Integer> dfsPath = graphService.findPath(startId, endId, "dfs");
        assertNotNull(dfsPath);

        assertThrows(IllegalArgumentException.class, () -> {
            graphService.findPath(startId, endId, "unknown");
        });
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
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