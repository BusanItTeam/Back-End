package com.shop.backend.dto;

import com.shop.backend.models.Address;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AddressUpdateRequestDTO {
    private Long userId;
    private Address address;
}