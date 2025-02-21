package com.shop.backend.controller;

import com.shop.backend.models.Category;
import com.shop.backend.models.Product;
import com.shop.backend.models.ProductAddImage;
import com.shop.backend.repository.ProductAddImageRepository;
import com.shop.backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductAddImageRepository productAddImageRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

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

            // Product 먼저 저장
            Product savedProduct = productService.saveProduct(product);

            // 이미지 파일 처리
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // 파일 저장 로직
                String imageUrlPath = saveImage(imageUrl);
                savedProduct.setImageUrl(imageUrlPath); // DB에 저장될 이미지 경로

                // ProductAddImage 생성 및 저장
                ProductAddImage productAddImage = new ProductAddImage();
                productAddImage.setProduct(savedProduct); // 저장된 Product 연결
                productAddImage.setImageUrl(imageUrlPath);
                productAddImageRepository.save(productAddImage); // ProductAddImageRepository를 사용하여 저장
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct); // 201 Created 응답
        } catch (Exception e) {
            // 예외 처리 로깅
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error 응답
        }
    }

    // 이미지 저장 로직
    private String saveImage(MultipartFile image) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());
        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = timestamp + extension;

        // 절대 경로 사용
        Path filePath = Paths.get(uploadPath + fileName);
        System.out.println("파일주소+filePath=" + filePath);
        try {
            // 퍼블릭 폴더에 이미지 저장
            Files.copy(image.getInputStream(), filePath);
        } catch (IOException e) {
            // 파일 저장 실패 시 로그 출력
            e.printStackTrace();
            throw new IOException("Failed to save image: " + fileName, e);
        }

        // DB에 저장될 상대 경로 (리액트 public 폴더 기준)
        return "/images/" + fileName;
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
