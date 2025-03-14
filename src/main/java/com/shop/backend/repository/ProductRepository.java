package com.shop.backend.repository;
import com.shop.backend.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String keyword);

    Page<Product> findAll(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.productId IN :productIds")
    List<Product> findProductsByIds(List<Long> productIds);
}
