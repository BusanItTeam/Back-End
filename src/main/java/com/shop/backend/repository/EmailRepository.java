package com.shop.backend.repository;


import com.shop.backend.models.EmailToken;
import com.shop.backend.models.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<EmailToken, Long> {





    void deleteByEmail(String email);

    Optional<EmailToken> findByEmailAndCode(String email, String verificationCode);
}
