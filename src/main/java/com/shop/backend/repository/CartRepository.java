package com.shop.backend.repository;

import com.shop.backend.models.Cart;
import com.shop.backend.models.Product;
import com.shop.backend.models.ProductOption;
import com.shop.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);

    Optional<Cart> findByUserAndProductAndProductOption(User user, Product product, ProductOption productOption);


    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user = :user AND c.product.id IN :productIds")
    void deleteByUserAndProductIds(@Param("user" + "")
                                   User user, @Param("productIds") List<Long> productIds);
}
