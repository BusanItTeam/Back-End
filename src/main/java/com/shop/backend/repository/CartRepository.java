package com.shop.backend.repository;

import com.shop.backend.models.Cart;
import com.shop.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser_UserId(Long userId);
}

