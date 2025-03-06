package com.shop.backend.services;

import com.shop.backend.dto.WishListDTO;
import com.shop.backend.models.WishList;

import java.util.List;

public interface WishListService {
    WishList createWishList(WishListDTO wishListDTO, String username);
    List<WishList> getWishListByUser(String username);
    void deleteWishList(Long wishListId, String username);

    // 기존 메서드
    void deleteWishListByUserAndProduct(Long productId, String username);

    // 위시리스트 존재 여부 확인
    boolean isProductInWishlist(Long productId, String username);
}