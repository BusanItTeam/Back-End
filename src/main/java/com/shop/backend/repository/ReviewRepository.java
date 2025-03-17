package com.shop.backend.repository;

import com.shop.backend.models.Product;
import com.shop.backend.models.Review;
import com.shop.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {


    boolean existsByUserUserIdAndProductProductIdAndProductOptionOptionId(Long userId, Long productId, Long optionId);

    List<Review> findByUser(User user);


    List<Review> findByProduct(Product product);
}
