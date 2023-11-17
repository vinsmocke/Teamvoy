package com.teamvoy.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamvoy.task.dto.userDto.UserRequest;
import com.teamvoy.task.dto.userDto.UserResponse;
import com.teamvoy.task.model.Role;
import com.teamvoy.task.model.User;
import com.teamvoy.task.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTests {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetAll() throws Exception {
        Role role = new Role();
        role.setId(1L);
        role.setName("CLIENT");

        User user = new User();
        user.setRole(role);

        List<User> userList = Arrays.asList(user);
        when(userService.getAll()).thenReturn(userList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(userList.size()));

        verify(userService, times(1)).getAll();
    }

    @Test
    public void testGetById() throws Exception {
        Role role = new Role();
        role.setId(1L);
        role.setName("CLIENT");

        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setRole(role);

        when(userService.readById(userId)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .param("id", String.valueOf(userId)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()));

        verify(userService, times(1)).readById(userId);
    }

    @Test
    public void testUpdate() throws Exception {
        Role role = new Role();
        role.setId(1L);
        role.setName("CLIENT");

        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setRole(role);

        UserRequest userRequest = new UserRequest();
        User updatedUser = new User();
        updatedUser.setId(userId);
        userRequest.setFirstName("Alan");
        userRequest.setLastName("Walker");
        userRequest.setEmail("alanWalker@mail.com");
        userRequest.setBalance(200);
        updatedUser.setRole(role);

        when(userService.readById(userId)).thenReturn(user);
        when(userService.update(any(User.class))).thenReturn(updatedUser);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/users")
                        .param("id", String.valueOf(userId))
                        .content(asJsonString(userRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        UserResponse userResponse = new ObjectMapper().readValue(content, UserResponse.class);

        verify(userService, times(1)).readById(userId);
        verify(userService, times(1)).update(any(User.class));
    }

    @Test
    public void testDelete() throws Exception {
        long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users")
                        .param("id", String.valueOf(userId)))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().string("User with id " + userId + " has been removed"));

        verify(userService, times(1)).delete(userId);
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
