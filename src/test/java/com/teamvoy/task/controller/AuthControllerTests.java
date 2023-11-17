package com.teamvoy.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamvoy.task.dto.jwt.AuthResponse;
import com.teamvoy.task.dto.jwt.LoginRequest;
import com.teamvoy.task.dto.userDto.UserRequest;
import com.teamvoy.task.dto.userDto.UserTransformer;
import com.teamvoy.task.exception.NullEntityReferenceException;
import com.teamvoy.task.model.Role;
import com.teamvoy.task.model.User;
import com.teamvoy.task.security.jwtToken.JwtUtils;
import com.teamvoy.task.service.RoleService;
import com.teamvoy.task.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTests {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RoleService roleService;

    @Mock
    private UserService userService;

    @InjectMocks
    private Auth authController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testLogin_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("username", "password");
        User user = new User();
        user.setEmail("username");
        AuthResponse authResponse = new AuthResponse(user.getUsername(), "fakeJwtToken");

        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, null));
        when(jwtUtils.generateTokenFormUsername(anyString())).thenReturn("fakeJwtToken");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(authResponse.getUsername()))
                .andExpect(jsonPath("$.accessToken").value(authResponse.getAccessToken()));

        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtUtils, times(1)).generateTokenFormUsername(anyString());
    }

    @Test
    public void testLogin_Failure() throws Exception {
        LoginRequest loginRequest = new LoginRequest("username", "wrongPassword");

        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    public void testRegister_Success() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("Firstname");
        userRequest.setLastName("Lastname");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("1111");

        Role role = new Role();
        role.setId(1L);
        role.setName("CLIENT");

        User user = UserTransformer.convertToEntity(userRequest, role);

        when(roleService.readById(anyLong())).thenReturn(role);
        when(userService.create(any())).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.role").value(user.getRole().getName()));

        verify(roleService, times(1)).readById(anyLong());
        verify(userService, times(1)).create(any());
    }

    @Test
    public void testRegister_Failure() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("Firstname");
        userRequest.setLastName("Lastname");
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("1111");

        Role role = new Role();
        role.setId(1L);
        role.setName("CLIENT");

        when(roleService.readById(anyLong())).thenThrow(NullEntityReferenceException.class);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());

        verify(roleService, times(1)).readById(anyLong());
    }
}
