package com.coffee.service;

import com.coffee.entity.Product;
import com.coffee.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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


    public void save(Product product) {
        // save() 메소드는 CrudRepository에 포함되어 있습니다.
        this.productRepository.save(product);
    }

    public Product getProductBy(Long id) {
        // findByid() 메소드는 CrudRepository에 포함되어 있습니다.
        // 그리고, Optional<>을 반환합니다.
        // Optional : 해당 상품이 있을 수도 있지만, 경우에 따라서 없을 수도 있습니다.
        Optional<Product> product = this.productRepository.findById(id);

        // 의미 있는 데이터면 그냥 넘기고, 그렇지 않으면 null을 반환해 줍니다.
        return product.orElse(null);
    }


    public Optional<Product> findById(Long id) {
        return this.productRepository.findById(id);
    }

    public Optional<Product> findProductById(Long productId) {
        return this.productRepository.findById(productId);
    }

    public List<Product> getProductsByFilter(String filter) {
        if(filter != null && !filter.isEmpty()){
            return productRepository.findByImageContaining(filter);
        }
        return productRepository.findAll();
    }
}
