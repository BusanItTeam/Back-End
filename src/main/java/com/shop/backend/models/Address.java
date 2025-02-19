package com.shop.backend.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String postcode;

    @Column(length = 255, nullable = false)
    private String address;

    @Column(length = 255)
    private String detailAddress;

    @Column(length = 255)
    private String extraAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    public Address(String postcode, String address, String detailAddress, String extraAddress, User user) {
        this.postcode = postcode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.extraAddress = extraAddress;
        this.user = user;

    }



}
