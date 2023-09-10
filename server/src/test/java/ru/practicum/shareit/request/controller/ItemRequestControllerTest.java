package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Constant;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService requestService;

    private User user;
    private ItemRequestResponseDto itemRequest;

    @BeforeEach
    void init() {
        user = new User();
        user.setId(1L);
        user.setName("username");
        user.setEmail("email@email.com");

        itemRequest = new ItemRequestResponseDto();
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(List.of());
    }

    @Test
    void createRequest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("description");
        when(requestService.addNewRequest(eq(requestDto), anyLong())).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getUserRequests() throws Exception {
        List<ItemRequestResponseDto> responseDtos = new ArrayList<>();

        ItemRequestResponseDto responseDto1 = new ItemRequestResponseDto();
        responseDto1.setId(1L);
        responseDto1.setDescription("Request 1 Description");
        responseDtos.add(responseDto1);

        ItemRequestResponseDto responseDto2 = new ItemRequestResponseDto();
        responseDto2.setId(2L);
        responseDto2.setDescription("Request 2 Description");
        responseDtos.add(responseDto2);

        when(requestService.getUserRequests(anyLong())).thenReturn(responseDtos);

        mockMvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Request 1 Description"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Request 2 Description"));
    }

    @Test
    void getAllRequests() throws Exception {
        List<ItemRequestResponseDto> responseDtos = new ArrayList<>();

        ItemRequestResponseDto responseDto1 = new ItemRequestResponseDto();
        responseDto1.setId(1L);
        responseDto1.setDescription("Request 1 Description");
        responseDtos.add(responseDto1);

        ItemRequestResponseDto responseDto2 = new ItemRequestResponseDto();
        responseDto2.setId(2L);
        responseDto2.setDescription("Request 2 Description");
        responseDtos.add(responseDto2);

        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(responseDtos);

        mockMvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("from", "0")
                        .param("size", "100")
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Request 1 Description"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("Request 2 Description"));
    }

    @Test
    void getRequestById() throws Exception {
        ItemRequestResponseDto responseDto = new ItemRequestResponseDto();
        responseDto.setId(1L);
        responseDto.setDescription("Request Description");

        when(requestService.getRequestById(anyLong(), anyLong())).thenReturn(responseDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Request Description"));
    }

}