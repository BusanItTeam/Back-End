package com.shop.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.backend.models.*;
import com.shop.backend.repository.CategoryRepository;
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
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductAddImageRepository productAddImageRepository;

    @Autowired
    private CategoryRepository categoryRepository;

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
            @RequestPart("description") String description,
            @RequestPart("categoryId") String categoryId,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestPart("options") String optionsJson, // JSON array of options
            @RequestPart(value = "discountRate", required = false) String discountRate // 할인율 추가
    ) throws IOException {
        try {
            Optional<Category> categoryOptional = categoryRepository.findById(Long.parseLong(categoryId));
            if (!categoryOptional.isPresent()) {
                return ResponseEntity.badRequest().build();
            }

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(new BigDecimal(price));

            // 할인율 설정
            if (discountRate != null && !discountRate.isEmpty()) {
                product.setDiscountRate(new BigDecimal(discountRate));
            } else {
                product.setDiscountRate(null); // or BigDecimal.ZERO if you prefer
            }

            Category category = categoryOptional.get();
            product.setCategory(category);

            List<ProductAddImage> images = new ArrayList<>();
            if (imageFiles != null && !imageFiles.isEmpty()) {
                for (MultipartFile imageFile : imageFiles) {
                    String imageUrlPath = saveImage(imageFile);
                    ProductAddImage productAddImage = new ProductAddImage();
                    productAddImage.setImageUrl(imageUrlPath);
                    productAddImage.setProduct(product); // Product 설정!!!
                    images.add(productAddImage);
                }
            }
            product.setImages(images);

            List<ProductOption> options = parseOptions(optionsJson, product);
            product.setOptions(options);

            Product savedProduct = productService.addProductWithOptions(product, options);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private List<ProductOption> parseOptions(String optionsJson, Product product) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> optionsList = objectMapper.readValue(optionsJson, new TypeReference<List<Map<String, Object>>>() {});

        List<ProductOption> options = new ArrayList<>();
        for (Map<String, Object> optionMap : optionsList) {
            ProductOption option = new ProductOption();
            option.setProduct(product);
            option.setColor((String) optionMap.get("color"));
            option.setSize((String) optionMap.get("size"));

            Inventory inventory = new Inventory();
            inventory.setStock(Integer.parseInt(String.valueOf(optionMap.get("stock"))));
            option.setInventory(inventory);

            options.add(option);
        }

        return options;
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
            @RequestPart("description") String description,
            @RequestPart("categoryId") String categoryId,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestPart("options") String optionsJson,
            @RequestPart(value = "discountRate", required = false) String discountRate // 할인율 추가
    ) throws IOException {
        try {
            Optional<Category> categoryOptional = categoryRepository.findById(Long.parseLong(categoryId));
            if (!categoryOptional.isPresent()) {
                return ResponseEntity.badRequest().build();
            }

            // 1. 업데이트할 Product 생성 및 정보 설정
            Product updatedProduct = new Product();
            updatedProduct.setName(name);
            updatedProduct.setDescription(description);
            updatedProduct.setPrice(new BigDecimal(price));

            // 할인율 설정
            if (discountRate != null && !discountRate.isEmpty()) {
                updatedProduct.setDiscountRate(new BigDecimal(discountRate));
            } else {
                updatedProduct.setDiscountRate(null); // or BigDecimal.ZERO if you prefer
            }

            Category category = categoryOptional.get();
            updatedProduct.setCategory(category);

            // 2. 업데이트할 ProductOption 생성 및 정보 설정
            List<ProductOption> updatedOptions = parseOptions(optionsJson, updatedProduct);

            // 3. 업데이트할 ProductAddImage 생성 및 정보 설정
            List<ProductAddImage> updatedImages = new ArrayList<>();
            if (imageFiles != null && !imageFiles.isEmpty()) {
                for (MultipartFile imageFile : imageFiles) {
                    String imageUrlPath = saveImage(imageFile);
                    ProductAddImage productAddImage = new ProductAddImage();
                    productAddImage.setImageUrl(imageUrlPath);
                    productAddImage.setProduct(updatedProduct);
                    updatedImages.add(productAddImage);
                }
            }

            // 4. ProductService를 통해 업데이트 수행
            Product updatedProductResult = productService.updateProduct(id, updatedProduct, updatedOptions, updatedImages);

            return ResponseEntity.ok(updatedProductResult);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            Optional<Product> productOptional = productService.getProductById(id);
            if (!productOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Product product = productOptional.get();

            // 이미지 파일 삭제 로직 추가
            if (product.getImages() != null) {
                for (ProductAddImage productImage : product.getImages()) {
                    try {
                        Path fileToDelete = Paths.get(uploadPath, productImage.getImageUrl().substring(productImage.getImageUrl().lastIndexOf("/") + 1));
                        Files.deleteIfExists(fileToDelete);
                        System.out.println("Deleted file: " + fileToDelete.toString());
                    } catch (IOException e) {
                        System.err.println("Failed to delete file: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                }
            }

            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
