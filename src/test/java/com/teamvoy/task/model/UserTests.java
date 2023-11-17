package com.teamvoy.task.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void createUser_ValidData_Success() {
        // Arrange
        Role role = new Role();
        role.setId(1L);
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("Password123");
        user.setBalance(100.0);
        user.setRole(role);
        user.setOrders(new ArrayList<>());

        assertDoesNotThrow(() -> validator.validate(user));
        assertNotNull(user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("Password123", user.getPassword());
        assertEquals(100.0, user.getBalance());
        assertEquals(role, user.getRole());
        assertNotNull(user.getOrders());
        assertTrue(user.getOrders().isEmpty());
    }

    @Test
    public void createUser_InvalidEmail_ValidationError() {
        User user = new User();
        user.setEmail("invalid-email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Must be a valid e-mail address", violation.getMessage());
    }

    @Test
    public void createUser_InvalidFirstName_ValidationError() {
        User user = new User();
        user.setFirstName("123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Must start with a capital letter followed by one or more lowercase letters", violation.getMessage());
    }

    @Test
    public void createUser_PasswordConstraints_Success() {
        User user = new User();
        user.setPassword("Pass123");

        assertDoesNotThrow(() -> validator.validate(user));
    }

    @Test
    public void createUser_PasswordNoDigit_ValidationError() {
        User user = new User();
        user.setPassword("Password");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Must contain at least one digit", violation.getMessage());
    }

    @Test
    public void createUser_PasswordNoUppercase_ValidationError() {
        User user = new User();
        user.setPassword("password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Must contain at least one uppercase letter", violation.getMessage());
    }

    @Test
    public void createUser_PasswordNoLowercase_ValidationError() {
        User user = new User();
        user.setPassword("PASSWORD123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Must contain at least one lowercase letter", violation.getMessage());
    }

    @Test
    public void createUser_PasswordTooShort_ValidationError() {
        User user = new User();
        user.setPassword("P1s");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Must be minimum 6 symbols long, using digits and latin letters", violation.getMessage());
    }


    @Test
    public void userDetailsMethods_Success() {
        User user = new User();
        user.setEmail("john.doe@example.com");
        user.setPassword("Password123");
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRole(role);

        assertEquals(user.getEmail(), user.getUsername());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
        assertEquals(role, user.getAuthorities().iterator().next());
    }
}
