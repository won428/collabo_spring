package com.coffee.service;

import com.coffee.dto.SearchDto;
import com.coffee.entity.Product;
import com.coffee.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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


    public Page<Product> ListProducts(Pageable pageable) {
        return this.productRepository.findAll(pageable);
    }

    //필드 검색 조건과 페이징 기본 정보를 사용하여 상품 목록을 조회하는 로직을 작성합니다.
    public Page<Product> ListProducts(SearchDto searchDto, int pageNumber, int pageSize){

        Specification<Product> spec = Specification.where(null); // null은 현재 어떠한 조건도 없음을 의미합니다.

        // 기간 검색 콤보 박스의 조건 추가하기
        if(searchDto.getSearchDateType() != null){
            spec = spec.and(ProductSpecification.hasDateRange(searchDto.getSearchDateType()));
        }
        // 카테고리 조건 추가하기
        if(searchDto.getCategory() != null){
            spec = spec.and(ProductSpecification.hasCategory(searchDto.getCategory()));
        }
        // 검색 모드에 따른 조건 추가하기(name 또는 description)
        String searchMode = searchDto.getSearchMode();
        String searchKeyword = searchDto.getSearchKeyword();

        if(searchMode != null && searchKeyword != null){
            if("name".equals(searchMode)){
                spec = spec.and(ProductSpecification.hasNameLike(searchKeyword));
            }else if("description".equals(searchMode)){
                spec = spec.and(ProductSpecification.hasDescriptionLike(searchKeyword));
            }
        }

        // 상품의 id를 역순으로 정렬하기
        Sort sort = Sort.by(Sort.Order.desc("id"));
        // pageNumber(0 base) 페이지를 보여주는데, pageSize개씩 보여주고 sort대로 정렬한다.
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sort);

        return this.productRepository.findAll(spec, pageable);
    }
}
