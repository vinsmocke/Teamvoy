package com.teamvoy.task.controller;

import com.teamvoy.task.dto.jwt.AuthResponse;
import com.teamvoy.task.dto.jwt.LoginRequest;
import com.teamvoy.task.dto.userDto.UserRequest;
import com.teamvoy.task.dto.userDto.UserResponse;
import com.teamvoy.task.dto.userDto.UserTransformer;
import com.teamvoy.task.exception.NullEntityReferenceException;
import com.teamvoy.task.model.User;
import com.teamvoy.task.security.jwtToken.JwtUtils;
import com.teamvoy.task.service.RoleService;
import com.teamvoy.task.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Slf4j
public class Auth {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            User user = (User) authentication.getPrincipal();
            String jwtToken = jwtUtils.generateTokenFormUsername(user.getUsername());
            AuthResponse authResponse = new AuthResponse(user.getUsername(), jwtToken);
            return ResponseEntity.ok().body(authResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRequest userDto) {
        try {
            User user = userService.create(UserTransformer.convertToEntity(
                    userDto,
                    roleService.readById(2)
            ));
            UserResponse userResponse = new UserResponse(user);
            log.info("New User has been created - {}", userResponse);
            return ResponseEntity.ok().body(userResponse);
        } catch (NullEntityReferenceException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
