package com.shop.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    @JsonManagedReference
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @CreationTimestamp
    @Column(nullable = false)
    private Timestamp createdAt;


    @Getter
    @Setter
    @Column(nullable = false, name = "product_size") // ✅ DTO에서 받은 사이즈 저장
    private String size;  // ✅ 기존 문제 해결 (DTO 반영)

    @Setter
    @Getter
    @Column(nullable = false, name = "product_color") // ✅ DTO에서 받은 색상 저장
    private String color;


}
