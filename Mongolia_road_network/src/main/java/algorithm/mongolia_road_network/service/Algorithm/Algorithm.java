package algorithm.biydaalt_1.service.Algorithm;

import algorithm.biydaalt_1.model.Edge;

import java.util.List;
import java.util.Map;

public abstract class Algorithm {
    public abstract List<Integer> findPath(int startId, int endId, Map<Integer, List<Edge>> edges);
}
