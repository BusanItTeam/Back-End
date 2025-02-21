package com.shop.backend.controller;


import com.shop.backend.models.Cart;
import com.shop.backend.models.User;
import com.shop.backend.services.CartService;
import com.shop.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;

    // 로그인한 유저만 본인의 장바구니를 조회 가능
    @GetMapping
    public ResponseEntity<List<Cart>> getUserCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(cartService.getUserCart(user.getUserId()));
    }

    //  로그인한 유저만 장바구니 추가 가능
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@AuthenticationPrincipal UserDetails userDetails,
                                          @RequestParam Long productId,
                                          @RequestParam int quantity) {
        User user = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(cartService.addToCart(user.getUserId(), productId, quantity));
    }

    //  로그인한 유저만 본인의 장바구니 아이템 삭제 가능
    @DeleteMapping("/remove/{cartId}")
    public ResponseEntity<String> removeFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                                 @PathVariable Long cartId) {
        User user = userService.findByUsername(userDetails.getUsername());
        cartService.removeFromCart(user.getUserId(), cartId);
        return ResponseEntity.ok("Item removed from cart");
    }


    // 로그인한 유저만 본인의 장바구니 전체 삭제 가능
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        cartService.clearCart(user.getUserId());
        return ResponseEntity.ok("Cart cleared");
    }


}
