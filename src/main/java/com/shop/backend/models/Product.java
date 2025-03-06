package com.shop.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Entity
@Data
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // 할인율 추가
    @Column(precision = 5, scale = 2)
    private BigDecimal discountRate;

    @CreationTimestamp
    @Column(nullable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAddImage> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options;

    public String getMainImageUrl() {
        return (images != null && !images.isEmpty())
                ? images.get(0).getImageUrl() // 리스트에서 첫 번째 이미지 URL 가져오기
                : "default.jpg"; // 기본 이미지 경로
    }

    // ✅ 가격을 int로 변환하는 메서드 추가
    public int getPriceAsInt() {
        return price != null ? price.intValue() : 0;
    }
}
