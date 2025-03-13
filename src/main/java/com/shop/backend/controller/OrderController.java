package com.shop.backend.controller;

import com.shop.backend.dto.OrderDTO;
import com.shop.backend.dto.PointUpdateRequest;
import com.shop.backend.models.Order;
import com.shop.backend.models.OrderStatus;
import com.shop.backend.security.services.UserDetailsImpl;
import com.shop.backend.services.OrderService;
import com.shop.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

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
    // 사용자별 주문 내역 조회 API 추가
    @GetMapping("/history")
    public ResponseEntity<List<OrderDTO>> getOrderHistory(Authentication authentication) {
        List<OrderDTO> orders = orderService.getOrdersByUser(authentication.getName());
        return ResponseEntity.ok(orders);
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

    //적립금 업데이트
    @PostMapping("/updatePoints")
    public ResponseEntity<?> updatePoints(@RequestBody PointUpdateRequest request, Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        try {
            userService.updateUserPoints(userId, request.getUsedPoints(), request.getEarnedPoints());
            return ResponseEntity.ok("포인트 업데이트 성공");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}


