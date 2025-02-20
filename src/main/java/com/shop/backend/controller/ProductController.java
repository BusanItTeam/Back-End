package com.shop.backend.controller;

import com.shop.backend.models.Category;
import com.shop.backend.models.Product;
import com.shop.backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> addProduct(
            @RequestPart("name") String name,
            @RequestPart("price") String price,
            @RequestPart("stock") String stock,
            @RequestPart("description") String description,
            @RequestPart("categoryId") String categoryId,
            @RequestPart(value = "imageUrl", required = false) MultipartFile imageUrl
    ) throws IOException {
        try {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(new BigDecimal(price));
            product.setStock(Integer.parseInt(stock));

            // 카테고리 설정
            Category category = new Category();
            category.setCategoryId(Long.parseLong(categoryId));
            product.setCategory(category);

            // 이미지 파일 처리
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // 파일 저장 로직 (예시) - 실제 구현 필요
                String imageUrlPath = saveImage(imageUrl);
                product.setImageUrl(imageUrlPath); // DB에 저장될 이미지 경로
            }

            Product savedProduct = productService.saveProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct); // 201 Created 응답
        } catch (Exception e) {
            // 예외 처리 로깅
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error 응답
        }
    }

    // 이미지 저장 로직 (실제 구현 필요)
    private String saveImage(MultipartFile image) throws IOException {
        // TODO: 실제 파일 저장 및 경로 반환 로직 구현
        // 예시:
        // String uploadDir = "/path/to/upload/directory";
        // String filePath = uploadDir + "/" + image.getOriginalFilename();
        // image.transferTo(new File(filePath));
        return "임시_이미지_경로"; // 임시 경로 반환
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.updateProduct(id, product)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
