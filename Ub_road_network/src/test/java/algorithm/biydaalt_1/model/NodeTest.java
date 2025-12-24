package algorithm.biydaalt_1.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NodeTest {

    @Test
    void testNodeInitializationAndGetters() {
        Node node = new Node(1, 47.921, 106.918);
        assertEquals(1, node.getId());
        assertEquals(47.921, node.getLat(), 0.000001);
        assertEquals(106.918, node.getLon(), 0.000001);
    }

    @Test
    void testDifferentNodesNotEqualByReference() {
        Node node1 = new Node(1, 47.0, 106.0);
        Node node2 = new Node(1, 47.0, 106.0);
        assertNotSame(node1, node2);
    }

    @Test
    void testFieldsAreFinal() throws NoSuchFieldException {
        var idField = Node.class.getDeclaredField("id");
        var latField = Node.class.getDeclaredField("lat");
        var lonField = Node.class.getDeclaredField("lon");

        assertTrue(java.lang.reflect.Modifier.isFinal(idField.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isFinal(latField.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isFinal(lonField.getModifiers()));
    }
}
