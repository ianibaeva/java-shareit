package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(groups = {Create.class}, message = "Name is required and must have at least 1 character")
    @Size(groups = {Create.class, Update.class}, min = 1, message = "Name is required and must have at least 1 character")
    private String name;

    @NotBlank(groups = {Create.class}, message = "Email is required and must have at least 1 character")
    @Email(groups = {Update.class, Create.class}, message = "Invalid email format")
    @Size(groups = {Create.class, Update.class}, min = 1, message = "Invalid email format")
    private String email;
}
