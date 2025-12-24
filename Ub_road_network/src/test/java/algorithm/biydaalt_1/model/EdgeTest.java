package algorithm.biydaalt_1.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    @Test
    void testEdgeCreationAndGetters() {
        Edge edge = new Edge(
                5,
                100.0,
                "primary",
                "asphalt",
                true,
                false,
                "no_left_turn",
                "yes",
                13.9
        );

        assertEquals(5, edge.getDestination());
        assertEquals(100.0, edge.getWeight());
        assertEquals("primary", edge.getFclass());
        assertEquals("asphalt", edge.getSurface());
        assertTrue(edge.isBridge());
        assertFalse(edge.isTunnel());
        assertEquals("no_left_turn", edge.getTurnRestriction());
        assertEquals("yes", edge.getAccess());
        assertEquals(13.9, edge.getMaxspeedMps());
    }

    @Test
    void testEdgeSetters() {
        Edge edge = new Edge(1, 50.0, "secondary", "gravel", false, false, null, null, null);

        edge.setFclass("primary");
        edge.setSurface("asphalt");
        edge.setBridge(true);
        edge.setTunnel(true);
        edge.setTurnRestriction("no_u_turn");
        edge.setAccess("private");
        edge.setMaxspeedMps(11.1);

        assertEquals("primary", edge.getFclass());
        assertEquals("asphalt", edge.getSurface());
        assertTrue(edge.isBridge());
        assertTrue(edge.isTunnel());
        assertEquals("no_u_turn", edge.getTurnRestriction());
        assertEquals("private", edge.getAccess());
        assertEquals(11.1, edge.getMaxspeedMps());
    }
}
