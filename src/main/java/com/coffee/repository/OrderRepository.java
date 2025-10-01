package com.coffee.repository;

import com.coffee.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // 쿼리 메소드를 사용하여 특정 회원의 주문 번호가 최신인 것부터 조회합니다.
    //cf. 좀 더 복잡한 쿼리를 사용하시려면 @Query 또는 querydsl을 사용하세요
    List<Order> findByMemberIdOrderByIdDesc(Long memberId);

    List<Order> findAllByOrderByIdDesc();
}
