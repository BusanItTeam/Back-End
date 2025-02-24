package com.shop.backend.dto;

import com.shop.backend.models.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressUpdateRequestDTO {
    private Long userId;
    private Address address;
}