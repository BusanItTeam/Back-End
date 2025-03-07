package com.shop.backend.repository;

import com.shop.backend.models.Product;
import com.shop.backend.models.ProductOption;
import com.shop.backend.models.User;
import com.shop.backend.models.WishList;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM WishList w WHERE w.productOption = :productOption")
    void deleteByProductOption(@Param("productOption") ProductOption productOption);

    List<WishList> findByProduct(Product product);

    boolean existsByProduct_ProductIdAndUser_UserName(Long productId, String username);

    boolean existsByUserAndProductAndProductOption(User user, Product product, ProductOption productOption);

    List<WishList> findByUser(User user);
    void deleteByUserAndProduct(User user, Product product);
}
