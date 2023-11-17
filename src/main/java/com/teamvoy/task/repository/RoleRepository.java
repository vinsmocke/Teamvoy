package com.teamvoy.task.repository;

import com.teamvoy.task.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
