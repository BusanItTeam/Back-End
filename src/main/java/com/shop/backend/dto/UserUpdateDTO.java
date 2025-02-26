package com.shop.backend.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String username;
    private String name;
    private String email;
    private String phoneNumber;
    private AddressDTO address;
}
