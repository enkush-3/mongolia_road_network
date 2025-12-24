package algorithm.biydaalt_1.Controller;

import algorithm.biydaalt_1.controller.PathController;
import algorithm.biydaalt_1.model.Node;
import algorithm.biydaalt_1.model.PathResponse;
import algorithm.biydaalt_1.service.GraphService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PathController.class)
class PathControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GraphService graphService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetPath_DijkstraSuccess() throws Exception {
        double startLat = 40.0;
        double startLon = -74.0;
        double endLat = 41.0;
        double endLon = -75.0;

        int startId = 1;
        int endId = 2;
        List<Integer> pathNodeIds = List.of(startId, 3, endId);
        List<PathResponse> expectedPathCoords = List.of(
                new PathResponse(40.0, -74.0),
                new PathResponse(40.5, -74.5),
                new PathResponse(41.0, -75.0)
        );

        when(graphService.findNearestNode(startLat, startLon)).thenReturn(startId);
        when(graphService.findNearestNode(endLat, endLon)).thenReturn(endId);
        when(graphService.findPath(startId, endId, "dijkstra")).thenReturn(pathNodeIds);
        when(graphService.getNodeById(startId)).thenReturn(new Node(startId, 40.0, -74.0));
        when(graphService.getNodeById(3)).thenReturn(new Node(3, 40.5, -74.5));
        when(graphService.getNodeById(endId)).thenReturn(new Node(endId, 41.0, -75.0));

        mockMvc.perform(get("/path")
                        .param("start_lat", String.valueOf(startLat))
                        .param("start_lon", String.valueOf(startLon))
                        .param("end_lat", String.valueOf(endLat))
                        .param("end_lon", String.valueOf(endLon))
                        .param("algo", "dijkstra")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path[0].lat").value(40.0))
                .andExpect(jsonPath("$.path[0].lng").value(-74.0))
                .andExpect(jsonPath("$.path[1].lat").value(40.5))
                .andExpect(jsonPath("$.path[1].lng").value(-74.5))
                .andExpect(jsonPath("$.path[2].lat").value(41.0))
                .andExpect(jsonPath("$.path[2].lng").value(-75.0))
                .andExpect(jsonPath("$.path.length()").value(3));
    }

    @Test
    void testGetPath_NearestNodeNotFound() throws Exception {
        double startLat = 40.0;
        double startLon = -74.0;
        double endLat = 41.0;
        double endLon = -75.0;

        when(graphService.findNearestNode(startLat, startLon)).thenReturn(-1);

        mockMvc.perform(get("/path")
                        .param("start_lat", String.valueOf(startLat))
                        .param("start_lon", String.valueOf(startLon))
                        .param("end_lat", String.valueOf(endLat))
                        .param("end_lon", String.valueOf(endLon))
                        .param("algo", "dijkstra")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Nearest node not found or map not initialized properly."));
    }

    @Test
    void testGetPath_PathNotFound() throws Exception {
        double startLat = 40.0;
        double startLon = -74.0;
        double endLat = 41.0;
        double endLon = -75.0;

        int startId = 1;
        int endId = 2;

        when(graphService.findNearestNode(startLat, startLon)).thenReturn(startId);
        when(graphService.findNearestNode(endLat, endLon)).thenReturn(endId);
        when(graphService.findPath(startId, endId, "dijkstra")).thenReturn(null);

        mockMvc.perform(get("/path")
                        .param("start_lat", String.valueOf(startLat))
                        .param("start_lon", String.valueOf(startLon))
                        .param("end_lat", String.valueOf(endLat))
                        .param("end_lon", String.valueOf(endLon))
                        .param("algo", "dijkstra")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Path not found"));
    }

    @Test
    void testGetPath_EmptyPathFound() throws Exception {
        double startLat = 40.0;
        double startLon = -74.0;
        double endLat = 41.0;
        double endLon = -75.0;

        int startId = 1;
        int endId = 2;

        when(graphService.findNearestNode(startLat, startLon)).thenReturn(startId);
        when(graphService.findNearestNode(endLat, endLon)).thenReturn(endId);
        when(graphService.findPath(startId, endId, "dijkstra")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/path")
                        .param("start_lat", String.valueOf(startLat))
                        .param("start_lon", String.valueOf(startLon))
                        .param("end_lat", String.valueOf(endLon))
                        .param("end_lon", String.valueOf(endLon))
                        .param("algo", "dijkstra")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Path not found"));
    }

    @Test
    void testGetPath_InvalidAlgorithm() throws Exception {
        double startLat = 40.0;
        double startLon = -74.0;
        double endLat = 41.0;
        double endLon = -75.0;

        int startId = 1;
        int endId = 2;

        when(graphService.findNearestNode(startLat, startLon)).thenReturn(startId);
        when(graphService.findNearestNode(endLat, endLon)).thenReturn(endId);
        when(graphService.findPath(startId, endId, "invalidAlgo"))
                .thenThrow(new IllegalArgumentException("Unknown algorithm: invalidAlgo"));

        mockMvc.perform(get("/path")
                        .param("start_lat", String.valueOf(startLat))
                        .param("start_lon", String.valueOf(startLon))
                        .param("end_lat", String.valueOf(endLat))
                        .param("end_lon", String.valueOf(endLon))
                        .param("algo", "invalidAlgo")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Unknown algorithm: invalidAlgo"));
    }

    @Test
    void testGetPath_DefaultAlgorithm() throws Exception {
        double startLat = 40.0;
        double startLon = -74.0;
        double endLat = 41.0;
        double endLon = -75.0;

        int startId = 1;
        int endId = 2;
        List<Integer> pathNodeIds = List.of(startId, endId);

        when(graphService.findNearestNode(startLat, startLon)).thenReturn(startId);
        when(graphService.findNearestNode(endLat, endLon)).thenReturn(endId);
        when(graphService.findPath(startId, endId, "dijkstra")).thenReturn(pathNodeIds);
        when(graphService.getNodeById(startId)).thenReturn(new Node(startId, 40.0, -74.0));
        when(graphService.getNodeById(endId)).thenReturn(new Node(endId, 41.0, -75.0));


        mockMvc.perform(get("/path")
                        .param("start_lat", String.valueOf(startLat))
                        .param("start_lon", String.valueOf(startLon))
                        .param("end_lat", String.valueOf(endLat))
                        .param("end_lon", String.valueOf(endLon))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path.length()").value(2));
    }
}