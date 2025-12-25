package algorithm.mongolia_road_network.service.Algorithm;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import algorithm.mongolia_road_network.model.Edge;
import algorithm.mongolia_road_network.model.Node;
import algorithm.mongolia_road_network.model.Response;

public class AStarAlgorithmTests extends AlgorithmTests {

    private AStarAlgorithm aStarAlgorithm;
    private Map<Integer, Node> idToNode;

    @BeforeEach
    void initAStar() {
        idToNode = new HashMap<>();

        idToNode.put(1, new Node(1, 0.0, 0.0));
        idToNode.put(2, new Node(2, 0.0, 1.0));
        idToNode.put(3, new Node(3, 1.0, 0.0));
        idToNode.put(4, new Node(4, 1.0, 1.0));
        idToNode.put(5, new Node(5, 2.0, 2.0));

        idToNode.put(0, new Node(0, -1.0, -1.0));
        idToNode.put(7, new Node(7, 5.0, 5.0));
        idToNode.put(10, new Node(10, 10.0, 10.0));
        idToNode.put(11, new Node(11, 10.0, 11.0));
        idToNode.put(99, new Node(99, 99.0, 99.0));

        aStarAlgorithm = new AStarAlgorithm(idToNode);
    }

    @Test
    void testFindPath_SimplePath() {
        Response path = aStarAlgorithm.findPath(1, 5, edges);
        assertNotNull(path);
        assertEquals(List.of(1, 3, 4, 5), path.getPath());
        assertEquals(11, path.getTotalDistance());
    }

    @Test
    void testFindPath_NoPath() {
        edges.put(7, new ArrayList<>());
        Response path = aStarAlgorithm.findPath(0, 7, edges);
        assertNull(path);
    }

    @Test
    void testFindPath_StartEqualsEnd() {
        Response response = aStarAlgorithm.findPath(1, 1, edges);
        assertNotNull(response);
        assertEquals(List.of(1), response.getPath());
        assertEquals(0.0, response.getTotalDistance());
    }

    @Test
    void testFindPath_ThroughIntermediateNodes() {
        Response path = aStarAlgorithm.findPath(1, 2, edges);
        assertNotNull(path);
        assertEquals(List.of(1, 2), path.getPath());
        assertEquals(2.0, path.getTotalDistance());
    }

    @Test
    void testFindPath_GraphWithDisconnectedComponents() {
        edges.computeIfAbsent(10, k -> new ArrayList<>())
                .add(new Edge(11, 1.0, "r", "a", false, false, null, "y", 10.0));

        edges.computeIfAbsent(11, k -> new ArrayList<>())
                .add(new Edge(10, 1.0, "r", "a", false, false, null, "y", 10.0));

        Response path = aStarAlgorithm.findPath(0, 10, edges);
        assertNull(path);

        Response pathWithinComponent = aStarAlgorithm.findPath(10, 11, edges);
        assertNotNull(pathWithinComponent);
        assertEquals(List.of(10, 11), pathWithinComponent.getPath());
        assertEquals(1.0, pathWithinComponent.getTotalDistance());
    }

    @Test
    void testFindPath_NoEdgesForStartNode() {
        edges.put(99, new ArrayList<>());
        Response path = aStarAlgorithm.findPath(99, 0, edges);
        assertNull(path);
    }

    @Test
    void testFindPath_EndNodeHasNoIncomingEdges() {
        Response path = aStarAlgorithm.findPath(1, 5, edges);
        assertNotNull(path);
        assertEquals(5, path.getPath().get(path.getPath().size() - 1));
    }
}
