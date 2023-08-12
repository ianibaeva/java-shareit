package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validator.Validator;

import java.util.*;

@Slf4j
@Component
public class InMemoryItemStorage {

    private final HashMap<Long, Item> items = new HashMap<>();
    private long id = 1L;

    public Item addItem(ItemDto itemDto, User owner) {
        var item = ItemMapper.toItem(itemDto);
        Validator.validate(item);
        item.setId(id++);
        items.put(item.getId(), item);
        log.info("Item {} has been added", item.toString().toUpperCase());
        return item;
    }

    public void updateItem(Item item) {
        items.put(item.getId(), item);
    }

    public Item getItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NoSuchElementException();
        }
        log.info("Item {}", items.get(itemId).toString().toUpperCase());
        return items.get(itemId);
    }

    public List<Item> getAllItems() {
        log.info("Items {}", items.toString().toUpperCase());
        return new ArrayList<>(items.values());
    }

    public List<Item> getItemBySearch(String str) {
        str = str.trim().toUpperCase();
        Set<Item> resultSet = new HashSet<>();
        if (str.isEmpty()) {
            return new ArrayList<>();
        }
        for (Item newItem : items.values()) {
            if (!newItem.getAvailable()) {
                continue;
            }
            if (newItem.getName() != null && newItem.getName().toUpperCase().contains(str)) {
                resultSet.add(newItem);
            } else if (newItem.getDescription() != null && newItem.getDescription().toUpperCase().contains(str)) {
                resultSet.add(newItem);
            }
        }
        return new ArrayList<>(resultSet);
    }
}
