package algorithm.biydaalt_1.service.Algorithm;

import algorithm.biydaalt_1.model.Edge;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DijkstraAlgorithmTest extends AlgorithmTests {

    private DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm();

    @Test
    void testFindPath_SimplePath() {
        List<Integer> path = dijkstraAlgorithm.findPath(1, 5, edges);
        assertNotNull(path);
        assertEquals(List.of(1,2,5), path);
    }

    @Test
    void testFindPath_NoPath() {
        edges.put(7, new ArrayList<>());
        List<Integer> path = dijkstraAlgorithm.findPath(0, 7, edges);
        assertNull(path);
    }

    @Test
    void testFindPath_StartEqualsEnd() {
        List<Integer> path = dijkstraAlgorithm.findPath(0, 0, edges);
        assertNotNull(path);
        assertEquals(List.of(0), path);
    }

    @Test
    void testFindPath_LongerPath() {
        List<Integer> path = dijkstraAlgorithm.findPath(0, 6, edges);
        assertNotNull(path);
        assertEquals(List.of(0, 3, 6), path);
    }

    @Test
    void testFindPath_ThroughIntermediateNodes() {
        List<Integer> path = dijkstraAlgorithm.findPath(0, 2, edges);
        assertNotNull(path);
        assertEquals(List.of(0, 1, 2), path);
    }

    @Test
    void testFindPath_GraphWithDisconnectedComponents() {
        edges.computeIfAbsent(10, k -> new ArrayList<>()).add(new Edge(11, 1.0, "r", "a", false, false, null, "y", 10.0));
        edges.computeIfAbsent(11, k -> new ArrayList<>()).add(new Edge(10, 1.0, "r", "a", false, false, null, "y", 10.0));

        List<Integer> path = dijkstraAlgorithm.findPath(0, 10, edges);
        assertNull(path);

        List<Integer> pathWithinComponent = dijkstraAlgorithm.findPath(10, 11, edges);
        assertNotNull(pathWithinComponent);
        assertEquals(List.of(10, 11), pathWithinComponent);
    }

    @Test
    void testFindPath_NoEdgesForStartNode() {
        edges.put(99, new ArrayList<>());
        List<Integer> path = dijkstraAlgorithm.findPath(99, 0, edges);
        assertNull(path);
    }

    @Test
    void testFindPath_EndNodeHasNoIncomingEdges() {
        List<Integer> path = dijkstraAlgorithm.findPath(0, 5, edges);
        assertNotNull(path);
        assertEquals(5, path.get(path.size() - 1));
    }
}