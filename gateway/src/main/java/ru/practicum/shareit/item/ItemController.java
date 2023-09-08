package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.Constant;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId) {
        return itemClient.getAllUserItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(
            @RequestHeader(Constant.REQUEST_HEADER_USER_ID) Long userId,
            @PathVariable Long itemId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(
            @RequestHeader(Constant.REQUEST_HEADER_USER_ID) Long userId,
            @RequestParam(name = "text") String text) {
        return itemClient.getAvailableItemBySearch(userId, text);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader(Constant.REQUEST_HEADER_USER_ID) Long userId,
            @Validated({Create.class})
            @RequestBody ItemDto itemDtoOut) {
        return itemClient.addItem(itemDtoOut, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId,
            @Validated({Update.class})
            @RequestBody ItemDto itemDtoOut,
            @PathVariable("itemId") Long itemId) {
        return itemClient.updateItem(itemId, userId, itemDtoOut);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId,
            @Validated({Create.class})
            @RequestBody CommentDto commentRequestDto,
            @PathVariable Long itemId) {
        return itemClient.createComment(userId, commentRequestDto, itemId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> removeItem(@PathVariable Long id) {
        return itemClient.removeItem(id);
    }
}
