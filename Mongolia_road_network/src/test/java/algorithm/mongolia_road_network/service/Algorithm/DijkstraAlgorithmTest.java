package algorithm.mongolia_road_network.service.Algorithm;

import algorithm.mongolia_road_network.model.Edge;
import algorithm.mongolia_road_network.model.Response;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DijkstraAlgorithmTest extends AlgorithmTests {

    private DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm();

    @Test
    void testFindPath_SimplePath() {
        Response path = dijkstraAlgorithm.findPath(1, 5, edges);
        assertNotNull(path);
        assertEquals(List.of(1,2,5), path.getPath());
        assertEquals(6, path.getTotalDistance());
    }

    @Test
    void testFindPath_NoPath() {
        edges.put(7, new ArrayList<>());
        Response path = dijkstraAlgorithm.findPath(0, 7, edges);
        assertNull(path);
    }

    @Test
    void testFindPath_StartEqualsEnd() {
        Response path = dijkstraAlgorithm.findPath(1, 1, edges);
        assertNotNull(path);
        assertEquals(List.of(1), path.getPath());
        assertEquals(0, path.getTotalDistance());
        assertEquals(1, path.getPath().size());
    }

    @Test
    void testFindPath_ThroughIntermediateNodes() {
        Response path = dijkstraAlgorithm.findPath(1, 2, edges);
        assertNotNull(path);
        assertEquals(List.of(1, 2), path.getPath());
        assertEquals(2, path.getTotalDistance());
    }

    @Test
    void testFindPath_GraphWithDisconnectedComponents() {
        edges.computeIfAbsent(10, k -> new ArrayList<>()).add(new Edge(11, 1.0, "r", "a", false, false, null, "y", 10.0));
        edges.computeIfAbsent(11, k -> new ArrayList<>()).add(new Edge(10, 1.0, "r", "a", false, false, null, "y", 10.0));

        Response path = dijkstraAlgorithm.findPath(0, 10, edges);
        assertNull(path);

        Response pathWithinComponent = dijkstraAlgorithm.findPath(10, 11, edges);
        assertNotNull(pathWithinComponent.getPath());
        assertEquals(List.of(10, 11), pathWithinComponent.getPath());
    }

    @Test
    void testFindPath_NoEdgesForStartNode() {
        edges.put(99, new ArrayList<>());
        Response path = dijkstraAlgorithm.findPath(99, 0, edges);
        assertNull(path);
    }

    @Test
    void testFindPath_EndNodeHasNoIncomingEdges() {
        Response path = dijkstraAlgorithm.findPath(1, 5, edges);
        assertNotNull(path.getPath());
        assertEquals(5, path.getPath().get(path.getPath().size() - 1));
    }
}