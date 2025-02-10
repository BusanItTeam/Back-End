package com.shop.backend.dto;
import com.shop.backend.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;//유저정보를 담기 위한 객체
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String userName;
    private String email;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private LocalDate credentialsExpiryDate;
    private LocalDate accountExpiryDate;
    private String twoFactorSecret;
    private boolean isTwoFactorEnabled;
    private String signUpMethod;
    private Role role;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}