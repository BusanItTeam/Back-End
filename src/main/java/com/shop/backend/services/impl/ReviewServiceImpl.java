package com.shop.backend.services.impl;

import com.shop.backend.dto.ReviewDTO;
import com.shop.backend.models.*;
import com.shop.backend.repository.*;
import com.shop.backend.services.ReviewService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderDetailRepository orderItemRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductRepository productRepository;


    public ReviewServiceImpl(ReviewRepository reviewRepository, OrderDetailRepository orderItemRepository, UserRepository userRepository, ProductOptionRepository productOptionRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.orderItemRepository = orderItemRepository;
        this.productOptionRepository = productOptionRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Review createReview(User user, Product product, ReviewDTO reviewDTO) {
        // ✅ 옵션 존재 확인
        ProductOption option = productOptionRepository.findById(reviewDTO.getOptionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 옵션을 찾을 수 없습니다."));

        // ✅ 사용자가 해당 상품을 주문했는지 확인
        boolean hasPurchased = orderItemRepository.existsByOrder_UserAndProduct(user, product);
        if (!hasPurchased) {
            throw new IllegalArgumentException("이 제품을 주문한 후에만 리뷰를 작성할 수 있습니다.");
        }

        // ✅ 동일한 옵션에 대한 리뷰 중복 검사 (기존 코드 수정)
        if (reviewRepository.existsByUserUserIdAndProductProductIdAndProductOptionOptionId(
                user.getUserId(), product.getProductId(), option.getOptionId())) {
            throw new IllegalArgumentException("이미 이 제품 옵션에 대한 리뷰를 작성하셨습니다.");
        }

        // ✅ 리뷰 저장
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setProductOption(option);
        review.setRating(Rating.fromInt(reviewDTO.getRating()));
        review.setContent(reviewDTO.getContent());
        review.setImageUrl(String.join(",", reviewDTO.getImageUrls())); // 이미지 URL 문자열 변환

        return reviewRepository.save(review);
    }


    @Override
    public List<ReviewDTO> getUserReviews(User user) {
        List<Review> reviews = reviewRepository.findByUser(user);

        return reviews.stream().map(review -> {
            ReviewDTO dto = new ReviewDTO();
            dto.setReviewId(review.getReviewId());
            dto.setProductId(review.getProduct().getProductId());
            dto.setOptionId(review.getProductOption().getOptionId());
            dto.setRating(review.getRating().ordinal()); // Enum -> 숫자로 변환
            dto.setContent(review.getContent());


            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Review updateReview(User user, Long reviewId, ReviewDTO reviewDTO) {
        // 1️⃣ 해당 리뷰가 존재하는지 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        // 2️⃣ 현재 로그인한 사용자가 해당 리뷰의 작성자인지 검증
        if (!review.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        // 3️⃣ 리뷰 내용 업데이트
        review.setRating(Rating.fromInt(reviewDTO.getRating()));
        review.setContent(reviewDTO.getContent());

        // 4️⃣ 이미지 변경 처리
        if (reviewDTO.getImageUrls() != null && !reviewDTO.getImageUrls().isEmpty()) {
            review.setImageUrl(String.join(",", reviewDTO.getImageUrls()));
        }

        return reviewRepository.save(review);
    }

    @Override
    public List<ReviewDTO> getReviewsByProduct(Product product) {
        List<Review> reviews = reviewRepository.findByProduct(product);
        return reviews.stream()
                .map(review -> new ReviewDTO(review))
                .collect(Collectors.toList());
    }

}
