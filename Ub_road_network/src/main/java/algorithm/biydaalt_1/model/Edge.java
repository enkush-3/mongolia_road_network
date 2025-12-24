package algorithm.biydaalt_1.model;

public class Edge {
    private int destination;
    private double weight;
    private String fclass;
    private String surface;
    private boolean bridge;
    private boolean tunnel;
    private String turnRestriction;
    private String access;
    private Double maxspeedMps;

    public Edge(int destination,
                double weight,
                String fclass,
                String surface,
                boolean bridge,
                boolean tunnel,
                String turnRestriction,
                String access,
                Double maxspeedMps) {
        this.destination = destination;
        this.weight = weight;
        this.fclass = fclass;
        this.surface = surface;
        this.bridge = bridge;
        this.tunnel = tunnel;
        this.turnRestriction = turnRestriction;
        this.access = access;
        this.maxspeedMps = maxspeedMps;
    }

    public int getDestination() {
        return destination;
    }

    public double getWeight() {
        return weight;
    }

    public String getFclass() {
        return fclass;
    }

    public void setFclass(String fclass) {
        this.fclass = fclass;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public boolean isBridge() {
        return bridge;
    }

    public void setBridge(boolean bridge) {
        this.bridge = bridge;
    }

    public boolean isTunnel() {
        return tunnel;
    }

    public void setTunnel(boolean tunnel) {
        this.tunnel = tunnel;
    }

    public String getTurnRestriction() {
        return turnRestriction;
    }

    public void setTurnRestriction(String turnRestriction) {
        this.turnRestriction = turnRestriction;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public Double getMaxspeedMps() {
        return maxspeedMps;
    }

    public void setMaxspeedMps(Double maxspeedMps) {
        this.maxspeedMps = maxspeedMps;
    }

}