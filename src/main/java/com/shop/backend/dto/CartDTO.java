package com.shop.backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Long cartId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private BigDecimal price;
    private int quantity;
    private String categoryName;
    private Long optionId;
    private BigDecimal discountRate;
    private Long userId;
    private Timestamp createdAt;
    private String color;
    private String size;




    public CartDTO(Long cartId, Long productId, String name, String mainImageUrl, BigDecimal price, int quantity, String s, Long aLong, BigDecimal discountRate, Long userId, String color, String size) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = name;
        this.productImageUrl = mainImageUrl;
        this.price = price;
        this.quantity = quantity;
        this.categoryName = s;
        this.optionId = aLong;
        this.discountRate = discountRate;
        this.color = color;
        this.size = size;

    }


}
