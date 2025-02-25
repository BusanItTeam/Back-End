package com.shop.backend.controller;


import com.shop.backend.models.Cart;
import com.shop.backend.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    // 유저아이디로 카트 목록 찾

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUser(userId));
    }

    // 카트추가
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.addToCart(userId, productId, quantity));
    }


    // 카트 하나씩 제거
    @DeleteMapping("/removeOne")
    public ResponseEntity<Void> removeOneFromCart(
            @RequestParam Long userId,
            @RequestParam Long productId) {
        cartService.removeOneFromCart(userId, productId);
        return ResponseEntity.noContent().build();
    }

    //장바구니 비우기
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
