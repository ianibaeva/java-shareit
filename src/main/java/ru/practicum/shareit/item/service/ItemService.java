package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long userId);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getAllUserItems(Long userId);

    List<ItemDto> getAvailableItemBySearch(String text, Long userId);

    CommentDto createComment(Long userId, CommentDto commentDto, Long itemId);
}
