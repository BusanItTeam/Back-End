package com.shop.backend.dto;

import com.shop.backend.models.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {
    private String postcode;
    private String address;
    private String detailAddress;
    private String extraAddress;
    private String name;
    private String phoneNumber;

    public AddressDTO(Address address) {
        this.postcode = address.getPostcode();
        this.address = address.getAddress();
        this.detailAddress = address.getDetailAddress();
        this.extraAddress = address.getExtraAddress();
        this.name = getName();
        this.phoneNumber = getPhoneNumber();
    }
}