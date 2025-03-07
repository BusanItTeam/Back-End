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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public List<CartDTO> getCartItems(Long userId) {
        List<Cart> cartItems = cartRepository.findByUser_UserId(userId);
        return cartItems.stream()
                .map(cart -> {
                    Product product = cart.getProduct();

                    // ✅ 대표 이미지 한 개만 가져오기
                    String mainImageUrl = (product.getImages() != null && !product.getImages().isEmpty()) ?
                            product.getImages().get(0).getImageUrl() :
                            "https://example.com/default.jpg"; // 기본 이미지

                    return new CartDTO(
                            cart.getCartId(),
                            product.getProductId(),
                            product.getName(),
                            mainImageUrl,
                            product.getPrice().intValue(),
                            cart.getQuantity()
                    );
                })
                .collect(Collectors.toList());
    }

}