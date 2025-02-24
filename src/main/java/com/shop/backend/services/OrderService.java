package com.shop.backend.services;

import com.shop.backend.dto.OrderRequestDTO;
import com.shop.backend.dto.OrderResponseDTO;
import com.shop.backend.models.Order;
import com.shop.backend.models.OrderStatus;
import com.shop.backend.models.User;
import com.shop.backend.repository.OrderRepository;
import com.shop.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;


    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }


    public OrderResponseDTO createOrder(
            OrderRequestDTO requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice(requestDto.getTotalPrice());
        order.setStatus(OrderStatus.PENDING);

        order = orderRepository.save(order);
        return new OrderResponseDTO(order);
    }

    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponseDTO::new)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderResponseDTO(order);
    }


}
