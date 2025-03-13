package com.shop.backend.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long productId;
    private int rating;
    private String content;
}
