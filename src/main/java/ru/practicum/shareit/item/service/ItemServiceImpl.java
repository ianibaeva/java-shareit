package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validator.Validator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
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
        try {
            Item oldItem = inMemoryItemStorage.getItemById(itemId);
            if (oldItem == null) {
                throw new ValidationException(HttpStatus.NOT_FOUND, "Item not found");
            }
            if (oldItem.getOwner() != null && !oldItem.getOwner().equals(owner)) {
                throw new ValidationException(HttpStatus.FORBIDDEN, "You are not the owner of this item");
            }

            if (itemDto.getName() != null) {
                oldItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                oldItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                oldItem.setAvailable(itemDto.getAvailable());
            }

            Validator.validate(oldItem);
            inMemoryItemStorage.updateItem(oldItem);
            log.info("Item {} has been updated", oldItem.toString().toUpperCase());
            return ItemMapper.toItemDto(oldItem);
        } catch (ValidationException e) {
            log.error("Error updating item with ID {}: {}", itemId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred while updating item with ID {}: {}", itemId, e.getMessage());
            throw e;
        }
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
