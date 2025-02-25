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

    public CartService(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }


    // 카트목록 불러오기
    public List<Cart> getCartByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return cartRepository.findByUser(user);
    }


  // 카트에 담기(추가)
  // Add an item to the cart
  public Cart addToCart(Long userId, Long productId, int quantity) {
      User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
      Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

      // Check if product already exists in the cart
      List<Cart> existingCarts = cartRepository.findByUser(user);
      for (Cart cart : existingCarts) {
          if (cart.getProduct().getProductId().equals(productId)) {
              cart.setQuantity(cart.getQuantity() + quantity);
              return cartRepository.save(cart);
          }
      }

      // 새롭게 추가
      Cart cart = new Cart();
      cart.setUser(user);
      cart.setProduct(product);
      cart.setQuantity(quantity);
      return cartRepository.save(cart);
  }

    // 상품 낱개로 삭제
    public void removeOneFromCart(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        List<Cart> cartItems = cartRepository.findByUser(user);

        for (Cart cart : cartItems) {
            if (cart.getProduct().getProductId().equals(productId)) {
                if (cart.getQuantity() > 1) {
                    cart.setQuantity(cart.getQuantity() - 1);
                    cartRepository.save(cart);
                } else {
                    cartRepository.delete(cart);
                }
                return;
            }
        }
        throw new RuntimeException("Product not found in cart");
    }

    // 장바구니 비우기
    public void clearCart(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<Cart> cartItems = cartRepository.findByUser(user);

        if (!cartItems.isEmpty()) {
            cartRepository.deleteAll(cartItems);
        }
    }
}
