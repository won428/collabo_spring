package com.coffee.service;

import com.coffee.entity.Order;
import com.coffee.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public List<Order> findOrderListById(Long id) {
        return orderRepository.findOrderListById(id);
    }

    public Optional<Order> findOrderByMemberId(Long id) {
        return this.orderRepository.findById(id);
    }
}
