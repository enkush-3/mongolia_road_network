package algorithm.mongolia_road_network.service.Algorithm;

import algorithm.mongolia_road_network.model.Edge;
import algorithm.mongolia_road_network.model.Response;

import java.util.List;
import java.util.Map;

public abstract class Algorithm {
    public abstract Response findPath(int startId, int endId, Map<Integer, List<Edge>> edges);
}
