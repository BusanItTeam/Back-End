package com.shop.backend.services.impl;
import com.shop.backend.models.Inquiry;
import com.shop.backend.models.User;
import com.shop.backend.repository.InquiryRepository;
import com.shop.backend.repository.UserRepository;
import com.shop.backend.services.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InquiryServiceImpl implements InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Inquiry createInquiry(Inquiry inquiry, String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        inquiry.setUser(user);
        return inquiryRepository.save(inquiry);
    }

    @Override
    public List<Inquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    @Override
    public Inquiry getInquiryById(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));
    }

    @Override
    public Inquiry updateInquiry(Long id, Inquiry inquiryDetails, String username) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!inquiry.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to update this inquiry");
        }
        inquiry.setType(inquiryDetails.getType());
        inquiry.setTitle(inquiryDetails.getTitle());
        inquiry.setContent(inquiryDetails.getContent());

        return inquiryRepository.save(inquiry);
    }

    @Override
    public void deleteInquiry(Long id, String username) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!inquiry.getUser().equals(user)) {
            throw new RuntimeException("You are not authorized to delete this inquiry");
        }

        inquiryRepository.delete(inquiry);
    }
}