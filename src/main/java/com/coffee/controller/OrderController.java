package com.coffee.controller;

import com.coffee.dto.OrderDto;
import com.coffee.dto.OrderItemDto;
import com.coffee.entity.Member;
import com.coffee.entity.Order;
import com.coffee.entity.OrderProduct;
import com.coffee.entity.Product;
import com.coffee.service.CartProductService;
import com.coffee.service.MemberService;
import com.coffee.service.OrderService;
import com.coffee.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ProductService productService;
    private final CartProductService cartProductService;

    // 리액트의 카트 목록이나 '주문하기' 버튼을 눌러서 주문을 시도합니다.
    @PostMapping // CartList.js 파일의 makeOrder() 함수와 연관이 있습니다.
    public ResponseEntity<?> order(@RequestBody OrderDto dto) {
        System.out.println(dto);

        // 회원(Member) 객체 생성
        Optional<Member> optionalMember = memberService.findByMemberId(dto.getMemberId());
        if (!optionalMember.isPresent()) {
            throw new RuntimeException("회원이 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        // 혹시 마일리지 적립 시스템이면, 마일리지 적립은 여기서 하세요.

        // 주문(Order) 객체 생성
        Order order = new Order();
        // 이 사람이 주문자 입니다.
        order.setMember(member);
        order.setOrederdate(LocalDate.now()); // 주문 시점
        order.setStatus(dto.getStatus());


        // 주문 상품(OrderProduct)들은 확장 for 구문을 사용합니다.
        List<OrderProduct> orderProductList = new ArrayList<>();
        for (OrderItemDto item : dto.getOrderItems()) {
            // item은 주문하고자 하는 '주문 상품' 한개를 의미합니다.
            Optional<Product> optionalProduct = productService.findProductById(item.getProductId());

            if (!optionalProduct.isPresent()) {
                throw new RuntimeException("회원이 존재하지 않습니다.");
            }
            Product product = optionalProduct.get();

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("재고 수량이 부족합니다");
            }

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product);
            orderProduct.setQuantity(item.getQuantity());

            // 리스트 컬랙션에 각 '주문 상품'을 담아 줍니다.
            orderProductList.add(orderProduct);

            // 상품의 재고 수량 빼기
            product.setStock(product.getStock() - item.getQuantity());

            // 데이터베이스에 존재하는 카트 상품의 데이터도 지워야 합니다.
            if(item.getCartProductId() != null){
                cartProductService.delete(item.getCartProductId());
            }

        }

        order.setOrderProducts(orderProductList);

        // 주문 객체를 저장합니다
        orderService.saveOrder(order);


        String message = "주문이 완료되었습니다.";
        return ResponseEntity.ok(message);
    }

    @GetMapping("/list/{id}")
    public List<Order> orderList(@PathVariable Long id){


        Optional<Member> optionalMember = memberService.findByMemberId(id);
        if (!optionalMember.isPresent()) {
            throw new RuntimeException("회원이 존재하지 않습니다.");
        }

        Member member = optionalMember.get();

        List<Order> OrderList = orderService.findOrderListById(id);
        if(OrderList.isEmpty()){
            throw new RuntimeException("회원이 주문한 목록이 존재하지 않습니다.");
        }

        for(Order item : OrderList){
            System.out.println(item);
        }

        return OrderList;
    }

}
