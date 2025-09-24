package com.coffee.service;

import com.coffee.entity.Product;
import com.coffee.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // 상품에 대한 여러가지 로직 정보를 처리해주는 서비스 클래스
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getProductList() {
        return this.productRepository.findAllByOrderByIdDesc();
    }


    public boolean deleteProduct(Long id) {
        // existsById(), deleteById()는 CrudRepository에 포함되어 있습니다.
        if(productRepository.existsById(id)){
            this.productRepository.deleteById(id);

            return true; // true의 의미는 "삭제 성공"을 의미합니다.
             }else {
            return false;
        }
    }
}
