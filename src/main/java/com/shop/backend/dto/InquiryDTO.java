package com.shop.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDTO {
    private Long inquiryId;
    private Long userId;  // 사용자 ID
    private String type;
    private String title;
    private String content;
    private Timestamp createdAt;
    private String answer;
    private Timestamp answeredAt;


}
