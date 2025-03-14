package com.shop.backend.services;

import com.shop.backend.models.*;
import com.shop.backend.repository.*;
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
import java.util.stream.Collectors;

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

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

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

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Transactional
    public Product updateProduct(Long id, Product updatedProduct, List<ProductOption> updatedOptions,
                                 List<ProductAddImage> updatedImages) {
        // 1. 기존 Product 정보 조회
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        // 2. Product 정보 업데이트 (이미지 및 옵션 제외)
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setDiscountRate(updatedProduct.getDiscountRate()); // 할인율 업데이트

        // 3. 기존 ProductAddImage 삭제 후 새로운 ProductAddImage 추가
        // 기존 이미지 삭제
        Iterator<ProductAddImage> imageIterator = existingProduct.getImages().iterator();
        while (imageIterator.hasNext()) {
            ProductAddImage productImage = imageIterator.next();
            try {
                Path fileToDelete = Paths.get(uploadPath,
                        productImage.getImageUrl().substring(productImage.getImageUrl().lastIndexOf("/") + 1));
                Files.deleteIfExists(fileToDelete);
                System.out.println("Deleted file: " + fileToDelete.toString());
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + e.getMessage());
                // 파일 삭제 실패 시, 예외를 던지지 않고 로그만 남기도록 처리
            }
            imageIterator.remove(); // iterator를 사용하여 컬렉션에서 안전하게 삭제
            productAddImageRepository.delete(productImage); // DB에서도 삭제
        }

        // 새로운 이미지 추가
        for (ProductAddImage image : updatedImages) {
            image.setProduct(existingProduct);
            productAddImageRepository.save(image);
            existingProduct.getImages().add(image);
        }

        // 4. 기존 ProductOption 삭제 및 새로운 ProductOption 추가
        Iterator<ProductOption> optionIterator = existingProduct.getOptions().iterator();
        while (optionIterator.hasNext()) {
            ProductOption option = optionIterator.next();

            // WishList에서 optionId를 참조하는 데이터 삭제
            wishListRepository.deleteByProductOption(option);

            if (option.getInventory() != null) {
                inventoryRepository.delete(option.getInventory());
            }
            productOptionRepository.delete(option);
            optionIterator.remove(); // iterator를 사용하여 안전하게 삭제
        }
        existingProduct.getOptions().clear();

        // 새로운 옵션 추가
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
        // 위시리스트에 있는 상품 정보 업데이트
        List<WishList> wishLists = wishListRepository.findByProduct(existingProduct);
        for (WishList wishList : wishLists) {
            wishList.setProduct(existingProduct); // 업데이트된 상품으로 설정
            wishListRepository.save(wishList);
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
                inventory.setOption(savedOption);
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
    public List<Product> getBestSellingProducts(int limit) {
        List<Object[]> bestSellingProducts = orderDetailRepository.findAllBestSellingProducts();
        List<Long> productIds = bestSellingProducts.stream()
                .map(array -> ((Product) array[0]).getProductId())
                .limit(limit)
                .collect(Collectors.toList());
        return productRepository.findProductsByIds(productIds);
    }

    public List<Product> getBestSellingProductsByCategory(String categoryName, int limit) {
        if ("all".equalsIgnoreCase(categoryName)) {
            return getBestSellingProducts(limit);
        } else {
            List<Object[]> bestSellingProducts = orderDetailRepository.findTopNBestSellingProductsByCategory(categoryName, limit);
            List<Long> productIds = bestSellingProducts.stream()
                    .map(array -> ((Product) array[0]).getProductId())
                    .collect(Collectors.toList());
            return productRepository.findProductsByIds(productIds);
        }
    }

}
