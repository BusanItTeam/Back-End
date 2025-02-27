package com.shop.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "inventory")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @Column(nullable = false)
    private int stock;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id") //nullable = false
    @JsonIgnore
    private ProductOption option;
}