package com.shop.backend.services;


import com.shop.backend.dto.CartDTO;
import com.shop.backend.models.Cart;
import com.shop.backend.models.User;
import org.springframework.http.ResponseEntity;

import java.util.List;


import java.util.List;

public interface CartService {
    CartDTO addToCart(User user, Long productId, Long optionId, int quantity);
    List<CartDTO> getCartItems(User user);
    void removeFromCart(User user, Long cartId);
    void updateCartQuantity(User user, Long cartId, int quantity);
    void clearCart(User user);
}
