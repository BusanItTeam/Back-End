package com.shop.backend.services;


import com.shop.backend.dto.OrderDTO;
import com.shop.backend.dto.OrderDetailDTO;
import com.shop.backend.models.*;
import com.shop.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);  // 사용자는 별도로 로딩하여 설정
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setStatus(OrderStatus.PENDING);  // 초기 상태는 PENDING
        order.setShippingCost(orderDTO.getShippingCost());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setRefundMethod(orderDTO.getRefundMethod());
        order.setShippingAddress(orderDTO.getShippingAddress());
        order.setRecipient(orderDTO.getRecipient());
        order.setOrderMessage(orderDTO.getOrderMessage());

        // 주문 저장
        orderRepository.save(order);

        // 주문 상세 저장
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailDTO detailDTO : orderDTO.getOrderDetails()) {
            Product product = productRepository.findById(detailDTO.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
            ProductOption productOption = productOptionRepository.findById(detailDTO.getProductId()).orElseThrow(() -> new RuntimeException("Product option not found"));

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setProductOption(productOption);
            orderDetail.setQuantity(detailDTO.getQuantity());
            orderDetail.setPrice(product.getPrice());  // 상품 가격 설정
            orderDetail.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(detailDTO.getQuantity())));  // 총 가격 계산

            orderDetails.add(orderDetail);
        }

        order.setOrderDetails(orderDetails);
        orderDetailRepository.saveAll(orderDetails); // 주문 상세 항목 저장

        return order;
    }

    // 모든 주문 조회
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    // 특정 주문 조회
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToOrderDTO(order);
    }

    // Order를 OrderDTO로 변환
    private OrderDTO convertToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO(); // 기본 생성자로 수정
        dto.setUserId(order.getUser().getUserId()); // 수정된 부분
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus());
        dto.setShippingCost(order.getShippingCost());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setRefundMethod(order.getRefundMethod());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setRecipient(order.getRecipient());
        dto.setOrderMessage(order.getOrderMessage());
        dto.setOrderDetails(order.getOrderDetails().stream()
                .map(this::convertToOrderDetailDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    // OrderDetail을 OrderDetailDTO로 변환
    private OrderDetailDTO convertToOrderDetailDTO(OrderDetail orderDetail) {
        OrderDetailDTO dto = new OrderDetailDTO(); // 기본 생성자로 수정
        dto.setProductId(orderDetail.getProduct().getProductId());
        dto.setQuantity(orderDetail.getQuantity());
        dto.setPrice(orderDetail.getPrice());
        dto.setOptionId(orderDetail.getProductOption() != null ? orderDetail.getProductOption().getOptionId() : null);
        return dto;
    }
}
