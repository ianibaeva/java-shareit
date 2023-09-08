package ru.practicum.shareit.item.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    @NotBlank//(groups = {Create.class})
    @Size(max = 1000)
    private String text;
}
