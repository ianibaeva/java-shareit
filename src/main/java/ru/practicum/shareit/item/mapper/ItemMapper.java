package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequests;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public static ItemResponseDto toItemDto(Item item, List<CommentResponseDto> comments,
                                            BookingItemDto lastBooking, BookingItemDto nextBooking) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments,
                item.getRequestId()
        );
    }

    public static Item toItem(ItemDtoOut itemDtoOut) {
        return new Item(
                null,
                itemDtoOut.getName(),
                itemDtoOut.getDescription(),
                itemDtoOut.getAvailable(),
                null,
                itemDtoOut.getRequestId()
        );
    }

    public static ItemDtoForRequests toItemDtoShort(Item item) {
        return new ItemDtoForRequests(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId());
    }
}
