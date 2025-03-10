package com.shop.backend.services;


import com.shop.backend.dto.CartDTO;
import com.shop.backend.models.Cart;
import com.shop.backend.models.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CartService {



    List<CartDTO> getCartItems(Long userId);


    boolean updateCartItem(Long userId, Long cartId, int quantity);

    boolean addToCart(Long userId, CartDTO cartDTO);

    void deleteCartItem(Long cartId);
}
