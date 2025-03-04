package com.shop.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@NoArgsConstructor
@Data
public class EmailToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Instant expiryDate;

    @Getter
    private boolean used;



    private String email;


    // 이메일 인증용 생성자 (User 없이 생성 가능)
    public EmailToken(String email, String code, Instant expiryDate) {
        this.email = email;
        this.code = code;
        this.expiryDate = expiryDate;
        this.used = false;
    }
    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }


    public void markUsed() {
        this.used = true;
    }



}
