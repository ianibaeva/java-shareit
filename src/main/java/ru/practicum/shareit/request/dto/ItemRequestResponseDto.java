package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoForRequests;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestResponseDto {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Description cannot be empty")
    private String description;
    private LocalDateTime created;
    private List<ItemDtoForRequests> items;
}
