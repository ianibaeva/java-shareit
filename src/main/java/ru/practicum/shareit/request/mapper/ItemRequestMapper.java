package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDtoForRequests;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto dto) {
        return new ItemRequest(null,
                dto.getDescription(),
                null,
                LocalDateTime.now());
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return new ItemRequestDto(request.getId(),
                request.getDescription(),
                request.getCreated());
    }

    public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest request, List<ItemDtoForRequests> items) {
        return new ItemRequestResponseDto(request.getId(),
                request.getDescription(),
                request.getCreated(),
                items);
    }
}
