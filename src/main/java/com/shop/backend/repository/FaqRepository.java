package com.shop.backend.repository;

import com.shop.backend.models.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqRepository extends JpaRepository<FAQ, Long> {
}
