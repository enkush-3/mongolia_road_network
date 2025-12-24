package algorithm.biydaalt_1.service.Algorithm;

import algorithm.biydaalt_1.model.Edge;

import java.util.*;

public class DijkstraAlgorithm extends Algorithm{

    @Override
    public List<Integer> findPath(int startId, int endId, Map<Integer, List<Edge>> edges){
        PriorityQueue<Map.Entry<Integer, Double>> queue = new PriorityQueue<>(Map.Entry.comparingByValue());
        Map<Integer, Integer> shortPath = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Double> weightPath = new HashMap<>();

        for(Integer node: edges.keySet())
            weightPath.put(node, Double.MAX_VALUE);

        weightPath.put(startId, 0.0);
        visited.add(startId);

        queue.add(new AbstractMap.SimpleEntry<>(startId, 0.0));

        while(!queue.isEmpty()){
            int u = queue.poll().getKey();
            if(u == endId)
                break;

            if(edges.get(u) == null)
                continue;

            for(Edge edge: edges.get(u)){
                int v = edge.getDestination();
                double weight = edge.getWeight();
                double sumWeight = weight + weightPath.get(u);

                if(!weightPath.containsKey(v))
                    weightPath.put(v, Double.MAX_VALUE);

                if(sumWeight < weightPath.get(v)) {
                    shortPath.put(v, u);
                    queue.add(new AbstractMap.SimpleEntry<>(v, sumWeight));
                    weightPath.put(v, sumWeight);
                }
            }
        }

        List<Integer> listPath = new ArrayList<>();
        Integer it = endId;

        while(it != null){
            listPath.add(it);
            it = shortPath.get(it);
        }

        Collections.reverse(listPath);

        return listPath.size() > 0 && listPath.get(0).equals(startId) ? listPath : null;
    }
}