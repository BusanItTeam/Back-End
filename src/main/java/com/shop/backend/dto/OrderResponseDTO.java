package com.shop.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shop.backend.models.Order;
import com.shop.backend.models.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private Timestamp createdAt;

    public OrderResponseDTO(Order order) {
        this.orderId = order.getOrderId();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
    }


}