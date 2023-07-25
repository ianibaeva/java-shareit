package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private InMemoryUserStorage inMemoryUserStorage;
    private final ItemService itemService;

    @GetMapping
    private Collection<ItemDto> getAllUserItems(
            @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/{id}")
    private ItemDto getItem(
            @PathVariable("id") Long id) {
        try {
            return itemService.getItemById(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    private List<ItemDto> searchItem(
            @RequestParam String text) {
        return itemService.getAvailableItemBySearch(text);
    }

    @PostMapping
    public ItemDto addItem(
            @RequestBody ItemDto itemDto,
            @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        try {
            User user = inMemoryUserStorage.getById(userId);
            return itemService.addItem(itemDto, user);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto,
            @RequestHeader(name = "X-Sharer-User-Id") int userId) {
        try {
            User user = inMemoryUserStorage.getById(userId);
            return itemService.updateItem(itemId, itemDto, user);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}
