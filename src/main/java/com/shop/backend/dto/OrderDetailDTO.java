package com.shop.backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class OrderDetailDTO {
    private Long productId;
    private int quantity;
    private BigDecimal price;
    private Long OptionId;
    private BigDecimal discountRate;
    public String ProductName;

    public String OptionSize;
    public String OptionColor;

    public String Image;
    private int stock;
    @Getter
    @Setter
    private boolean reviewExists;

}
