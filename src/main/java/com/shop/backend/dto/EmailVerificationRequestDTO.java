package com.shop.backend.dto;

import lombok.Data;

@Data
public class EmailVerificationRequestDTO {
    private String email;
    private String code;
}
