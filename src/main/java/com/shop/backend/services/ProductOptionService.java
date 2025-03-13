package com.shop.backend.services;

import com.shop.backend.dto.ProductOptionDTO;
import com.shop.backend.models.ProductOption;
import com.shop.backend.repository.ProductOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductOptionService {

    @Autowired
    private ProductOptionRepository productOptionRepository;

    // 모든 옵션을 조회하고, ProductOptionDTO로 변환하여 반환
    public List<ProductOptionDTO> getAllOptions() {
        List<ProductOption> options = productOptionRepository.findAll();
        return options.stream()
                .map(option -> {
                    ProductOptionDTO dto = new ProductOptionDTO();
                    dto.setOptionId(option.getOptionId());
                    dto.setColor(option.getColor());
                    dto.setSize(option.getSize());
                    return dto;
                })
                .collect(Collectors.toList());
    }





}