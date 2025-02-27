package com.shop.backend.dto;

import lombok.Data;

@Data
public class ProductOptionDTO {
    private Long optionId;
    private String color;
    private String size;
    private int stock;
}
