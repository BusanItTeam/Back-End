package com.shop.backend.controller;

import com.shop.backend.dto.WishListDTO;
import com.shop.backend.models.WishList;
import com.shop.backend.services.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("api/wishlist")
public class WishListController {
    @Autowired
    private WishListService wishListService;

    @PostMapping
    public ResponseEntity<WishListDTO> createWishList(@RequestBody WishListDTO wishListDTO,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        WishList createdWishList = wishListService.createWishList(wishListDTO, username);
        WishListDTO responseDTO = convertToDTO(createdWishList);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<WishListDTO>> getWishListByUser(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<WishList> wishLists = wishListService.getWishListByUser(username);
        List<WishListDTO> wishListDTOs = wishLists.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(wishListDTOs);
    }

    @DeleteMapping("/{wishListId}")
    public ResponseEntity<?> deleteWishList(@PathVariable Long wishListId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        wishListService.deleteWishList(wishListId, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> deleteWishListByUserAndProduct(@PathVariable Long productId,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        wishListService.deleteWishListByUserAndProduct(productId, username);
        return ResponseEntity.ok().build();
    }

    // 찜 여부 확인
    @GetMapping("/product/{productId}")
    public ResponseEntity<Boolean> checkWishlistStatus(@PathVariable Long productId,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        boolean isWishlisted = wishListService.isProductInWishlist(productId, username);
        return ResponseEntity.ok(isWishlisted);
    }

    private WishListDTO convertToDTO(WishList wishList) {
        WishListDTO wishListDTO = new WishListDTO();
        wishListDTO.setWishListId(wishList.getWishListId());
        wishListDTO.setUserId(wishList.getUser().getUserId());
        wishListDTO.setProductId(wishList.getProduct().getProductId());
        return wishListDTO;
    }
}
