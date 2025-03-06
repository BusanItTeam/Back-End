package com.shop.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WishListDTO {
    private Long wishListId;
    private Long userId; //사용자 ID
    private Long productId; // 상품 ID
    private Long optionId; // 선택한 옵션 ID

    // 상품 정보 추가
    private String productName; // 상품명
    private String productImage; // 상품 이미지 URL
    private BigDecimal price; // 상품 가격
    private String option; // 선택 옵션
}
