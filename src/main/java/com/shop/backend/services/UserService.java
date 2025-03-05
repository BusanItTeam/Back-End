package com.shop.backend.services;

import com.shop.backend.dto.UserDTO;
import com.shop.backend.models.Address;
import com.shop.backend.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    //유저 권한 업데이트
    void updateUserRole(Long userId, String roleName);

    //모든 유저 가져옴
    List<User> getAllUsers();

    void deleteUser(Long id);

    //한명의 유저를 가져옴
    UserDTO getUserById(Long id);

    //유저 정보를 가져옴(유저네임)
    User findByUsername(String username);


    Optional<User> findByEmail(String email);

    User registerUser(User newUser);


    void generatePasswordResetToken(String email);

    void resetPassword(String token, String newPassword);

    void generateEmailResetToken(String email);

    //이메일 검증
    boolean verifyEmailCode(String email, String code);



}


