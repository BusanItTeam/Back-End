package com.shop.backend.services;

import com.shop.backend.models.Inquiry;

import java.util.List;

public interface InquiryService {
    Inquiry createInquiry(Inquiry inquiry, String username);
    List<Inquiry> getAllInquiries();
    Inquiry getInquiryById(Long id, String username);
    Inquiry updateInquiry(Long id, Inquiry inquiryDetails, String username);
    void deleteInquiry(Long id, String username);

    List<Inquiry> getInquiriesByUserId(Long id);
}
