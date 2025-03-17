package com.shop.backend.controller;

import com.shop.backend.dto.ReviewDTO;
import com.shop.backend.models.Product;
import com.shop.backend.models.Rating;
import com.shop.backend.models.Review;
import com.shop.backend.models.User;
import com.shop.backend.repository.ProductRepository;
import com.shop.backend.repository.ReviewRepository;
import com.shop.backend.repository.UserRepository;
import com.shop.backend.security.services.UserDetailsImpl;
import com.shop.backend.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private  ReviewService reviewService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/user")
    public ResponseEntity<?> getUserReviews(Authentication authentication) {
        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUserName(username);

        if(userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자를 찾을수없습니다.");
        }
        List<ReviewDTO> reviews = reviewService.getUserReviews(userOpt.get());
        return ResponseEntity.ok(reviews);
    }

    @PostMapping()
    public ResponseEntity<?> createReview(Authentication authentication, @RequestBody ReviewDTO reviewDTO) {
        if (reviewDTO.getProductId() == null || reviewDTO.getOptionId() == null) {
            return ResponseEntity.badRequest().body("상품 ID 또는 옵션 ID가 누락되었습니다.");
        }

        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUserName(username);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증된 사용자를 찾을 수 없습니다.");
        }

        Optional<Product> productOpt = productRepository.findById(reviewDTO.getProductId());

        if (productOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("상품을 찾을 수 없습니다.");
        }

        try {
            Review review = reviewService.createReview(userOpt.get(), productOpt.get(), reviewDTO);
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewDTO updatedReview,
            @AuthenticationPrincipal UserDetails userDetails // ✅ 로그인한 사용자 정보 가져오기
    ) {
        Optional<Review> existingReviewOpt = reviewRepository.findById(reviewId);
        if (existingReviewOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("리뷰를 찾을 수 없습니다.");
        }

        Review existingReview = existingReviewOpt.get();

        // ✅ 유저 ID 가져오기
        String username = userDetails.getUsername(); // username (유저의 식별자)

        // ✅ 유저 ID로 DB에서 User 객체 가져오기
        User currentUser = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 본인이 작성한 리뷰인지 확인
        if (!existingReview.getUser().getUserId().equals(currentUser.getUserId())) {
            return ResponseEntity.status(403).body("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        // 리뷰 내용 업데이트
        existingReview.setRating(Rating.fromInt(updatedReview.getRating()));
        existingReview.setContent(updatedReview.getContent());
        if (updatedReview.getImageUrls() != null) {
            existingReview.setImageUrl(String.join(",", updatedReview.getImageUrls()));
        }

        reviewRepository.save(existingReview);
        return ResponseEntity.ok("리뷰가 성공적으로 수정되었습니다.");
    }


    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductReviews(@PathVariable Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("상품을 찾을 수 없습니다.");
        }

        List<ReviewDTO> reviews = reviewService.getReviewsByProduct(productOpt.get());
        return ResponseEntity.ok(reviews);
    }



}