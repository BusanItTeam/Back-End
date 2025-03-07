package com.shop.backend.controller;


import com.shop.backend.dto.CartDTO;
import com.shop.backend.models.Cart;
import com.shop.backend.models.User;
import com.shop.backend.services.CartService;
import com.shop.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private UserService userService;

    @GetMapping("/show")
    public ResponseEntity<List<CartDTO>> getUserCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<CartDTO> cartItems = cartService.getCartItems(user.getUserId());

        return ResponseEntity.ok(cartItems);
    }



}
