package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ItemServiceImpl implements ItemService {
    private final InMemoryItemStorage inMemoryItemStorage;

    public ItemServiceImpl(InMemoryItemStorage inMemoryItemStorage) {
        this.inMemoryItemStorage = inMemoryItemStorage;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, User owner) {
        return ItemMapper.toItemDto(inMemoryItemStorage.addItem(itemDto, owner));
    }

    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto, User owner) {
        inMemoryItemStorage.updateItem(itemId, itemDto, owner);
        return getItemById(itemId);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(inMemoryItemStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getAllUserItems(int userId) {
        return inMemoryItemStorage
                .getAllItems()
                .stream()
                .filter(i -> {
                    Optional<User> ownerOptional = Optional.ofNullable(i.getOwner());
                    return ownerOptional.map(owner -> owner.getId() == userId).orElse(false);
                })
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAvailableItemBySearch(String text) {
        return inMemoryItemStorage
                .getItemBySearch(text)
                .stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
