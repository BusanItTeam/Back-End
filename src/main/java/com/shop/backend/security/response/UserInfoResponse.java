
package com.shop.backend.security.response;

import com.shop.backend.models.Address;
import com.shop.backend.models.PointHistory;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private List<AddressResponse> addresses;
    private List<PointHistory> pointHistories;
    private int points;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private LocalDate credentialsExpiryDate;
    private LocalDate accountExpiryDate;
    private boolean isTwoFactorEnabled;
    private List<String> roles;
    private String name;
    private final LocalDateTime createdDate;


    public UserInfoResponse(Long id, String username, String email, List<Address> addresses, String phoneNumber,
                            int points, List<PointHistory> pointHistories, boolean accountNonLocked, boolean accountNonExpired, boolean credentialsNonExpired,
                            boolean enabled, LocalDate credentialsExpiryDate, LocalDate accountExpiryDate,
                            boolean isTwoFactorEnabled, List<String> roles, String name, LocalDateTime createdDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.pointHistories = pointHistories;
        this.points = points;
        this.accountNonLocked = accountNonLocked;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
        this.credentialsExpiryDate = credentialsExpiryDate;
        this.accountExpiryDate = accountExpiryDate;
        this.isTwoFactorEnabled = isTwoFactorEnabled;
        this.roles = roles;
        this.addresses = addresses.stream().map(AddressResponse::new).collect(Collectors.toList());
        this.name = name;
        this.createdDate = createdDate;
    }

    @Getter
    @Setter
    public static class AddressResponse {
        private Long id;
        private String postcode;
        private String address;
        private String detailAddress;
        private String extraAddress;

        public AddressResponse(Address address) {
            this.id = address.getId();
            this.postcode = address.getPostcode();
            this.address = address.getAddress();
            this.detailAddress = address.getDetailAddress();
            this.extraAddress = address.getExtraAddress();
        }
    }


}