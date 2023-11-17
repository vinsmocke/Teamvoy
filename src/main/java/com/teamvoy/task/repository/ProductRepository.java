package com.teamvoy.task.repository;

import com.teamvoy.task.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByNameIgnoreCase(String name);

    boolean existsByName(String name);
}
