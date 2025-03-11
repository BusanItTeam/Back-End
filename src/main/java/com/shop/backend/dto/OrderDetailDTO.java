package com.shop.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailDTO {
    private Long productId;
    private int quantity;
    private BigDecimal price;
    private String color;
    private String size;
}
