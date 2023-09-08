package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentDto {
    @NotBlank//(groups = {Create.class})
    @Size(max = 1000)
    private String text;
}
