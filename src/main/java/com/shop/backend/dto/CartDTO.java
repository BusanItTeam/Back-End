package com.shop.backend.dto;

import com.shop.backend.models.Category;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class CartDTO {
    private Long cartId;
    private Long productId;
    private String productName;
    private String productImageUrl; // ✅ String 타입으로 유지
    private int productPrice; // ✅ int 타입으로 유지
    private int quantity;
    private String color;
    private String size;
    private String categoryName;
    private BigDecimal discountRate;

    public CartDTO(Long cartId, Long productId, String name, String mainImageUrl, int priceAsInt, int quantity, String categoryName, String size, String color, BigDecimal discountRate) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = name;
        this.productImageUrl = mainImageUrl;
        this.productPrice = priceAsInt;
        this.quantity = quantity;
        this.categoryName = categoryName;
        this.color = color;
        this.size = size;
        this.discountRate = discountRate;

    }


}
