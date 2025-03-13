package com.shop.backend.dto;

import com.shop.backend.models.ProductOption;
import lombok.Data;

@Data
public class ProductOptionDTO {
    private Long optionId;
    private String color;
    private String size;
    private int stock;

    private ProductOptionDTO convertToDTO(ProductOption productOption) {
        ProductOptionDTO dto = new ProductOptionDTO();
        dto.setOptionId(productOption.getOptionId());
        dto.setColor(productOption.getColor());
        dto.setSize(productOption.getSize());
        return dto;
    }

}
