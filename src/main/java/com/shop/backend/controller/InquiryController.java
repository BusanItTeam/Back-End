package com.shop.backend.controller;

import com.shop.backend.dto.InquiryDTO; // DTO import 추가
import com.shop.backend.models.Inquiry;
import com.shop.backend.models.User;
import com.shop.backend.services.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;

    @Autowired
    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @PostMapping
    public ResponseEntity<InquiryDTO> createInquiry(@RequestBody InquiryDTO inquiryDTO,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        // InquiryDTO를 Inquiry로 변환
        Inquiry inquiry = new Inquiry();
        inquiry.setType(inquiryDTO.getType());
        inquiry.setTitle(inquiryDTO.getTitle());
        inquiry.setContent(inquiryDTO.getContent());

        Inquiry createdInquiry = inquiryService.createInquiry(inquiry, username); // Inquiry 객체를 사용

        // Inquiry를 InquiryDTO로 변환
        InquiryDTO responseDTO = convertToDTO(createdInquiry);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<InquiryDTO>> getAllInquiries() {
        List<Inquiry> inquiries = inquiryService.getAllInquiries();
        List<InquiryDTO> inquiryDTOs = inquiries.stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(inquiryDTOs);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<InquiryDTO>> getInquiriesByUserId(@PathVariable Long id,
                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        List<Inquiry> inquiries = inquiryService.getInquiriesByUserId(id);
        List<InquiryDTO> inquiryDTOs = inquiries.stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(inquiryDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InquiryDTO> updateInquiry(@PathVariable Long id,
                                                    @RequestBody InquiryDTO inquiryDTO,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        // InquiryDTO를 Inquiry로 변환
        Inquiry inquiryDetails = new Inquiry();
        inquiryDetails.setType(inquiryDTO.getType());
        inquiryDetails.setTitle(inquiryDTO.getTitle());
        inquiryDetails.setContent(inquiryDTO.getContent());

        Inquiry updatedInquiry = inquiryService.updateInquiry(id, inquiryDetails, username); // Inquiry 객체 사용
        InquiryDTO responseDTO = convertToDTO(updatedInquiry);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInquiry(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        inquiryService.deleteInquiry(id, username);
        return ResponseEntity.ok().build();
    }

    private InquiryDTO convertToDTO(Inquiry inquiry) {
        InquiryDTO inquiryDTO = new InquiryDTO();
        inquiryDTO.setInquiryId(inquiry.getInquiryId());
        inquiryDTO.setType(inquiry.getType());
        inquiryDTO.setTitle(inquiry.getTitle());
        inquiryDTO.setContent(inquiry.getContent());
        inquiryDTO.setCreatedAt(inquiry.getCreatedAt());
        inquiryDTO.setAnswer(inquiry.getAnswer());
        inquiryDTO.setAnsweredAt(inquiry.getAnsweredAt());
        inquiryDTO.setUserId(inquiry.getUser().getUserId()); // userId 설정
        inquiryDTO.setName(inquiry.getUser().getName());
        return inquiryDTO;
    }

    @PostMapping("/{id}/answer")
    public ResponseEntity<InquiryDTO> answerInquiry(@PathVariable Long id,
                                                    @RequestBody InquiryDTO inquiryDTO,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        String adminUsername = userDetails.getUsername();
        Inquiry answeredInquiry = inquiryService.answerInquiry(id, inquiryDTO.getAnswer(), adminUsername);
        InquiryDTO responseDTO = convertToDTO(answeredInquiry);
        return ResponseEntity.ok(responseDTO);
    }

}