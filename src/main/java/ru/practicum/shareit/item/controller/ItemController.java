package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemResponseDto;
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
    public List<ItemResponseDto> getAllUserItems(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId) {
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto get(
            @RequestHeader(Constant.REQUEST_HEADER_USER_ID) Long userId,
            @PathVariable Long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItem(
            @RequestHeader(Constant.REQUEST_HEADER_USER_ID) Long userId,
            @RequestParam(name = "text") String text) {
        return itemService.getAvailableItemBySearch(text, userId);
    }

    @PostMapping
    public ItemResponseDto addItem(
            @RequestHeader(Constant.REQUEST_HEADER_USER_ID) Long userId,
            @Validated({Create.class})
            @RequestBody ItemDtoOut itemDtoOut) {
        return itemService.addItem(itemDtoOut, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId,
            @Validated({Update.class})
            @RequestBody ItemDtoOut itemDtoOut,
            @PathVariable("itemId") Long itemId) {
        return itemService.updateItem(itemId, itemDtoOut, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId,
            @Validated({Create.class})
            @RequestBody CommentRequestDto commentRequestDto,
            @PathVariable Long itemId) {
        return itemService.createComment(userId, commentRequestDto, itemId);
    }
}
