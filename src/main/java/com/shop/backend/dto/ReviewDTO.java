package com.shop.backend.dto;

import com.shop.backend.models.ProductOption;
import com.shop.backend.models.Rating;
import com.shop.backend.models.Review;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ReviewDTO {
    private Long reviewId;
    private Long productId;
    private Long optionId;
    private String userName;
    private int rating;
    private String content;
    private List<String> imageUrls;




    public ReviewDTO() {

    }


    public ReviewDTO(Review review) {
        this.reviewId = review.getReviewId();
        this.content = review.getContent();
        this.optionId = review.getProductOption() != null ? review.getProductOption().getOptionId() : null;
        this.productId = review.getProduct() != null ? review.getProduct().getProductId() : null;
        this.rating = review.getRating().getValue();
        this.userName = review.getUser() != null ? review.getUser().getUserName() : null;
        this.imageUrls = Collections.singletonList(review.getImageUrl());
    }
}
