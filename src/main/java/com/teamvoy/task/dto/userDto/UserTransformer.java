package com.teamvoy.task.dto.userDto;

import com.teamvoy.task.model.Role;
import com.teamvoy.task.model.User;

import java.util.Objects;

public class UserTransformer {
    public static User convertToEntity(UserRequest userRequest, Role role) {
        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setBalance(userRequest.getBalance());
        user.setRole(role);

        return user;
    }

    public static User convertToEntityForUpdate(UserRequest userRequest, User user) {
        if (userRequest.getFirstName() != null && !userRequest.getFirstName().isBlank()) {
            user.setFirstName(userRequest.getFirstName());
        }
        if (userRequest.getLastName() != null && !userRequest.getLastName().isBlank()) {
            user.setLastName(userRequest.getLastName());
        }
        if (userRequest.getEmail() != null && !userRequest.getEmail().isBlank()) {
            user.setEmail(userRequest.getEmail());
        }
        if (userRequest.getPassword() != null && !userRequest.getPassword().isBlank()) {
            user.setPassword(userRequest.getPassword());
        }
        if (!Objects.equals(userRequest.getBalance(), user.getBalance())) {
            user.setBalance(userRequest.getBalance());
        }

        return user;
    }
}
