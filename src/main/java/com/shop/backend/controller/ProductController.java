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
import java.util.Optional;
import java.util.ArrayList;
import java.util.UUID;

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
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles
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
            if (imageFiles != null && !imageFiles.isEmpty()) {
                List<ProductAddImage> images = new ArrayList<>();
                for (MultipartFile imageFile : imageFiles) {
                    String imageUrlPath = saveImage(imageFile);
                    ProductAddImage productAddImage = new ProductAddImage();
                    productAddImage.setProduct(savedProduct); // 저장된 Product 연결
                    productAddImage.setImageUrl(imageUrlPath);
                    productAddImageRepository.save(productAddImage); // ProductAddImageRepository를 사용하여 저장
                    images.add(productAddImage);
                }
                savedProduct.setImages(images);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    private String saveImage(MultipartFile image) throws IOException {
        String uniqueFileName = UUID.randomUUID().toString();
        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = uniqueFileName + extension;

        Path filePath = Paths.get(uploadPath + fileName);
        System.out.println("파일주소+filePath=" + filePath);
        try {
            Files.copy(image.getInputStream(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Failed to save image: " + fileName, e);
        }

        return "/images/" + fileName;
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestPart("name") String name,
            @RequestPart("price") String price,
            @RequestPart("stock") String stock,
            @RequestPart("description") String description,
            @RequestPart("categoryId") String categoryId,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles
    ) throws IOException {
        try {
            Optional<Product> productOptional = productService.getProductById(id);
            if (!productOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Product existingProduct = productOptional.get();
            existingProduct.setName(name);
            existingProduct.setPrice(new BigDecimal(price));
            existingProduct.setStock(Integer.parseInt(stock));
            existingProduct.setDescription(description);

            Category category = new Category();
            category.setCategoryId(Long.parseLong(categoryId));
            existingProduct.setCategory(category);

            // 기존 이미지 삭제 로직 (선택적)
            if (imageFiles != null && !imageFiles.isEmpty()) {
                // 기존 이미지 파일 삭제
                if (existingProduct.getImages() != null) {
                    for (ProductAddImage productImage : existingProduct.getImages()) {
                        try {
                            Path fileToDelete = Paths.get(uploadPath, productImage.getImageUrl().substring(productImage.getImageUrl().lastIndexOf("/") + 1));
                            Files.deleteIfExists(fileToDelete);
                            productAddImageRepository.delete(productImage);
                            System.out.println("Deleted file: " + fileToDelete.toString());
                        } catch (IOException e) {
                            System.err.println("Failed to delete file: " + e.getMessage());
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                        }
                    }
                    existingProduct.getImages().clear();
                }


                List<ProductAddImage> images = new ArrayList<>();
                for (MultipartFile imageFile : imageFiles) {
                    String imageUrlPath = saveImage(imageFile);
                    ProductAddImage productAddImage = new ProductAddImage();
                    productAddImage.setProduct(existingProduct);
                    productAddImage.setImageUrl(imageUrlPath);
                    productAddImageRepository.save(productAddImage);
                    images.add(productAddImage);
                }
                existingProduct.setImages(images);
            }


            Product updatedProduct = productService.saveProduct(existingProduct);
            return ResponseEntity.ok(updatedProduct);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        Optional<Product> productOptional = productService.getProductById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            // 이미지 삭제
            if (product.getImages() != null) {
                for (ProductAddImage productImage : product.getImages()) {
                    try {
                        Path fileToDelete = Paths.get(uploadPath, productImage.getImageUrl().substring(productImage.getImageUrl().lastIndexOf("/") + 1));
                        Files.deleteIfExists(fileToDelete);
                        productAddImageRepository.delete(productImage);
                        System.out.println("Deleted file: " + fileToDelete.toString());
                    } catch (IOException e) {
                        System.err.println("Failed to delete file: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                }
            }

            boolean isDeleted = productService.deleteProduct(id);
            if (isDeleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
