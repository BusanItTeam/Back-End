package com.shop.backend.services.impl;

import com.shop.backend.dto.CartDTO;
import com.shop.backend.models.Cart;
import com.shop.backend.models.Product;
import com.shop.backend.models.ProductOption;
import com.shop.backend.models.User;
import com.shop.backend.repository.CartRepository;
import com.shop.backend.repository.ProductOptionRepository;
import com.shop.backend.repository.ProductRepository;
import com.shop.backend.services.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;


    @Override
    public List<CartDTO> getCartItems(User user) {
        return cartRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CartDTO addToCart(User user, Long productId, Long optionId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductOption productOption = null;
        if (optionId != null) {
            productOption = productOptionRepository.findById(optionId)
                    .orElseThrow(() -> new RuntimeException("Product option not found"));
        }

        Optional<Cart> existingCart = cartRepository.findByUserAndProductAndProductOption(user, product, productOption);

        Cart cart;
        if (existingCart.isPresent()) {
            cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
        } else {
            cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setProductOption(productOption);
            cart.setQuantity(quantity);
        }

        cartRepository.save(cart);
        return convertToDTO(cart);
    }


    @Override
    public void removeFromCart(User user, Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cart.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized access");
        }

        cartRepository.delete(cart);
    }

    @Override
    public void updateCartQuantity(User user, Long cartId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cart.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized access");
        }

        cart.setQuantity(quantity);
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(User user) {
        List<Cart> cartItems = cartRepository.findByUser(user);
        cartRepository.deleteAll(cartItems);
    }

    private CartDTO convertToDTO(Cart cart) {
        Product product = cart.getProduct();
        ProductOption productOption = cart.getProductOption();

        return new CartDTO(
                cart.getCartId(),
                product.getProductId(),
                product.getName(),
                product.getMainImageUrl(),
                product.getPrice(),
                cart.getQuantity(),
                product.getCategory() != null ? product.getCategory().getName() : "카테고리 없음",
                productOption != null ? productOption.getOptionId() : null,
                product.getDiscountRate(),
                cart.getUser().getUserId(),
                productOption != null ? productOption.getColor() : "색상 없음",
                productOption != null ? productOption.getSize() : "사이즈 없음"

        );
    }
}
