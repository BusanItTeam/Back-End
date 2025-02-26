package com.shop.backend.services;

import com.shop.backend.models.Product;
import com.shop.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

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
                    // existingProduct.setImageUrl(product.getImageUrl()); // 이 줄을 제거
                    existingProduct.setCategory(product.getCategory());
                    existingProduct.setStock(product.getStock());
                    existingProduct.setDescription(product.getDescription());
                    return productRepository.save(existingProduct);
                });
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return true;
                })
                .orElse(false);
    }
}
