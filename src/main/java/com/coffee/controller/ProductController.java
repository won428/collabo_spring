package com.coffee.controller;

import com.coffee.constant.Category;
import com.coffee.dto.SearchDto;
import com.coffee.entity.Product;
import com.coffee.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Value("${productImageLocation}")
    private String productImageLocation ; // 기본값 null

    @Autowired
    private ProductService productService;

//    @GetMapping("/list") // 상품 목록을 List 컬렉션으로 반환해 줍니다.
//    public List<Product> list(){
//        List<Product> products = this.productService.getProductList();
//        return products;
//    }
    // 클라이언트가 특정 상품 id에 대하여 "삭제" 요청을하엿습니다.
    // @PathVariable은 URL의 경로 변수를 메소드의 매개변수로 값을 전달해 줍니다.
    @DeleteMapping("/delete/{id}") // {id}는 경로 변수라고 하며, 가변 매개변수로 이해하면 됩니다.
    public ResponseEntity<String> delete(@PathVariable Long id){  // {id}로 넘겨온 상품의 아이디가, 변수 id에 할당됩니다.
        try {
            boolean isDelete = this.productService.deleteProduct(id);

            if(isDelete){
                return ResponseEntity.ok( id +"번 상품이 삭제 되었습니다.");
            }else {
                ResponseEntity.badRequest().body(id +"번 상품이 존재하지 않습니다.");
            }
        }catch (Exception err){
            return ResponseEntity.internalServerError().body("오류 발생 : " + err.getMessage());
        }

        return null;
    };

    // 상품 등록하기
    @PostMapping("/insert")
    public ResponseEntity<?> insert(@RequestBody Product product){
        // @RequestBody http를 사용하여 넘어온 데이터을 자바 객체 형식으로 변환해줍니다

        // 데이터 베이스와 이미지 경로에 저장될 이미지의 이름
        String imageFilename = "product_" + System.currentTimeMillis() +".jpg";

        // String 클래스 공부 : endsWith(), split() 메소드

        // 폴더 구분자가 제대로 설정 되어 있으면 그대로 사용합니다.
        // 그렇지 않으면 폴더 구분자를 붙여 줍니다.
        // File.separator : 폴더 구분자를 의미하며 리눅스는 /, 윈도우는 \입니다.
        String pathName = productImageLocation.endsWith("\\") || productImageLocation.endsWith("/")
                ? productImageLocation
                : productImageLocation + File.separator;

        File imageFile = new File(pathName + imageFilename);

        String imageData = product.getImage(); // Bass64 인코딩 문자열(엄청김)

        try {
            // 파일 정보를 byte단위로 변환하여 이미지를 복사합니다.
            FileOutputStream fos = new FileOutputStream(imageFile);

            // 메소드 체이닝 : 점을 연속적으로 찍어서 메소드를 계속 호출하는 것
            byte[] decodedImage = Base64.getDecoder().decode(imageData.split(",")[1]);
            fos.write(decodedImage); // 바이트 파일을 해당 이미지 경로에 복사하기

            product.setImage(imageFilename);
            product.setInputdate(LocalDate.now());

            this.productService.save(product);

            return ResponseEntity.ok(Map.of("message","Product insert successfully","image",imageFilename));
        }catch (Exception err){
            return ResponseEntity.status(500).body(Map.of("message",err.getMessage(),"error","Error file uploading"));
        }

    };
   // 프론트 앤드의 상품 수정 페이지에서 요청이 들어왔습니다.
    @GetMapping("/update/{id}") // 상품의 id정보를 이용하여 해당 상품 bean 객체를 반환해 줍니다.
    public ResponseEntity<Product> getUpdate(@PathVariable Long id){
        System.out.println("수정할 상품 번호");
        Product product = this.productService.getProductBy(id);
        if(product == null){ // 상품이 없으면 404 응답과 함께 null을 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }else {// 해당 상품의 정보와 함께, 성공(200) 메세지를 반환 합니다.
            return ResponseEntity.ok(product);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> putUpdate(@PathVariable Long id, @RequestBody Product updatedProduct) {
        Optional<Product> findProduct = productService.findById(id);
        if (findProduct.isEmpty()) { // 상품이 존재하지 않으면 404 응답 반환
            return ResponseEntity.notFound().build();
        } else { // 상품이 있습니다.
            // Optional에서 실제 상품 정보 끄집어 내기
            Product savedProduct = findProduct.get();

            try { // 이전 이미지 객체에 새로운 이미지 객체 정보를 업데이트합니다.
                savedProduct.setName(updatedProduct.getName());
                savedProduct.setPrice(updatedProduct.getPrice());
                savedProduct.setCategory(updatedProduct.getCategory());
                savedProduct.setStock(updatedProduct.getStock());
                savedProduct.setDescription(updatedProduct.getDescription());
                // savedProduct.setInputdate(LocalDate.now());

                // 이미지가 의미 있는 문자열로 되어 있고, Base64 인코딩 형식이라면 이미지 이름을 변경합니다.
                if (updatedProduct.getImage() != null && updatedProduct.getImage().startsWith("data:image")) {
                    String imageFileName = savedProductImage(updatedProduct.getImage());
                    savedProduct.setImage(imageFileName);
                }

                this.productService.save(savedProduct); // 서비스를 통하여 데이터 베이스에 저장합니다.

                return ResponseEntity.ok(Map.of("message", "상품 수정 성공"));
            } catch (Exception err) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message",err.getMessage(),"error","Error product update failed"));
            }
        }
    }


    // Base64 인코딩 문자열을 반환하여 이미지로 만들고, 저장해주는 메소드입니다.
    private String savedProductImage(String base64Image) {
    // base64Image : JavaScript FileReader API에 만들어진 이미지 입니다.
        String imageFilename = "product_" + System.currentTimeMillis() + ".jpg";
        // 폴더 구분자가 제대로 설정 되어 있으면 그대로 사용합니다.
        // 그렇지 않으면 폴더 구분자를 붙여 줍니다.
        // File.separator : 폴더 구분자를 의미하며 리눅스는 /, 윈도우는 \입니다.
        String pathName = productImageLocation.endsWith("\\") || productImageLocation.endsWith("/")
                ? productImageLocation
                : productImageLocation + File.separator;

        File imageFile = new File(pathName + imageFilename);

        byte[] decodedImage = Base64.getDecoder().decode(base64Image.split(",")[1]);

        try { // FileOutputStream은 바이트 파일을 처리해주는 자바의 Stream 클래스
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(decodedImage);

            return imageFilename;

        }catch (Exception ex){
            ex.printStackTrace();
            return base64Image;
        }
    }
    @GetMapping("/detail/{id}")
    public ResponseEntity<Product> detailProduct(@PathVariable Long id){
            Product product = this.productService.getProductBy(id);

            if(product == null){ // 404 응답
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }else { // 200 ok 응답
                return ResponseEntity.ok(product);
            }
     }

    @GetMapping // 홈페이지에 보여줄 큰 이미지들에 대한 정보들을 읽어옵니다.
    public List<Product> getBigsizeProducts(@RequestParam(required = false) String filter){
        return productService.getProductsByFilter(filter);
    }


//    @GetMapping("/list") // 페이징 관련 파라미터를 사용하여 상품목록 조회
//    public ResponseEntity<Page<Product>> ListProducts(
//            @RequestParam(defaultValue = "0") int pageNumber,
//            @RequestParam(defaultValue = "6") int pageSize
//    ){
//        System.out.println("pageNumber : " + pageNumber + ", pageSize : " + pageSize);
//
//        // 현재 페이지는 pageNumber이고, 페이지당 보여줄 갯수 pageSize를 사용하여 Pageable 페이지를 구합니다.
//        // 상품 번호가 큰 것부터 정렬합니다.
//        Sort mysort = Sort.by(Sort.Direction.DESC, "id");
//        Pageable pageable = PageRequest.of(pageNumber, pageSize, mysort );
//
//        Page<Product> productPage = productService.ListProducts(pageable);
//
//        return ResponseEntity.ok(productPage);
//    }

    // 필드 검색 조건과 페이징 관련 파라미터를 사용하여 상품 목록을 조회합니다.
    @GetMapping("/list") // 페이징 관련 파라미터를 사용하여 상품목록 조회
    public ResponseEntity<Page<Product>> ListProducts(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "6") int pageSize,
            @RequestParam(defaultValue = "all") String searchDateType,
            @RequestParam(defaultValue = "") Category category,
            @RequestParam(defaultValue = "") String searchMode,
            @RequestParam(defaultValue = "") String searchKeyword
    ){

        SearchDto searchDto = new SearchDto(searchDateType, category, searchMode, searchKeyword);

        Page<Product> products = productService.ListProducts(searchDto, pageNumber, pageSize);

        System.out.println("검색 조건 : " + searchDto);
        System.out.println("총 상품 개수 : " + products.getTotalElements());
        System.out.println("총 페이지 번호 : " + products.getTotalPages());
        System.out.println("현재 페이지 번호 : " + products.getNumber());

        // http 응답 코드 200과 함께 상품 정보를 json 형태로 반환해 줍니다.
        return ResponseEntity.ok(products);
    }
}
