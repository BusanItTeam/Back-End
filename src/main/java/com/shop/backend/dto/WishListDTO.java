package com.shop.backend.dto;

import lombok.Data;

@Data
public class WishListDTO {
    private Long wishListId;
    private Long userId; //사용자 ID
    private Long productId; // 상품 ID
}
