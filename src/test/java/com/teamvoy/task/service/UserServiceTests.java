package com.teamvoy.task.service;

import com.teamvoy.task.exception.EntityNotFoundException;
import com.teamvoy.task.exception.InvalidEmailException;
import com.teamvoy.task.exception.NullEntityReferenceException;
import com.teamvoy.task.model.User;
import com.teamvoy.task.repository.UserRepository;
import com.teamvoy.task.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUserSuccess() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        when(userRepository.existsUserByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.create(user);

        assertNotNull(createdUser);
        assertEquals("encodedPassword", createdUser.getPassword());
        verify(userRepository, times(1)).existsUserByEmail(user.getEmail());
        verify(passwordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        when(userRepository.existsUserByEmail(user.getEmail())).thenReturn(true);

        assertThrows(InvalidEmailException.class, () -> userService.create(user));

        verify(userRepository, times(1)).existsUserByEmail(user.getEmail());
        verifyNoMoreInteractions(passwordEncoder, userRepository);
    }

    @Test
    void testCreateUserWithNullUser() {
        assertThrows(NullEntityReferenceException.class, () -> userService.create(null));

        verifyNoInteractions(passwordEncoder, userRepository);
    }

    @Test
    void testReadUserByIdSuccess() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User retrievedUser = userService.readById(1L);

        assertNotNull(retrievedUser);
        assertEquals(user, retrievedUser);

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testReadUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.readById(1L));

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateUserSuccess() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("test@example.com");
        existingUser.setPassword("oldPassword");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("test@example.com");
        updatedUser.setPassword("password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(updatedUser.getPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User modifiedUser = userService.update(updatedUser);

        assertNotNull(modifiedUser);
        assertEquals("encodedNewPassword", modifiedUser.getPassword());

        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void testUpdateUserNotFound() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(user));

        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(passwordEncoder, userRepository);
    }

    @Test
    void testUpdateUserWithNullUser() {
        assertThrows(NullEntityReferenceException.class, () -> userService.update(null));

        verifyNoInteractions(passwordEncoder, userRepository);
    }

    @Test
    void testDeleteUserSuccess() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.delete(1L));

        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testGetAllUsers() {
        List<User> userList = new ArrayList<>();

        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");

        userList.add(user1);
        userList.add(user2);

        when(userRepository.findAll()).thenReturn(userList);

        List<User> allUsers = userService.getAll();

        assertNotNull(allUsers);
        assertEquals(userList.size(), allUsers.size());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistent@example.com"));

        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }
}
