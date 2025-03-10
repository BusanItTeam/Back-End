package com.shop.backend.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long productId;
    private String content;
    private int rating; // 정수 값으로 전달됨
}
