package com.coffee.test;

import com.coffee.common.GenerateData;
import com.coffee.entity.Product;
import com.coffee.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ProductTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 이미지 추가하기")
    public void createProuductMany(){
     // 특정한 폴더 내에 들어 있는 상품 이미지들을 이용하여 상품 테이블에 이미지 데이터를 추가합니다.
        GenerateData gendata = new GenerateData();

        List<String> imageNameList = gendata.getImageFileNames();
        System.out.println("총 이미지 개수 : " + imageNameList.size());
        // 반복문을 사용하셔 데이터 베이스에 각각 추가합니다.
        for (int i = 0; i < imageNameList.size(); i++) {
            Product bean = gendata.createProduct(i, imageNameList.get(i));
            //System.out.println(bean);
            this.productRepository.save(bean);
        }
    }



}
