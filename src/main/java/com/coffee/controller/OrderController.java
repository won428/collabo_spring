package com.coffee.controller;

import com.coffee.constant.OrderStatus;
import com.coffee.constant.Role;
import com.coffee.dto.OrderDto;
import com.coffee.dto.OrderItemDto;
import com.coffee.dto.OrderResponseDto;
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
        order.setOrderdate(LocalDate.now()); // 주문 시점
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

    // 특정한 회원의 주문 정보를 최신 날짜 순으로 조회합니다.
    // http://localhost:9000/order/list?memberId=회원아이디
    @GetMapping("/list") // 리액트의 OrderList.js 파일내의 useEffect 참조
    public  ResponseEntity<List<OrderResponseDto>> getOrderList(@RequestParam Long memberId, @RequestParam Role role){
        System.out.println("로그인 한 사람의 id : " + memberId);
        System.out.println("로그인 한 사람의 역할 : " + role);
        List<Order> orders = null;
        if(role == Role.ADMIN){
            System.out.println("관리자");
            orders = orderService.findAllOrderByIdDesc();
            //orders = orderService.findByStatusOrderByIdDesc(OrderStatus.PENDING);
        }else if(role == Role.USER){
            System.out.println("일반인");
            orders = orderService.findOrderByMemberId(memberId);
        }else{
            System.out.println("존재하지 않는 역할입니다.");
        }


        System.out.println("주문 건수 : " + orders.size());

        List<OrderResponseDto> responseDtos = new ArrayList<>();

        for(Order bean:orders){
            OrderResponseDto dto = new OrderResponseDto();
            // 주문의 기초 정보 셋팅
            dto.setOrderId(bean.getId());
            dto.setOrderdate(bean.getOrderdate());
            dto.setStatus(bean.getStatus().name());

            // `주문 상품` 여러개에 대한 셋팅
            List<OrderResponseDto.OrderItem> orderItems = new ArrayList<>();

            for (OrderProduct item : bean.getOrderProducts()){
                OrderResponseDto.OrderItem items = new OrderResponseDto.OrderItem(item.getProduct().getName(), item.getQuantity());

                orderItems.add(items);
            }

            dto.setOrderItems(orderItems);

            responseDtos.add(dto);
        }

        return ResponseEntity.ok(responseDtos);
    }


    @GetMapping("/update/{orderId}")
    public String ddd(@PathVariable Long orderId){
        System.out.println("수정할 항목 : " + orderId);
        return null ;
    }

    @PutMapping("/update/status/{orderId}")
    public ResponseEntity<String> statusChange(@PathVariable Long orderId, @RequestParam OrderStatus status){
        System.out.println("수정할 항목의 아이디 : " + orderId);
        System.out.println("변경하고자 하는 주문 상태 : " + status);


        int affected = -1;
        affected = orderService.updateOrderStatus(orderId, status);
        System.out.println("데이터 베이스에 반영이 된 행 개수 : " + affected);

        String message = "송장 번호" + orderId + "의 주문 상태가 변경이 되었습니다.";
        return ResponseEntity.ok("");
    }

    //관지라 또는 주문자가 보낸 주문에 대한 삭제 요청을 진행합니다.
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId){
        if(!orderService.existsById(orderId)){
            return ResponseEntity.notFound().build();
        }



        // 여기서 부터 재고 수량 증가를 위한 코드입니다.
        Optional<Order> orderOptional = orderService.findOrderById(orderId);
        if(orderOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Order order = orderOptional.get();

        if(order.getStatus() == OrderStatus.COMPLETED){
            String message = "이미 완료된 주문은 취소할수 없습니다.";
            return ResponseEntity.badRequest().body(message);
        }
        // 주문 상품을 반복하면서 재고 수량을 더해 줍니다.
        for(OrderProduct op : order.getOrderProducts()){
            Product product = op.getProduct();
            int quantity = op.getQuantity();

            product.setStock((product.getStock() + quantity));

            productService.save(product);
            // 기존 재고에 주문 취소된 수량을 다시 더해 줍니다
        }

        orderService.deleteById(orderId);

        String message = "주문이 취소 되었습니다.";
        return ResponseEntity.ok(message);
    }



    /*
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
    }  */

}
