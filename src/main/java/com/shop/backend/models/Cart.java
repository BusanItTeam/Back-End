package com.shop.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.math.BigDecimal;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = true)
    @JsonIgnore
    private ProductOption productOption;

    @CreationTimestamp
    @Column(nullable = false)
    private Timestamp createdAt;




    
  public BigDecimal getPrice() {
        if (product == null || product.getPrice() == null) {
            return BigDecimal.ZERO; // 상품이 없는 경우 기본값
        }

        BigDecimal discountRate = product.getDiscountRate().divide(BigDecimal.valueOf(100));
        BigDecimal discountAmount = product.getPrice().multiply(discountRate);
        return product.getPrice().subtract(discountAmount);
    }

}
