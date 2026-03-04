package com.panicar.epistemic.harmony.service;

import com.panicar.epistemic.harmony.entity.User;
import com.panicar.epistemic.harmony.entity.User.Role;
import com.panicar.epistemic.harmony.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ----------------- Create operations -------------
    @Transactional
    public User createUser(User user) {
        // Validate unique constraints
        if (usernameExists(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        if (emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }

        // Set default role if not provided
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        return userRepository.save(user);
    }

    @Transactional
    public User createUserWithRole(User user, Role role) {
        try {
            user.setRole(role);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role.name());
        }
        return createUser(user);
    }
    // -------------------------------------------------

    // ----------------- Read Operations --------------------
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String name) {
        return List.of(); // TODO: Implement name-based search
    }

    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }
    // ---------------------------------------------------

    // ------------------ Update operations --------------------
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        // Update fields if provided and different
        if (userDetails.getUsername() != null &&
                !userDetails.getUsername().equals(existingUser.getUsername())) {
            existingUser.setUsername(userDetails.getUsername());
        }

        if (userDetails.getEmail() != null &&
                !userDetails.getEmail().equals(existingUser.getEmail())) {
            // Check if new email is unique
            if (emailExists(userDetails.getEmail()) &&
                    !userDetails.getEmail().equals(existingUser.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + userDetails.getEmail());
            }
            existingUser.setEmail(userDetails.getEmail());
        }

        if (userDetails.getRole() != null &&
                !userDetails.getRole().equals(existingUser.getRole())) {
            existingUser.setRole(userDetails.getRole());
        }

        // TODO: password update logic

        return userRepository.save(existingUser);
    }

    @Transactional
    public User updateUserRole(Long id, Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        try {
            user.setRole(role);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role.name());
        }

        return userRepository.save(user);
    }
    // ------------------------------------------------

    // --------------------- Delete operations ----------------
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    // -------------------------------------------------------

    // --------------------- Just validation ------------------
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    // --------------------------------------------------------
}