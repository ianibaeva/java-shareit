package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, User owner);

    ItemDto updateItem(Long itemId, ItemDto itemDto, User owner);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllUserItems(int userId);

    List<ItemDto> getAvailableItemBySearch(String text);
}
