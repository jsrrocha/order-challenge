package com.challenge.loomi.repository;

import com.challenge.loomi.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByIdAndActiveTrue(String id);
}