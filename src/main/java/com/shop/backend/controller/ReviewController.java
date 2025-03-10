package com.shop.backend.controller;

import com.shop.backend.dto.ReviewDTO;
import com.shop.backend.models.Review;
import com.shop.backend.models.User;
import com.shop.backend.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody ReviewDTO reviewDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Review review = reviewService.addReview(user.getUserId(), reviewDTO);
        return ResponseEntity.ok(review);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getReviewsByProduct(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProduct(productId);
        return ResponseEntity.ok(reviews);
    }
}
