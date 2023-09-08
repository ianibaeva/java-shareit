package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Constant;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRepository itemRepository;

    private User user;
    private User user2;
    private ItemRequestResponseDto addItemRequestDto;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private Item item;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setId(1L);
        user.setName("User_Name");
        user.setEmail("user@test.testz");

        user2 = new User();
        user2.setId(2L);
        user2.setName("UserAnoter_Name");
        user2.setEmail("userAnother@test.testz");

        item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item description");
        item.setOwner(user);
        item.setAvailable(true);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Description");
        itemRequest.setRequestorId(user.getId());
        itemRequest.setCreated(LocalDateTime.now());

        addItemRequestDto = new ItemRequestResponseDto();
        addItemRequestDto.setId(itemRequest.getId());
        addItemRequestDto.setId(itemRequest.getRequestorId());
        addItemRequestDto.setDescription("Description");
        addItemRequestDto.setCreated(itemRequest.getCreated());
        addItemRequestDto.setItems(List.of());

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription("Description");
    }

    @Test
    public void createRequestTest() {
        Long userId = user.getId();
        when(userRepository.countById(userId)).thenReturn(1L);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        ItemRequestDto actualNewRequest = itemRequestService.addNewRequest(userId, itemRequestDto);

        assertNotNull(actualNewRequest);
        assertEquals(addItemRequestDto.getId(), actualNewRequest.getId());
        assertEquals(addItemRequestDto.getDescription(), actualNewRequest.getDescription());
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void getRequestByIdTest() {
        Long userId = 1L;
        Long requestId = 1L;

        User user = new User();
        user.setId(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("description");
        itemRequest.setRequestorId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequest_Id(anyLong())).thenReturn(List.of());

        ItemRequestResponseDto actual = itemRequestService.getRequestById(userId, requestId);

        assertNotNull(actual);
        assertEquals(itemRequest.getId(), actual.getId());
        assertEquals(itemRequest.getDescription(), actual.getDescription());
    }

    @Test
    void getRequestByIdWithUserNotFoundTest() {
        Long userId = 999L;
        Long requestId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getRequestById(userId, requestId));
        verify(itemRequestRepository, never()).findById(anyLong());
    }

    @Test
    void getRequestByIdWithRequestNotFoundTest() {
        Long userId = 1L;
        Long requestId = 999L;

        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getRequestById(userId, requestId));
    }

    @Test
    void getUserRequestsTest() {
        Long userId = user.getId();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Description");
        itemRequest.setRequestorId(userId);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto();
        itemRequestResponseDto.setId(itemRequest.getId());
        itemRequestResponseDto.setDescription(itemRequest.getDescription());
        itemRequestResponseDto.setCreated(itemRequest.getCreated());
        itemRequestResponseDto.setItems(List.of());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(itemRequestRepository.findAllByRequestorId(userId)).thenReturn(List.of(itemRequest));

        List<ItemRequestResponseDto> actualDtoList = itemRequestService.getUserRequests(userId);

        assertEquals(1, actualDtoList.size());
        assertEquals(itemRequestResponseDto.getId(), actualDtoList.get(0).getId());
        assertEquals(itemRequestResponseDto.getDescription(), actualDtoList.get(0).getDescription());
        assertEquals(itemRequestResponseDto.getCreated(), actualDtoList.get(0).getCreated());
    }

    @Test
    void getAllRequestsTest() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;

        User user = new User();
        user.setId(userId);

        Pageable pageable = PageRequest.of(from / size, size, Constant.SORT_BY_CREATED_ASC);
        Page<ItemRequest> itemRequestPage = mock(Page.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdIsNot(userId, pageable)).thenReturn(itemRequestPage);
        when(itemRequestPage.stream()).thenReturn(List.of(
                createItemRequest(1L, "Description 1"),
                createItemRequest(2L, "Description 2")
        ).stream());

        List<ItemRequestResponseDto> actual = itemRequestService.getAllRequests(userId, from, size);

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals("Description 1", actual.get(0).getDescription());
        assertEquals("Description 2", actual.get(1).getDescription());
    }

    @Test
    void getAllRequestsWithUserNotFoundTest() {
        Long userId = 999L;
        Integer from = 0;
        Integer size = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> itemRequestService.getAllRequests(userId, from, size));
        verify(itemRequestRepository, never()).findAllByRequestorIdIsNot(anyLong(), any());
    }

    private ItemRequest createItemRequest(Long id, String description) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        return itemRequest;
    }
}






