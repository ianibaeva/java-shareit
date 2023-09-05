package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.util.Constant;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    private BookingOutDto bookingOutDto;
    private BookItemRequestDto bookItemRequestDto;

    @BeforeEach
    void init() {
        bookingOutDto = new BookingOutDto();
        bookingOutDto.setId(1L);
        bookingOutDto.setStart(LocalDateTime.of(2023, 12, 12, 10, 0));
        bookingOutDto.setEnd(LocalDateTime.of(2023, 12, 20, 10, 0));
        bookingOutDto.setStatus(Status.WAITING);
        bookingOutDto.setBooker(new BookingOutDto.User(2L, "testName"));
        bookingOutDto.setItem(new BookingOutDto.Item(1L, "testName"));

        bookItemRequestDto = new BookItemRequestDto();
        bookItemRequestDto.setItemId(1L);
        bookItemRequestDto.setStart(LocalDateTime.of(2023, 12, 12, 10, 0));
        bookItemRequestDto.setEnd(LocalDateTime.of(2023, 12, 20, 10, 0));
    }

    @Test
    void createBooking_whenValidDates_ReturnsStatusOk() throws Exception {
        when(bookingService.create(anyLong(), any(BookItemRequestDto.class)))
                .thenReturn(bookingOutDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(bookingOutDto.getId()));
    }

    @Test
    void createBooking_whenInvalidDates_ReturnsBadRequest() throws Exception {
        bookItemRequestDto.setStart(LocalDateTime.now().plusDays(5));
        bookItemRequestDto.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBooking_whenValidData_ReturnsStatusOk() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingOutDto);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(bookingOutDto.getId()));
    }

    @Test
    void updateBooking_whenInvalidData_ReturnsInternalServerError() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingOutDto);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "invalid")
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getByIdWith_whenValidBookingId_ReturnsStatusOk() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingOutDto);

        mockMvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(bookingOutDto.getId()));
    }

    @Test
    void getAllBookings_ReturnsStatusOk() throws Exception {
        List<BookingOutDto> bookingList = new ArrayList<>();
        bookingList.add(bookingOutDto);

        when(bookingService.getAllByBooker(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(bookingList);

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(bookingList.size()));
    }

    @Test
    void getAllBookings_whenInvalidStatus_ThrowsIllegalArgumentException() throws Exception {
        doThrow(new IllegalArgumentException("Page parameters must be non-negative"))
                .when(bookingService).getAllByBooker(anyLong(), eq("INVALID_STATUS"), anyInt(), anyInt());

        mockMvc.perform(get("/bookings")
                        .param("state", "INVALID_STATUS")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Page parameters must be non-negative"));
    }

    @Test
    void getAllBookingsByOwner_ReturnsStatusOk() throws Exception {
        List<BookingOutDto> bookingList = new ArrayList<>();
        bookingList.add(bookingOutDto);

        when(bookingService.getAllByOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(bookingList);

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(bookingList.size()));
    }

    @Test
    void getAllBookingsByOwner_whenInvalidStatus_ThrowsIllegalArgumentException() throws Exception {
        doThrow(new IllegalArgumentException("Page parameters must be non-negative"))
                .when(bookingService).getAllByOwner(anyLong(), eq("INVALID_STATUS"), anyInt(), anyInt());

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "INVALID_STATUS")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(Constant.REQUEST_HEADER_USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Page parameters must be non-negative"));
    }
}



