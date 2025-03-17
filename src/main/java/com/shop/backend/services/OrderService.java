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

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingCost(orderDTO.getShippingCost());
        order.setPaymentMethod(orderDTO.getPaymentMethod());
        order.setRefundMethod(orderDTO.getRefundMethod());
        order.setShippingAddress(orderDTO.getShippingAddress());
        order.setRecipient(orderDTO.getRecipient());
        order.setOrderMessage(orderDTO.getOrderMessage());

        // 주문 저장
        orderRepository.save(order);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (OrderDetailDTO detailDTO : orderDTO.getOrderDetails()) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            ProductOption productOption = productOptionRepository.findById(detailDTO.getOptionId())
                    .orElseThrow(() -> new RuntimeException("Product option not found"));

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setProductOption(productOption);
            orderDetail.setQuantity(detailDTO.getQuantity());
            orderDetail.setPrice(product.getPrice());
            orderDetail.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(detailDTO.getQuantity())));

            Inventory inventory = inventoryRepository.findById(productOption.getInventory().getInventoryId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));

            if (inventory.getStock() < orderDetail.getQuantity()) {
                throw new RuntimeException("재고가 부족합니다. 상품: " + product.getName());
            }

            inventory.setStock(inventory.getStock() - detailDTO.getQuantity());
            inventoryRepository.save(inventory);

            orderDetails.add(orderDetail);
        }

        order.setOrderDetails(orderDetails);
        orderDetailRepository.saveAll(orderDetails);

        removeItemsFromCart(user, orderDTO);

        return order;
    }

    private void removeItemsFromCart(User user, OrderDTO orderDTO) {
        List<Long> productIds = orderDTO.getOrderDetails().stream()
                .map(OrderDetailDTO::getProductId)
                .collect(Collectors.toList());

        cartRepository.deleteByUserAndProductIds(user, productIds);
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
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser().getUserId());
        dto.setName(order.getUser().getName());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus());
        dto.setShippingCost(order.getShippingCost());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setRefundMethod(order.getRefundMethod());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setRecipient(order.getRecipient());
        dto.setOrderMessage(order.getOrderMessage());

        if (order.getOrderDetails() != null) {
            dto.setOrderDetails(order.getOrderDetails().stream()
                    .map(this::convertToOrderDetailDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setOrderDetails(new ArrayList<>());
        }

        return dto;
    }

    // OrderDetail을 OrderDetailDTO로 변환
    private OrderDetailDTO convertToOrderDetailDTO(OrderDetail orderDetail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setProductId(orderDetail.getProduct().getProductId());
        dto.setDiscountRate(orderDetail.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
        dto.setProductName(orderDetail.getProduct().getName());
        dto.setImage(orderDetail.getProduct().getMainImageUrl());
        dto.setQuantity(orderDetail.getQuantity());
        dto.setPrice(orderDetail.getPrice());
        dto.setOptionId(orderDetail.getProductOption() != null ? orderDetail.getProductOption().getOptionId() : null);
        dto.setOptionColor(orderDetail.getProductOption().getColor());
        dto.setOptionSize(orderDetail.getProductOption().getSize());

        // 리뷰가 존재하는지 확인하는 로직 추가
        boolean reviewExists = reviewRepository.existsByUserUserIdAndProductProductIdAndProductOptionOptionId(
                orderDetail.getOrder().getUser().getUserId(),
                orderDetail.getProduct().getProductId(),
                orderDetail.getProductOption().getOptionId()
        );
        dto.setReviewExists(reviewExists);

        return dto;
    }

    // 배송 상태 변경
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문 없음"));
        order.setStatus(status);
    }

    public List<OrderDTO> getOrdersByUser(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> userOrders = orderRepository.findByUser(user);
        return userOrders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }
}
