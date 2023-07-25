package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;

    // для выборочного обновления полей
    public void updateFrom(Item otherItem) {
        if (otherItem.getName() != null) {
            name = otherItem.getName();
        }
        if (otherItem.getDescription() != null) {
            description = otherItem.getDescription();
        }
        if (otherItem.getAvailable() != null) {
            available = otherItem.getAvailable();
        }
    }
}
