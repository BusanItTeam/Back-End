package com.shop.backend.services;

import com.shop.backend.models.Cart;
import com.shop.backend.models.Product;
import com.shop.backend.models.User;
import com.shop.backend.repository.CartRepository;
import com.shop.backend.repository.ProductRepository;
import com.shop.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class CartService {

    @Autowired
    private  CartRepository cartRepository;
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  ProductRepository productRepository;

    public List<Cart> getUserCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartRepository.findByUser(user);
    }


    public Cart addToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<Cart> existingCartItem = cartRepository.findByUser(user).stream()
                .filter(cart -> cart.getProduct().getProductId().equals(productId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            Cart cart = existingCartItem.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            return cartRepository.save(cart);
        }

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(quantity);

        return cartRepository.save(cart);
    }


    public void removeFromCart(Long userId, Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // ✅ 본인의 장바구니 아이템인지 확인
        if (!cart.getUser().getUserId().equals(userId)) {
            throw new SecurityException("You are not authorized to remove this item");
        }

        cartRepository.deleteById(cartId);
    }


    public void clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Cart> userCart = cartRepository.findByUser(user);
        cartRepository.deleteAll(userCart);
    }
}