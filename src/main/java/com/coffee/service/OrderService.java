package com.coffee.service;

import com.coffee.entity.Order;
import com.coffee.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public List<Order> findOrderByMemberId(Long memberId) {
        return this.orderRepository.findByMemberIdOrderByIdDesc(memberId);
    }

    public List<Order> findAllOrderByIdDesc() {
        return this.orderRepository.findAllByOrderByIdDesc();
    }
}
