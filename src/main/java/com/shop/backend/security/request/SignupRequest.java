package com.shop.backend.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Data
public class SignupRequest {

    @NotBlank
    @Size(min = 2, max = 20)
    private String username; //유저네임은 2~20자

    @NotBlank
    @Size(max = 50)
    @Email
    private String email; //이메일 최대 50자

    @Setter
    @Getter
    private Set<String> role; //권한 리스트

    @NotBlank
    @Size(min = 6, max = 40)
    private String password; //비밀번호 6~40자

    @NotBlank
    @Getter
    @Setter
    private String phoneNumber;

    @NotBlank
    @Setter
    @Getter
    private String address;

}