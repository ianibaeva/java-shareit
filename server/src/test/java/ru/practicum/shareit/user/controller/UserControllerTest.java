package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void createUser_whenUserIsValid() throws Exception {
        UserDto userDtoToCreate = new UserDto();
        userDtoToCreate.setEmail("email@email.com");
        userDtoToCreate.setName("name");

        when(userService.addUser(userDtoToCreate)).thenReturn(userDtoToCreate);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoToCreate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(userDtoToCreate.getEmail()))
                .andExpect(jsonPath("$.name").value(userDtoToCreate.getName()));
    }

//    @Test
//    void createUser_whenUserEmailIsNotValid_ReturnsBadRequest() throws Exception {
//        UserDto userDtoToCreate = new UserDto();
//        userDtoToCreate.setEmail("email.com");
//        userDtoToCreate.setName("name");
//
//        when(userService.addUser(userDtoToCreate)).thenReturn(userDtoToCreate);
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userDtoToCreate)))
//                .andExpect(status().isBadRequest());
//
//        verify(userService, never()).addUser(userDtoToCreate);
//    }

//    @Test
//    @SneakyThrows
//    @DisplayName("Testing user creation with an invalid name")
//    void createUser_whenNameIsNotValid_ReturnsBadRequest() {
//        UserDto userDtoToCreate = new UserDto();
//        userDtoToCreate.setEmail("email@email.com");
//        userDtoToCreate.setName("     ");
//
//        when(userService.addUser(userDtoToCreate)).thenReturn(userDtoToCreate);
//
//        mockMvc.perform(post("/users")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(userDtoToCreate)))
//                .andExpect(status().isBadRequest());
//
//        verify(userService, never()).addUser(userDtoToCreate);
//    }

    @Test
    void updateUser_whenUserIsValid() throws Exception {
        Long userId = 0L;
        UserDto userDtoToUpdate = new UserDto();
        userDtoToUpdate.setEmail("update@update.com");
        userDtoToUpdate.setName("update");

        when(userService.updateUser(userDtoToUpdate, userId)).thenReturn(userDtoToUpdate);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoToUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(userDtoToUpdate.getEmail()))
                .andExpect(jsonPath("$.name").value(userDtoToUpdate.getName()));
    }

//    @Test
//    void updateUser_whenUserIsNotValid_ReturnsBadRequest() throws Exception {
//        Long userId = 0L;
//        UserDto userDtoToUpdate = new UserDto();
//        userDtoToUpdate.setEmail("update.com");
//        userDtoToUpdate.setName("    ");
//
//        when(userService.updateUser(userDtoToUpdate, userId)).thenReturn(userDtoToUpdate);
//
//        mockMvc.perform(patch("/users/{userId}", userId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userDtoToUpdate)))
//                .andExpect(status().isBadRequest());
//
//        verify(userService, never()).updateUser(userDtoToUpdate, userId);
//    }

    @Test
    void getTest() throws Exception {
        long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).getUserById(userId);
    }

    @Test
    void getAll() throws Exception {
        List<UserDto> usersDtoToExpect = List.of(new UserDto());

        when(userService.getAllUsers()).thenReturn(usersDtoToExpect);

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].email").value(usersDtoToExpect.get(0).getEmail()))
                .andExpect(jsonPath("$[0].name").value(usersDtoToExpect.get(0).getName()));
    }

    @Test
    void delete() throws Exception {
        long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userId);
    }
}
