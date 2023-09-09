package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Constant;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem_whenItemIsValid_ReturnsStatusOk() throws Exception {
        ItemResponseDto itemDtoToCreate = new ItemResponseDto();
        itemDtoToCreate.setDescription("some item description");
        itemDtoToCreate.setName("some item name");
        itemDtoToCreate.setAvailable(true);
        itemDtoToCreate.setRequestId(null);

        when(itemService.addItem(any(ItemDtoOut.class), anyLong()))
                .thenReturn(itemDtoToCreate);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDtoToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(IsNull.nullValue()));
    }

    @Test
    void update_whenItemIsValid_ReturnsStatusIsOk() throws Exception {
        Long itemId = 0L;
        ItemResponseDto itemDtoToCreate = new ItemResponseDto();
        itemDtoToCreate.setId(itemId);
        itemDtoToCreate.setDescription("some item description");
        itemDtoToCreate.setName("some item name");
        itemDtoToCreate.setAvailable(true);

        when(itemService.updateItem(anyLong(), any(ItemDtoOut.class), anyLong()))
                .thenReturn(itemDtoToCreate);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemDtoToCreate.getId()));
    }

    @Test
    void get_ReturnsStatusOk() throws Exception {
        Long itemId = 0L;
        ItemResponseDto itemDtoToCreate = new ItemResponseDto();
        itemDtoToCreate.setId(itemId);
        itemDtoToCreate.setDescription("");
        itemDtoToCreate.setName("");
        itemDtoToCreate.setAvailable(true);

        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDtoToCreate);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemDtoToCreate.getId()));
    }

    @Test
    void getAll_ReturnsStatusOk() throws Exception {
        Long userId = 0L;
        List<ItemResponseDto> itemsDtoToExpect = List.of(new ItemResponseDto());

        when(itemService.getAllUserItems(anyLong()))
                .thenReturn(itemsDtoToExpect);

        mockMvc.perform(MockMvcRequestBuilders.get("/items", userId)
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(itemsDtoToExpect.size()));
    }

    @Test
    void searchItems_ReturnsStatusOk() throws Exception {
        Long userId = 0L;
        Integer from = 0;
        Integer size = 10;
        String text = "find";
        List<ItemResponseDto> itemsDtoToExpect = List.of(new ItemResponseDto());

        when(itemService.getAvailableItemBySearch(anyString(), anyLong())).thenReturn(itemsDtoToExpect);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search", from, size)
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, userId)
                        .param("text", text)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void createComment_whenCommentIsValid_ReturnsStatusIsOk() throws Exception {
        Long itemId = 0L;
        Long userId = 0L;
        CommentRequestDto commentToAdd = new CommentRequestDto();
        commentToAdd.setText("some comment");

        itemService.createComment(userId, commentToAdd, itemId);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", itemId)
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(commentToAdd))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}