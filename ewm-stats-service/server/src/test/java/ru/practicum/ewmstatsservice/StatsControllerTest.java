package ru.practicum.ewmstatsservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.EndpointHitDto;
import dto.ViewStatsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewmstatsservice.controller.StatsController;
import ru.practicum.ewmstatsservice.service.StatsService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = StatsController.class)
public class StatsControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    StatsService statsService;

    @Autowired
    private MockMvc mvc;

    EndpointHitDto endpointHitDto = EndpointHitDto.builder()
            .id(1L)
            .app("ewm-main-service")
            .uri("/events")
            .ip("")
            .build();

    ViewStatsDto viewStatsDto = ViewStatsDto.builder()
            .app("ewm-main-service")
            .uri("/events")
            .hits(1L)
            .build();

   List<String> list = new ArrayList<>();

    @Test
    void testCreateHit() throws Exception {
        when(statsService.create(any()))
                .thenReturn(endpointHitDto);

        mvc.perform(post("/hit")
                        .content(mapper.writeValueAsString(endpointHitDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(endpointHitDto.getId()), Long.class))
                .andExpect(jsonPath("$.app", is(endpointHitDto.getApp()), String.class))
                .andExpect(jsonPath("$.uri", is(endpointHitDto.getUri()), String.class))
                .andExpect(jsonPath("$.ip", is(endpointHitDto.getIp()), String.class));
    }
}
