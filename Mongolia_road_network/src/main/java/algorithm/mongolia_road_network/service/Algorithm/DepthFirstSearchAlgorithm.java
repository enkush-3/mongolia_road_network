package algorithm.biydaalt_1.service.Algorithm;

import algorithm.biydaalt_1.model.Edge;
import org.opengis.filter.temporal.BinaryTemporalOperator;

import java.util.*;

public class DepthFirstSearchAlgorithm extends Algorithm {

    @Override
    public List<Integer> findPath(int startId, int endId, Map<Integer, List<Edge>> edges) {
        Stack<Integer> stack = new Stack<>();
        Map<Integer, Integer> shortpath = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        stack.push(startId);
        visited.add(startId);

        boolean pathFound = false;

        while(!stack.isEmpty()){
            int u = stack.pop();
            if(u == endId)
            {
                pathFound = true;
                break;
            }
            if(edges.get(u) == null)
                continue;

            for(Edge edge: edges.get(u)){
                int v = edge.getDestination();

                if(!visited.contains(v))
                {
                    visited.add(v);
                    stack.push(v);
                    shortpath.put(v, u);
                }
            }
        }

        if(!pathFound)
            return null;

        List<Integer> listPath = new ArrayList<>();
        Integer it = endId;

        while (it != null){
            listPath.add(it);
            it = shortpath.get(it);
        }
        Collections.reverse(listPath);

        return (listPath.size() > 0 && listPath.get(0).equals(startId)) ? listPath : null;
    }

}