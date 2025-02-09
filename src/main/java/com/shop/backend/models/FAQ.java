package com.shop.backend.models;

import jakarta.persistence.*;
import lombok.Data;

//자주 묻는 질문
@Entity
@Data
public class FAQ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long faqId;

    @Column(nullable = false)
    private String question;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String answer;
}
