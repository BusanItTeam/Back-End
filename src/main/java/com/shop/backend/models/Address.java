package com.shop.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    @Getter
    @Setter
    private String postcode;

    @Column(length = 255)
    @Getter
    @Setter
    private String address;

    @Column(length = 255)
    @Getter
    @Setter
    private String detailAddress;

    @Column(length = 255)
    @Getter
    @Setter
    private String extraAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  nullable = false)
    @JsonBackReference
    private User user;


    public Address(String postcode, String address, String detailAddress, String extraAddress, User user) {
        this.postcode = postcode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.extraAddress = extraAddress;
        this.user = user;

    }



}
