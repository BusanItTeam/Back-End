package com.shop.backend.repository;

import com.shop.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //유저 네임으로 찾기
    Optional<User> findByUserName(String username);
    
    //유저네임 여부 확인
    Boolean existsByUserName(String username);

    Boolean existsByEmail(String email);

}
