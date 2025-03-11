package com.shop.backend.repository;

import com.shop.backend.models.Cart;
import com.shop.backend.models.Product;
import com.shop.backend.models.ProductOption;
import com.shop.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);

    Optional<Cart> findByUserAndProductAndProductOption(User user, Product product, ProductOption productOption);

}
