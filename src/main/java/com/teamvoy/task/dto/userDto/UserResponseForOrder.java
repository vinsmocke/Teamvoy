package com.teamvoy.task.dto.userDto;

import com.teamvoy.task.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseForOrder {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    public UserResponseForOrder() {
    }

    public UserResponseForOrder(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.role = user.getRole().getName();
    }
}
