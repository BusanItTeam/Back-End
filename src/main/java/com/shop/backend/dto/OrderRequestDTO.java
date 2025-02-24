package com.shop.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequestDTO {
    private Long userId;
    private BigDecimal totalPrice;
}
