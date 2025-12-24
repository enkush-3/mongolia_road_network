package algorithm.biydaalt_1.service.AlgorithmMe;

import algorithm.biydaalt_1.model.Edge;
import algorithm.biydaalt_1.service.Algorithm.Algorithm;

import java.util.*;

public class DepthFirstSearchAlgorithm extends Algorithm {

    @Override
    public List<Integer> findPath(int startId, int endId, Map<Integer, List<Edge>> edges) {
        Stack<Integer> stack = new Stack<>();
        Map<Integer, Integer> parentMap = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        stack.push(startId);
        visited.add(startId);

        boolean pathFound = false;

        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (u == endId) {
                pathFound = true;
                break;
            }

            if (edges.get(u) == null) continue;

            List<Edge> neighbors = edges.get(u);
            if (neighbors != null) {
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    Edge edge = neighbors.get(i);
                    int v = edge.getDestination();
                    if (!visited.contains(v)) {
                        visited.add(v);
                        parentMap.put(v, u);
                        stack.push(v);
                    }
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
        System.out.printf("Path distance: %.3f km\n", pathDistance / 1000.0);
        return path.size() > 0 && path.get(0).equals(startId) ? path : null;
    }
}