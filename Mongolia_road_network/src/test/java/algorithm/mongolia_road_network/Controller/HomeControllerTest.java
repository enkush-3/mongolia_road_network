package algorithm.biydaalt_1.Controller;

import algorithm.biydaalt_1.controller.HomeController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Test
    public void indexTest() {
        HomeController homeTest = new HomeController();
        String result = homeTest.index();
        assert (result.equals("index"));
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getIndex_shouldReturnStatusOkAndIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Autowired
    private HomeController controller;

    @Test
    void controllerShouldBeLoaded() {
        assertThat(controller).isNotNull();
    }

}
