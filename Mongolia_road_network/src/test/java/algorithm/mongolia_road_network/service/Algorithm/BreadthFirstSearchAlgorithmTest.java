package algorithm.mongolia_road_network.service.Algorithm;

import algorithm.mongolia_road_network.model.Edge;
import algorithm.mongolia_road_network.model.Response;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BreadthFirstSearchAlgorithmTest extends AlgorithmTests {

    private BreadthFirstSearchAlgorithm bfsAlgorithm = new BreadthFirstSearchAlgorithm();

    @Test
    void testFindPath_SimplePath() {
        Response path = bfsAlgorithm.findPath(1, 5, edges);
        assertNotNull(path);
        assertEquals(1, path.getPath().get(0));
        assertEquals(5, path.getPath().get(path.getPath().size() - 1));
        assertEquals(3, path.getPath().size());

        assertTrue(isValidPath(path.getPath()));

        assertTrue(path.getPath().equals(List.of(1, 2, 5)) ||
                path.getPath().equals(List.of(1, 2, 4, 5)) ||
                path.getPath().equals(List.of(1, 3, 4, 5)) ||
                path.getPath().equals(List.of(1, 3, 4, 2, 5)));
        
        assertTrue(path.getTotalDistance() == 6 || 
                path.getTotalDistance() == 10 || 
                path.getTotalDistance() == 11 || 
                path.getTotalDistance() == 13);
    }

    @Test
    void testFindPath_NoPath() {
        edges.put(7, new ArrayList<>());
        Response path = bfsAlgorithm.findPath(0, 7, edges);
        assertNull(path);
    }

    @Test
    void testFindPath_StartEqualsEnd() {
        Response path = bfsAlgorithm.findPath(1, 1, edges);
        assertNotNull(path.getPath());
        assertEquals(List.of(1), path.getPath());
    }

    @Test
    void testFindPath_LongerPath() {
        Response path = bfsAlgorithm.findPath(1, 5, edges);
        assertNotNull(path);
        assertEquals(1, path.getPath().get(0));
        assertEquals(5, path.getPath().get(path.getPath().size() - 1));
        assertEquals(List.of(1, 2, 5), path.getPath());
    }

    @Test
    void testFindPath_GraphWithDisconnectedComponents() {
        edges.computeIfAbsent(10, k -> new ArrayList<>()).add(new Edge(11, 1.0, "r", "a", false, false, null, "y", 10.0));
        edges.computeIfAbsent(11, k -> new ArrayList<>()).add(new Edge(10, 1.0, "r", "a", false, false, null, "y", 10.0));

        Response path = bfsAlgorithm.findPath(0, 10, edges);
        assertNull(path);

        Response pathWithinComponent = bfsAlgorithm.findPath(10, 11, edges);
        assertNotNull(pathWithinComponent);
        assertEquals(List.of(10, 11), pathWithinComponent.getPath());
    }

    @Test
    void testFindPath_NoEdgesForStartNode() {
        edges.put(99, new ArrayList<>());
        Response path = bfsAlgorithm.findPath(99, 0, edges);
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