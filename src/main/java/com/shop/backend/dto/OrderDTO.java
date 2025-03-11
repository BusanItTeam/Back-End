package com.shop.backend.dto;

import com.shop.backend.models.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDTO {
    private Long userId;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private int shippingCost;
    private String paymentMethod;
    private String refundMethod;
    private String shippingAddress;
    private String recipient;
    private String orderMessage;
    private List<OrderDetailDTO> orderDetails;
}
