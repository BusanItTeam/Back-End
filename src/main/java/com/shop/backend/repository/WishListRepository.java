package com.shop.backend.repository;

import com.shop.backend.models.Product;
import com.shop.backend.models.ProductOption;
import com.shop.backend.models.User;
import com.shop.backend.models.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    // 특정 사용자의 위시리스트 조회
    List<WishList> findByUser(User user);

    // 사용자 및 상품으로 위시리스트 존재 여부 확인 (중복 방지)
    boolean existsByUserAndProduct(User user, Product product);

    // 사용자 및 상품으로 위시리스트 삭제
    void deleteByUserAndProduct(User user, Product product);

    // 사용자 ID로 위시리스트 항목을 가져오는 메서드 추가
    @Query("SELECT wl FROM WishList wl WHERE wl.user.userId = :userId")
    List<WishList> findByUser_UserId(Long userId);

    boolean existsByProduct_ProductIdAndUser_UserName(Long productId, String username);

    boolean existsByUserAndProductAndProductOption(User user, Product product, ProductOption productOption);
    void deleteByUserAndProductAndProductOption(User user, Product product, ProductOption productOption);
}
