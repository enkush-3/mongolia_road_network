package algorithm.biydaalt_1.service.Algorithm;

import algorithm.biydaalt_1.model.Edge;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BreadthFirstSearchAlgorithmTest extends AlgorithmTests {

    private BreadthFirstSearchAlgorithm bfsAlgorithm = new BreadthFirstSearchAlgorithm();

    @Test
    void testFindPath_SimplePath() {
        List<Integer> path = bfsAlgorithm.findPath(0, 5, edges);
        assertNotNull(path);
        assertEquals(0, path.get(0));
        assertEquals(5, path.get(path.size() - 1));
        assertEquals(3, path.size());

        assertTrue(isValidPath(path));

        assertTrue(path.equals(List.of(0, 1, 2, 5)) ||
                path.equals(List.of(0, 1, 4, 5)) ||
                path.equals(List.of(0, 3, 4, 5)) ||
                path.equals(List.of(0, 4, 5)));
    }

    @Test
    void testFindPath_NoPath() {
        edges.put(7, new ArrayList<>());
        List<Integer> path = bfsAlgorithm.findPath(0, 7, edges);
        assertNull(path);
    }

    @Test
    void testFindPath_StartEqualsEnd() {
        List<Integer> path = bfsAlgorithm.findPath(0, 0, edges);
        assertNotNull(path);
        assertEquals(List.of(0), path);
    }

    @Test
    void testFindPath_LongerPath() {
        List<Integer> path = bfsAlgorithm.findPath(0, 6, edges);
        assertNotNull(path);
        assertEquals(0, path.get(0));
        assertEquals(6, path.get(path.size() - 1));
        assertEquals(List.of(0, 3, 6), path);
    }

    @Test
    void testFindPath_GraphWithDisconnectedComponents() {
        edges.computeIfAbsent(10, k -> new ArrayList<>()).add(new Edge(11, 1.0, "r", "a", false, false, null, "y", 10.0));
        edges.computeIfAbsent(11, k -> new ArrayList<>()).add(new Edge(10, 1.0, "r", "a", false, false, null, "y", 10.0));

        List<Integer> path = bfsAlgorithm.findPath(0, 10, edges);
        assertNull(path);

        List<Integer> pathWithinComponent = bfsAlgorithm.findPath(10, 11, edges);
        assertNotNull(pathWithinComponent);
        assertEquals(List.of(10, 11), pathWithinComponent);
    }

    @Test
    void testFindPath_NoEdgesForStartNode() {
        edges.put(99, new ArrayList<>());
        List<Integer> path = bfsAlgorithm.findPath(99, 0, edges);
        assertNull(path);
    }
    private boolean isValidPath(List<Integer> path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        if (path.size() == 1) {
            return true;
        }

        for (int i = 0; i < path.size() - 1; i++) {
            int u = path.get(i);
            int v = path.get(i + 1);
            boolean connected = edges.getOrDefault(u, List.of()).stream()
                    .anyMatch(edge -> edge.getDestination() == v);
            if (!connected) {
                return false;
            }
        }
        return true;
    }
}