package com.shop.backend.services.impl;

import com.shop.backend.dto.WishListDTO;
import com.shop.backend.models.Product;
import com.shop.backend.models.ProductOption;
import com.shop.backend.models.User;
import com.shop.backend.models.WishList;
import com.shop.backend.repository.ProductRepository;
import com.shop.backend.repository.ProductOptionRepository;
import com.shop.backend.repository.UserRepository;
import com.shop.backend.repository.WishListRepository;
import com.shop.backend.services.WishListService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishListServiceImpl implements WishListService {

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Override
    public WishList createWishList(WishListDTO wishListDTO, String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(wishListDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductOption productOption = null;
        if (wishListDTO.getOptionId() != null) {
            productOption = productOptionRepository.findById(wishListDTO.getOptionId())
                    .orElseThrow(() -> new RuntimeException("Product option not found"));
        }

        // 중복 확인 (옵션도 포함)
        if (wishListRepository.existsByUserAndProductAndProductOption(user, product, productOption)) {
            throw new RuntimeException("이미 위시리스트에 있는 상품입니다.");
        }

        WishList wishList = new WishList();
        wishList.setUser(user);
        wishList.setProduct(product);
        wishList.setProductOption(productOption);

        return wishListRepository.save(wishList);
    }

    @Override
    public List<WishList> getWishListByUser(String username) {
        // 1. 사용자 조회
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. 위시리스트 조회
        return wishListRepository.findByUser(user);
    }

    @Override
    public void deleteWishList(Long wishListId, String username) {
        // 1. 위시리스트 조회
        WishList wishList = wishListRepository.findById(wishListId)
                .orElseThrow(() -> new RuntimeException("WishList not found"));

        // 2. 사용자 조회 (권한 검증)
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. 권한 검증
        if (!wishList.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to delete this wishlist");
        }

        // 4. 위시리스트 삭제
        wishListRepository.delete(wishList);
    }

    @Override
    @Transactional
    public void deleteWishListByUserAndProduct(Long productId, String username) {
        // 1. 사용자 조회
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 3. 위시리스트에서 상품 삭제
        wishListRepository.deleteByUserAndProduct(user, product);
    }

    @Override
    public boolean isProductInWishlist(Long productId, String username) {
        return wishListRepository.existsByProduct_ProductIdAndUser_UserName(productId, username);
    }
}
