package algorithm.biydaalt_1.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PathResponseTest {

    @Test
    void testConstructorAndGetters() {
        PathResponse path = new PathResponse(47.921, 106.918);

        assertEquals(47.921, path.getLat(), 0.000001);
        assertEquals(106.918, path.getLng(), 0.000001);
    }

    @Test
    void testSettersChangeValues() {
        PathResponse path = new PathResponse(0.0, 0.0);

        path.setLat(48.0);
        path.setLng(107.0);

        assertEquals(48.0, path.getLat(), 0.000001);
        assertEquals(107.0, path.getLng(), 0.000001);
    }

    @Test
    void testMultipleObjectsIndependence() {
        PathResponse path1 = new PathResponse(47.0, 106.0);
        PathResponse path2 = new PathResponse(48.0, 107.0);

        assertNotEquals(path1.getLat(), path2.getLat());
        assertNotEquals(path1.getLng(), path2.getLng());
    }
}
