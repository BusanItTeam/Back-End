package com.shop.backend.repository;

import com.shop.backend.models.Cart;
import com.shop.backend.models.Product;
import com.shop.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser_UserId(Long userId);


    Optional<Cart> findByCartIdAndUser_UserId(Long cartId, Long userId);





    Optional<Cart> findByUserAndProductAndSize(User user, Product product, String size);
}

