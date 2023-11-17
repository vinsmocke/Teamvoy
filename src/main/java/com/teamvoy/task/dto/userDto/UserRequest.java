package com.teamvoy.task.dto.userDto;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class UserRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private double balance;

    public UserRequest() {
    }

    public UserRequest(String firstName, String lastName, String email, String password, double balance) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRequest that = (UserRequest) o;
        return Double.compare(balance, that.balance) == 0 && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, email, password, balance);
    }
}
