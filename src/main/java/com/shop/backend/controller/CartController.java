package com.shop.backend.controller;

import com.shop.backend.dto.CartDTO;
import com.shop.backend.models.User;
import com.shop.backend.repository.UserRepository;
import com.shop.backend.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/show")
    public ResponseEntity<List<CartDTO>> getCartItems(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        List<CartDTO> cartItems = cartService.getCartItems(user);
        return ResponseEntity.ok(cartItems);
    }


    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestBody CartDTO cartDTO) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        CartDTO cart = cartService.addToCart(user, cartDTO.getProductId(), cartDTO.getOptionId(), cartDTO.getQuantity());
        return ResponseEntity.ok(cart);
    }


    @DeleteMapping("/delete/{cartId}")
    public ResponseEntity<Void> removeFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable Long cartId) {
        User user = userRepository.findByUserName(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        cartService.removeFromCart(user, cartId);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/update/{cartId}")
    public ResponseEntity<Void> updateCartQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartId,
            @RequestBody Map<String, Integer> requestBody) {

        int quantity = requestBody.get("quantity");

        User user = userRepository.findByUserName(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        cartService.updateCartQuantity(user, cartId, quantity);
        return ResponseEntity.ok().build();
    }




    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUserName(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        cartService.clearCart(user);
        return ResponseEntity.ok().build();
    }
}
