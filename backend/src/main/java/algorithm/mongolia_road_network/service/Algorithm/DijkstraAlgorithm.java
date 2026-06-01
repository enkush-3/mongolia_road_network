package algorithm.mongolia_road_network.service.Algorithm;

import algorithm.mongolia_road_network.model.Edge;
import algorithm.mongolia_road_network.model.Response;

import java.util.*;

public class DijkstraAlgorithm extends Algorithm {

    @Override
    public Response findPath(int startId, int endId, Map<Integer, List<Edge>> edges) {
        if (!edges.containsKey(startId)) {
            return null;
        }

        PriorityQueue<Map.Entry<Integer, Double>> pq = new PriorityQueue<>(Map.Entry.comparingByValue());
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> predecessors = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        for (Integer nodeId : edges.keySet())
            distances.put(nodeId, Double.MAX_VALUE);

        distances.put(startId, 0.0);
        pq.add(new AbstractMap.SimpleEntry<>(startId, 0.0));

        while (!pq.isEmpty()) {
            int u = pq.poll().getKey();
            if (visited.contains(u)) continue;
            visited.add(u);

            if (u == endId) break;

            if (edges.get(u) == null) continue;

            for (Edge edge : edges.get(u)) {
                int v = edge.getDestination();
                double weight = edge.getWeight();
                double distThroughU = distances.get(u) + weight;

                if (!distances.containsKey(v))
                    distances.put(v, Double.MAX_VALUE);

                if (distThroughU < distances.get(v)) {
                    distances.put(v, distThroughU);
                    predecessors.put(v, u);
                    pq.add(new AbstractMap.SimpleEntry<>(v, distThroughU));
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        Integer at = endId;

        while (at != null) {
            path.add(at);
            at = predecessors.get(at);
        }

        Collections.reverse(path);
        
        double pathDistance = 0.0;
        if (path.size() > 1) {
            for (int i = 0; i < path.size() - 1; i++) {
                int from = path.get(i);
                int to = path.get(i + 1);
                List<Edge> edgesFrom = edges.get(from);
                for (Edge edge : edgesFrom) {
                    if (edge.getDestination() == to) {
                        pathDistance += edge.getWeight();
                        break;
                    }
                }
            }
        }

        return (path.size() > 0 && path.get(0).equals(startId)) ? new Response(path, pathDistance) : null;
    }
}