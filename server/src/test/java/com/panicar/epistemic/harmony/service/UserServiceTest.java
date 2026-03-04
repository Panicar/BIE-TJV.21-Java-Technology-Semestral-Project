package com.panicar.epistemic.harmony.service;

import com.panicar.epistemic.harmony.entity.User;
import com.panicar.epistemic.harmony.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testGetUserById_Success() {
        // Arrange
        User user = new User("testUser", "test@fit.cvut.cz", "123", User.Role.USER, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        Optional<User> found = userService.getUserById(1L);

        // Assert
        assertTrue(found.isPresent());
        assertEquals("testUser", found.get().getUsername());
    }
}
