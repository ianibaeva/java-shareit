package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemResponseDto addItem(ItemDtoOut itemDtoOut, Long userId);

    ItemResponseDto updateItem(Long itemId, ItemDtoOut itemDtoOut, Long userId);

    ItemResponseDto getItemById(Long itemId, Long userId);

    List<ItemResponseDto> getAllUserItems(Long userId);

    List<ItemResponseDto> getAvailableItemBySearch(String text, Long userId);

    CommentResponseDto createComment(Long userId, CommentRequestDto commentRequestDto, Long itemId);
}
