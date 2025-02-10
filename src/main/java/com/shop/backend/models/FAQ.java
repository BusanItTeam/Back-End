package com.shop.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdDateTime;  // 최초 생성 시간

    @UpdateTimestamp
    private LocalDateTime updatedDateTime;  // 마지막 수정 시간
}
