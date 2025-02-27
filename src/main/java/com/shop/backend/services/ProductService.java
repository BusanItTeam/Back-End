package com.shop.backend.services;

import com.shop.backend.models.Inventory;
import com.shop.backend.models.Product;
import com.shop.backend.models.ProductAddImage;
import com.shop.backend.models.ProductOption;
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

    public Optional<Product> updateProduct(Long id, Product product) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setCategory(product.getCategory());
                    return productRepository.save(existingProduct);
                });
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
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
}
