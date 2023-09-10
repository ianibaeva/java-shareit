package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    @NotBlank(groups = {Create.class})
    @Size(groups = {Create.class}, max = 255)
    private String name;

    @NotBlank(groups = {Create.class})
    @Size(groups = {Create.class}, max = 512)
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;

    private Long requestId;
}
