package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Constant;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllUserItems(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId) {
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(
            @RequestHeader(Constant.REQUEST_HEADER_USER_ID) Long userId,
            @PathVariable Long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(
            @RequestHeader(Constant.REQUEST_HEADER_USER_ID) Long userId,
            @RequestParam(name = "text") String text) {
        return itemService.getAvailableItemBySearch(text, userId);
    }

    @PostMapping
    public ItemDto addItem(
            @RequestHeader(Constant.REQUEST_HEADER_USER_ID) Long userId,
            @Validated({Create.class})
            @RequestBody ItemDto itemDto) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId,
            @Validated({Update.class})
            @RequestBody ItemDto itemDto,
            @PathVariable("itemId") Long itemId) {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId,
            @Validated({Create.class})
            @RequestBody CommentDto commentDto,
            @PathVariable Long itemId) {
        return itemService.createComment(userId, commentDto, itemId);
    }
}
