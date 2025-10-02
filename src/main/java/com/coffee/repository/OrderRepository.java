package com.coffee.repository;

import com.coffee.constant.OrderStatus;
import com.coffee.entity.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // 쿼리 메소드를 사용하여 특정 회원의 주문 번호가 최신인 것부터 조회합니다.
    //cf. 좀 더 복잡한 쿼리를 사용하시려면 @Query 또는 querydsl을 사용하세요
    List<Order> findByMemberIdOrderByIdDesc(Long memberId);

    List<Order> findAllByOrderByIdDesc();

    // 특정 주문의 상태를 주문완료(COMPLETED)로 변경합니다.
    // 쿼리 메소드 대신 @Query 사용 예시 (sql 대신 JPQL 사용)
    // 데이터베이스 내에 있는 테이블 이름이 아닌 스프링에서 작성한 Entity이름을 사용해야한다.
    // 대소문자를 구분합니다.
    // JPQL 에서는 간단한 쿼리문이라도 alias 사용 권장
    @Modifying // select문이 아닌 update, delete등의 쿼리를 JPA에서 사용할때는 @Modifying으로 명시를 해줘야합니다. 반환은 영향을 받은 행(Row)수를 int값으로 반환해줍니다.
    @Transactional
    @Query("update Order o set o.status = :status where o.id= :orderId")
    int updateOrderStatus(@Param("orderId") Long orderId,@Param("status") OrderStatus status);

   // List<Order> findByStatusOrderByIdDesc();
}
