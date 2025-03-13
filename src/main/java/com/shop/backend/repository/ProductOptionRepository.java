package com.shop.backend.repository;

import com.shop.backend.models.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    Optional<ProductOption> findById(Long id);






    List<ProductOption> findByProduct_ProductId(Long productId);
}
