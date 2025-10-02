package com.coffee.service;

import com.coffee.constant.OrderStatus;
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

    public List<Order> findOrderByMemberId(Long memberId) {
        return this.orderRepository.findByMemberIdOrderByIdDesc(memberId);
    }

    public List<Order> findAllOrderByIdDesc() {
        return this.orderRepository.findAllByOrderByIdDesc();
    }

//    public List<Order> findByStatusOrderByIdDesc(OrderStatus.PENDING) {
    // 주문의 상태가 PENDING인것만 조회합니다.
//        return this.orderRepository.findByStatusOrderByIdDesc();
//    }

    public int updateOrderStatus(Long orderId, OrderStatus status) {
        return orderRepository.updateOrderStatus(orderId,status);
    }

    public boolean existsById(Long orderId) {
        return orderRepository.existsById(orderId);
    }

    public void deleteById(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    public Optional<Order> findOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
}
