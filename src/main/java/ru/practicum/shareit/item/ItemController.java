package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Constant;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Slf4j
public class ItemController {
    private final UserService userService;
    private final ItemService itemService;

    @GetMapping
    private List<ItemDto> getAllUserItems(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) int userId) {
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
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) int userId) {
        try {
            User user = userService.getById(userId);
            return itemService.addItem(itemDto, user);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto,
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) int userId) {
        try {
            User user = userService.getById(userId);
            return itemService.updateItem(itemId, itemDto, user);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}
