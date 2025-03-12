package com.shop.backend.dto;

import lombok.Data;

@Data
public class PointUpdateRequest {
    private int usedPoints;
    private int earnedPoints;
}
