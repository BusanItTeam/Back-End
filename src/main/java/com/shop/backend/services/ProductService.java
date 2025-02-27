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
import org.springframework.stereotype.Service;

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

            // Delete associated ProductAddImages
            product.getImages().forEach(productAddImage -> {
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
