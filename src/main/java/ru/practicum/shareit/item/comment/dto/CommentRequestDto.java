package ru.practicum.shareit.item.comment.dto;

import lombok.Data;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentRequestDto {

    @NotBlank(groups = {Create.class})
    @Size(max = 255)
    private String text;
}
