package algorithm.mongolia_road_network.model;

public class Node {
    private final int id;
    private final double lat;
    private final double lon;

    public Node(int id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}