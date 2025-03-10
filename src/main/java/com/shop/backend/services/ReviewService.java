package com.shop.backend.services;

import com.shop.backend.dto.ReviewDTO;
import com.shop.backend.models.Product;
import com.shop.backend.models.Rating;
import com.shop.backend.models.Review;
import com.shop.backend.models.User;
import com.shop.backend.repository.ProductRepository;
import com.shop.backend.repository.ReviewRepository;
import com.shop.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public Review addReview(Long userId, ReviewDTO reviewDTO) {
        // User 객체 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // Product 객체 조회
        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // Review 객체 생성 및 설정
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setContent(reviewDTO.getContent());

        // 정수를 Rating enum으로 변환하여 설정
        review.setRating(Rating.fromInt(reviewDTO.getRating()));

        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProduct_ProductId(productId);
    }
}
