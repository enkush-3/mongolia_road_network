package algorithm.biydaalt_1.service.Algorithm;

import algorithm.biydaalt_1.model.Edge;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AlgorithmTests {

    protected Map<Integer, List<Edge>> edges;

    protected void addEdge(int from, int to, double weight, boolean bidirectional) {
        edges.computeIfAbsent(from, k -> new ArrayList<>())
                .add(new Edge(to, weight, "residential", "asphalt", false, false, null, "yes", 10.0));
        if (bidirectional) {
            edges.computeIfAbsent(to, k -> new ArrayList<>())
                    .add(new Edge(from, weight, "residential", "asphalt", false, false, null, "yes", 10.0));
        }
    }

    @BeforeEach
    void setUp() {
        edges = new HashMap<>();

        addEdge(1, 2, 2.0, true);
        addEdge(1, 3, 1.0, true);

        addEdge(2, 4, 3.0, true);
        addEdge(2, 5, 4.0, true);

        addEdge(3, 4, 5.0, true);

        addEdge(4, 2, 3.0, true);
        addEdge(4, 5, 5.0, true);

        addEdge(5, 4, 5.0, true);
        addEdge(5, 2, 4.0, true);
    }
}