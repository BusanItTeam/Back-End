package com.shop.backend.services;

import com.shop.backend.dto.WishListDTO;
import com.shop.backend.models.WishList;

import java.util.List;

public interface WishListService {
    WishList createWishList(WishListDTO wishListDTO, String username);
    List<WishList> getWishListByUser(String username);
    void deleteWishList(Long wishListId, String username);
    void deleteWishListByUserAndProduct(Long productId, String username);
    boolean isProductInWishlist(Long productId, String username);
}
