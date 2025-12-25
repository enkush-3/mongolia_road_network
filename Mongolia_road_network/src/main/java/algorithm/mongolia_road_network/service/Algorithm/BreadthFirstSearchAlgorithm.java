package algorithm.mongolia_road_network.service.Algorithm;

import algorithm.mongolia_road_network.model.Edge;
import algorithm.mongolia_road_network.model.Response;

import java.util.*;

public class BreadthFirstSearchAlgorithm extends Algorithm {

    @Override
    public Response findPath(int startId, int endId, Map<Integer, List<Edge>> edges) {
        if (!edges.containsKey(startId)) {
            return null;
        }
        Queue<Integer> queue = new ArrayDeque<>();
        Map<Integer, Integer> parentMap = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(startId);
        visited.add(startId);

        boolean pathFound = false;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            if (u == endId) {
                pathFound = true;
                break;
            }

            if (edges.get(u) == null)
                continue;

            for (Edge edge : edges.get(u)) {
                int v = edge.getDestination();
                if (!visited.contains(v)) {
                    visited.add(v);
                    parentMap.put(v, u);
                    queue.add(v);
                }
            }
        }

        if (!pathFound) {
            return null;
        }

        List<Integer> path = new ArrayList<>();
        Integer at = endId;
        while (at != null) {
            path.add(at);
            at = parentMap.get(at);
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
        return path.size() > 0 && path.get(0).equals(startId) ? new Response(path, pathDistance) : null;
    }
}