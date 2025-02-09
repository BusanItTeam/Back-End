package com.shop.backend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

//주문 상태 변경 내역
@Entity
@Data
@Table(name = "order_status_history")
public class OrderStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(nullable = false)
    private Timestamp changedAt;
}
