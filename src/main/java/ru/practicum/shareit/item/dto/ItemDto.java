package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(groups = {Create.class}, message = "Name is required and must have at least 1 character")
    @Size(groups = {Create.class, Update.class}, min = 1, message = "Name is required and must have at least 1 character")
    private String name;

    @NotBlank(groups = {Create.class}, message = "Description is required and must have at least 1 character")
    @Size(groups = {Create.class, Update.class}, min = 1, message = "Description is required and must have at least 1 character")
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;

    private List<CommentDto> comments;
}
