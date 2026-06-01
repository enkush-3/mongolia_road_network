package algorithm.mongolia_road_network.model;

import java.util.List;

public class Response {
    private List<Integer> path;
    private double totalDistance;
    public Response(List<Integer> path, double totalDistance) {
        this.path = path;
        this.totalDistance = totalDistance;
    }

    public List<Integer> getPath() {
        return path;
    }
    public double getTotalDistance() {
        return totalDistance;
    }

    public void setPath(List<Integer> path) {
        this.path = path;
    }
    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }
}
