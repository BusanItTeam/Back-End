package com.shop.backend.repository;

import com.shop.backend.models.Inquiry;
import com.shop.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findByUser(User user);

    @Query("SELECT i FROM Inquiry i JOIN FETCH i.user")
    List<Inquiry> findAllWithUser();
}
