package com.panicar.epistemic.harmony.controller;

import com.panicar.epistemic.harmony.entity.User;
import com.panicar.epistemic.harmony.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // New Import
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Replaces @MockBean in Spring Boot 3.4+
    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("GET /api/users/{id} - Should return UserDTO")
    void getUserById_ShouldReturnUser() throws Exception {
        // Arrange
        User user = new User("tester", "test@fit.cvut.cz", "pass", User.Role.USER, true);
        user.setId(1L);
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("tester"))
                .andExpect(jsonPath("$.email").value("test@fit.cvut.cz"));
    }

    @Test
    @DisplayName("POST /api/users - Should return 201 Created")
    void createUser_ShouldReturnCreated() throws Exception {
        // Arrange
        User user = new User("newbie", "new@fit.cvut.cz", "pass", User.Role.USER, true);
        user.setId(2L);
        when(userService.createUser(any(User.class))).thenReturn(user);

        String userJson = """
            {
                "username": "newbie",
                "email": "new@fit.cvut.cz",
                "password": "pass",
                "role": "USER",
                "isActive": true
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newbie"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should return 204 No Content")
    void deleteUser_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}