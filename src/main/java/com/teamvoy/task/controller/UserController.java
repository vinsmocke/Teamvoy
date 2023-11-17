package com.teamvoy.task.controller;

import com.teamvoy.task.dto.userDto.UserRequest;
import com.teamvoy.task.dto.userDto.UserResponse;
import com.teamvoy.task.dto.userDto.UserTransformer;
import com.teamvoy.task.model.User;
import com.teamvoy.task.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    @PreAuthorize("@check.isManager()")
    public List<UserResponse> getAll() {
        return userService.getAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping()
    @PreAuthorize("@check.confirmAccessOwnerOrManager(#id)")
    public UserResponse getById(@RequestParam long id) {
        return new UserResponse(userService.readById(id));
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("@check.confirmAccessOwnerOrManager(#id)")
    public UserResponse update(@RequestParam long id, @RequestBody @Valid UserRequest userDto) {
        User user = userService.readById(id);
        return new UserResponse(userService.update(UserTransformer.convertToEntityForUpdate(userDto, user)));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("@check.isManager()")
    public ResponseEntity<String> delete(@RequestParam long id) {
        userService.delete(id);
        return ResponseEntity.accepted().body("User with id " + id + " has been removed");
    }
}
