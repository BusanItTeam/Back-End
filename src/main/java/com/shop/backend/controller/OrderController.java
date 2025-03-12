package com.shop.backend.controller;

import com.shop.backend.dto.OrderDTO;
import com.shop.backend.models.Order;
import com.shop.backend.models.OrderStatus;
import com.shop.backend.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 주문 생성 API
    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            Order order = orderService.createOrder(orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);  // 에러 처리 추가 가능
        }
    }

    // 모든 주문 조회 API
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    // 특정 주문 상세 조회 API
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        OrderDTO order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    //배송 상태 변경
    @PutMapping("/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> requestBody) { // ✅ Map으로 직접 받기

        OrderStatus status = OrderStatus.valueOf(requestBody.get("status"));
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok("상태 업데이트 완료");
    }

}


