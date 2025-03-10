package com.shop.backend.services.impl;

import com.shop.backend.dto.CartDTO;
import com.shop.backend.models.Cart;
import com.shop.backend.models.Product;
import com.shop.backend.models.ProductOption;
import com.shop.backend.models.User;
import com.shop.backend.repository.CartRepository;
import com.shop.backend.repository.ProductOptionRepository;
import com.shop.backend.repository.ProductRepository;
import com.shop.backend.repository.UserRepository;
import com.shop.backend.services.CartService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<CartDTO> getCartItems(Long userId) {
        List<Cart> cartItems = cartRepository.findByUser_UserId(userId);
        return cartItems.stream()
                .map(cart -> {
                    Product product = cart.getProduct();


                    String mainImageUrl = (product.getImages() != null && !product.getImages().isEmpty()) ?
                            product.getImages().get(0).getImageUrl() :
                            "https://example.com/default.jpg"; // 기본 이미지

                    String categoryName = (product.getCategory() != null) ? product.getCategory().getName() : "";

                    ProductOption productOption = productOptionRepository.findByProduct_ProductId(product.getProductId())
                            .orElse(null);

                    return new CartDTO(
                            cart.getCartId(),
                            product.getProductId(),
                            product.getName(),
                            mainImageUrl,
                            cart.getPrice().intValue(),
                            cart.getQuantity(),
                            categoryName,
                            productOption.getSize(),
                            productOption.getColor(),
                            product.getDiscountRate()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateCartItem(Long userId, Long cartId, int newQuantity) {
        Cart cartItem = cartRepository.findByCartIdAndUser_UserId(cartId, userId)
                .orElseThrow(() -> new RuntimeException("해당 장바구니 아이템을 찾을 수 없습니다."));

        if (newQuantity < 1) {
            return false; // 수량이 1 미만이면 업데이트 방지
        }

        cartItem.setQuantity(newQuantity);
        cartRepository.save(cartItem);
        return true;
    }


    @Override
    public boolean addToCart(Long userId, CartDTO cartDTO) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Product> productOptional = productRepository.findById(cartDTO.getProductId());

        if (!userOptional.isPresent() || !productOptional.isPresent()) {
            throw new IllegalArgumentException("유효하지 않은 사용자 또는 상품 ID입니다.");
        }

        User user = userOptional.get();
        Product product = productOptional.get();


        Optional<Cart> existingCartItem = cartRepository.findByUserAndProductAndSize(user, product, cartDTO.getSize());

        if (existingCartItem.isPresent()) {
            // 같은 상품이 이미 존재하면 수량 증가
            Cart cart = existingCartItem.get();
            cart.setQuantity(cart.getQuantity() + cartDTO.getQuantity());
            cartRepository.save(cart);
        } else {
            // 장바구니에 없으면 새로 추가
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(cartDTO.getQuantity());

            cart.setColor(cartDTO.getColor());
            cart.setSize(cartDTO.getSize());

            cartRepository.save(cart);
        }

        return true;
    }

    @Override
    public void deleteCartItem(Long cartId) {
        Cart cartItem = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 아이템을 찾을 수 없습니다."));

        cartRepository.delete(cartItem);
    }



}