package com.shop.backend.repository;

import com.shop.backend.models.ProductAddImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAddImageRepository extends JpaRepository<ProductAddImage, Long> {
}