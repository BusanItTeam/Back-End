package com.shop.backend.services;

import com.shop.backend.models.*;
import com.shop.backend.repository.InventoryRepository;
import com.shop.backend.repository.ProductOptionRepository;
import com.shop.backend.repository.ProductRepository;
import com.shop.backend.repository.ProductAddImageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductAddImageRepository productAddImageRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product updateProduct(Long id, Product updatedProduct, List<ProductOption> updatedOptions, List<ProductAddImage> updatedImages) {
        // 1. 기존 Product 정보 조회
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        // 2. Product 정보 업데이트 (이미지 및 옵션 제외)
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setCategory(updatedProduct.getCategory());

        // 3. 기존 ProductAddImage 삭제 후 새로운 ProductAddImage 추가
        // 기존 이미지 삭제
        Iterator<ProductAddImage> iterator = existingProduct.getImages().iterator();
        while (iterator.hasNext()) {
            ProductAddImage productImage = iterator.next();
            try {
                Path fileToDelete = Paths.get(uploadPath, productImage.getImageUrl().substring(productImage.getImageUrl().lastIndexOf("/") + 1));
                Files.deleteIfExists(fileToDelete);
                System.out.println("Deleted file: " + fileToDelete.toString());
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + e.getMessage());
                // 파일 삭제 실패 시, 예외를 던지지 않고 로그만 남기도록 처리
            }
            iterator.remove(); // iterator를 사용하여 컬렉션에서 안전하게 삭제
            productAddImageRepository.delete(productImage); // DB에서도 삭제
        }

        // 새로운 이미지 추가
        for (ProductAddImage image : updatedImages) {
            image.setProduct(existingProduct);
            productAddImageRepository.save(image);
            existingProduct.getImages().add(image);
        }

        // 4. 기존 ProductOption 삭제 후 새로운 ProductOption 추가 (ProductService에 관련 로직 구현 필요)
        // 기존 옵션 삭제 로직 (ProductOptionService로 분리 권장)
        existingProduct.getOptions().forEach(option -> {
            if (option.getInventory() != null) {
                inventoryRepository.delete(option.getInventory());
            }
            productOptionRepository.delete(option);
        });
        existingProduct.getOptions().clear();

        // 새로운 옵션 추가 로직 (ProductOptionService로 분리 권장)
        for (ProductOption option : updatedOptions) {
            option.setProduct(existingProduct);
            ProductOption savedOption = productOptionRepository.save(option);

            Inventory inventory = option.getInventory();
            if (inventory != null) {
                inventory.setOption(savedOption);
                inventoryRepository.save(inventory);
                savedOption.setInventory(inventory);
                productOptionRepository.save(savedOption);
            }
            existingProduct.getOptions().add(option);
        }

        // 5. Product 정보 저장 및 반환
        return productRepository.save(existingProduct);
    }


    @Transactional
    public Product addProductWithOptions(Product product, List<ProductOption> options) {
        Product savedProduct = productRepository.save(product);

        List<ProductAddImage> images = product.getImages();
        for (ProductAddImage image : images) {
            image.setProduct(savedProduct);
            productAddImageRepository.save(image);
        }
        savedProduct.setImages(images);

        for (ProductOption option : options) {
            option.setProduct(savedProduct);
            ProductOption savedOption = productOptionRepository.save(option);

            Inventory inventory = option.getInventory();
            if (inventory != null) {
                inventory.setOption(savedOption); // 이 부분을 추가해야 합니다.
                inventoryRepository.save(inventory);
                savedOption.setInventory(inventory);
                productOptionRepository.save(savedOption);
            }
        }

        return savedProduct;
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            // Delete associated ProductOptions and Inventories
            product.getOptions().forEach(option -> {
                if (option.getInventory() != null) {
                    inventoryRepository.delete(option.getInventory());
                }
                productOptionRepository.delete(option);
            });

            // Delete associated ProductAddImages and files
            product.getImages().forEach(productAddImage -> {
                // 이미지 파일 삭제
                String imageUrl = productAddImage.getImageUrl();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    try {
                        Path fileToDelete = Paths.get(uploadPath, imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
                        Files.deleteIfExists(fileToDelete);
                        System.out.println("Deleted file: " + fileToDelete.toString());
                    } catch (IOException e) {
                        System.err.println("Failed to delete file: " + e.getMessage());
                        // 파일 삭제 실패 시, 예외를 던지지 않고 로그만 남기도록 처리
                    }
                }
                productAddImageRepository.delete(productAddImage);
            });

            productRepository.delete(product);
        }
    }
}
