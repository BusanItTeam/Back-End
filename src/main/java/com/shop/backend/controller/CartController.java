package com.shop.backend.controller;


import com.shop.backend.dto.CartDTO;
import com.shop.backend.models.Cart;
import com.shop.backend.models.User;
import com.shop.backend.services.CartService;
import com.shop.backend.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CartDTO>> getUserCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<CartDTO> cartItems = cartService.getCartItems(user.getUserId());

        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> addToCart(@RequestBody CartDTO cartDTO, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        if (user == null) {
            return ResponseEntity.status(401).body("인증된 사용자만 장바구니를 이용할 수 있습니다.");
        }

        // ✅ 요청 데이터 검증
        if (cartDTO.getProductId() == null || cartDTO.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("잘못된 데이터입니다.");
        }

        // ✅ 사이즈가 숫자인지 확인 (예: "95" → 95)

        System.out.println("🛒 장바구니 추가 요청 데이터: " + cartDTO);

        boolean added = cartService.addToCart(user.getUserId(), cartDTO);

        if (added) {
            return ResponseEntity.ok("장바구니에 추가되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("장바구니 추가에 실패했습니다.");
        }
    }


    // ✅ 장바구니 아이템 업데이트 (cartId 필요)
    @PutMapping("/update/{cartId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateCartItem(
            @PathVariable Long cartId,
            @RequestBody CartDTO cartDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.findByUsername(userDetails.getUsername());

        boolean updated = cartService.updateCartItem(user.getUserId(), cartId, cartDTO.getQuantity());

        if (updated) {
            return ResponseEntity.ok("장바구니 아이템 업데이트 성공");
        } else {
            return ResponseEntity.badRequest().body("장바구니 아이템 업데이트 실패");
        }
    }

    @DeleteMapping("/delete/{cartId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable Long cartId) {
        cartService.deleteCartItem(cartId);
        return ResponseEntity.ok("장바구니에서 삭제되었습니다.");
    }

}



