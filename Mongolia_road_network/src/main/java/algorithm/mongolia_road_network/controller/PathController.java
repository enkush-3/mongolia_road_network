package algorithm.biydaalt_1.controller;

import algorithm.biydaalt_1.model.PathResponse;
import algorithm.biydaalt_1.service.GraphService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class PathController {

    private final GraphService graphService;

    public PathController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/path")
    public ResponseEntity<?> getPath(
            @RequestParam double start_lat,
            @RequestParam double start_lon,
            @RequestParam double end_lat,
            @RequestParam double end_lon,
            @RequestParam(defaultValue = "dijkstra") String algo) {

        int startId = graphService.findNearestNode(start_lat, start_lon);
        int endId = graphService.findNearestNode(end_lat, end_lon);

        if (startId == -1 || endId == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Nearest node not found or map not initialized properly."));
        }

        List<Integer> pathNodeIds;
        try {
            pathNodeIds = graphService.findPath(startId, endId, algo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }

        if (pathNodeIds == null || pathNodeIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Path not found"));
        }

        List<PathResponse> pathCoords = pathNodeIds.stream()
                .map(graphService::getNodeById)
                .map(node -> new PathResponse(node.getLat(), node.getLon()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("path", pathCoords));
    }
}