package com.shop.backend.dto;

import com.shop.backend.models.ProductAddImage;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

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


    public CartDTO(Long cartId, Long productId, String name, String mainImageUrl, int priceAsInt, int quantity) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = name;
        this.productImageUrl = mainImageUrl;
        this.productPrice = priceAsInt;
        this.quantity = quantity;

    }
}
