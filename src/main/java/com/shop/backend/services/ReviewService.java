package com.shop.backend.services;

import com.shop.backend.dto.ReviewDTO;
import com.shop.backend.models.Product;
import com.shop.backend.models.Review;
import com.shop.backend.models.User;

import java.util.List;

public interface ReviewService {
    Review createReview(User user, Product product, ReviewDTO reviewDTO);

    List<ReviewDTO> getUserReviews(User user);

    Review updateReview(User user, Long reviewId, ReviewDTO reviewDTO);


    List<ReviewDTO> getReviewsByProduct(Product product);
}
