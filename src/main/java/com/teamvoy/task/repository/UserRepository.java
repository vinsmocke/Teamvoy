package com.teamvoy.task.repository;

import com.teamvoy.task.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    boolean existsUserByEmail(String email);
}
