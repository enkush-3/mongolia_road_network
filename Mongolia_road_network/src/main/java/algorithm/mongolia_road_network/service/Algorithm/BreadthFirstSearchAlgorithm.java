package algorithm.biydaalt_1.service.Algorithm;

import algorithm.biydaalt_1.model.Edge;
import java.util.*;

public class BreadthFirstSearchAlgorithm extends Algorithm {

    @Override
    public List<Integer> findPath(int startId, int endId, Map<Integer, List<Edge>> edges) {
        Queue<Integer> queue = new ArrayDeque<>();
        Map<Integer, Integer> shortPath = new HashMap<>();
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
                if (!visited.contains(v)){
                    visited.add(v);
                    queue.add(v);
                    shortPath.put(v, u);
                }
            }
        }
        if(!pathFound)
            return null;

        List<Integer> listPath = new ArrayList<>();
        Integer it = endId;
        while (it != null) {
            listPath.add(it);
            it = shortPath.get(it);
        }
        Collections.reverse(listPath);
        return (listPath.size() > 0 && listPath.get(0).equals(startId)) ? listPath : null;
    }
}