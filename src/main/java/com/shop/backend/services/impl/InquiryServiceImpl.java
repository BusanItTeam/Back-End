package com.shop.backend.services.impl;
import com.shop.backend.dto.InquiryDTO; // DTO import 추가
import com.shop.backend.models.Inquiry;
import com.shop.backend.models.User;
import com.shop.backend.repository.InquiryRepository;
import com.shop.backend.repository.UserRepository;
import com.shop.backend.services.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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
        return inquiryRepository.findAllWithUser(); // 이 메서드는 사용자 정보도 가져와야 함
    }

    @Override
    public Inquiry getInquiryById(Long id, String username) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!inquiry.getUser().equals(user)) {
            throw new AccessDeniedException("You are not authorized to view this inquiry");
        }

        return inquiry;
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

    @Override
    public List<Inquiry> getInquiriesByUserId(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return inquiryRepository.findByUser(user);
    }

    @Override
    public Inquiry answerInquiry(Long id, String answer, String adminUsername) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        // 관리자 권한 확인 (실제 구현은 프로젝트의 인증/인가 방식에 따라 다를 수 있습니다)
        if (!isAdmin(adminUsername)) {
            throw new AccessDeniedException("Only admins can answer inquiries");
        }

        inquiry.setAnswer(answer);
        return inquiryRepository.save(inquiry);
    }

    private boolean isAdmin(String username) {
        // 관리자 확인 로직 구현
        // 예: 사용자의 역할을 확인하거나, 관리자 목록을 확인하는 등의 로직
        return true; // 임시로 모든 사용자를 관리자로 취급
    }

}