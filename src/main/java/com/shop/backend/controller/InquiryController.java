package com.shop.backend.controller;

import com.shop.backend.models.Inquiry;
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
    public ResponseEntity<Inquiry> createInquiry(@RequestBody Inquiry inquiry,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Inquiry createdInquiry = inquiryService.createInquiry(inquiry, username);
        return ResponseEntity.ok(createdInquiry);
    }

    @GetMapping
    public ResponseEntity<List<Inquiry>> getAllInquiries() {
        List<Inquiry> inquiries = inquiryService.getAllInquiries();
        return ResponseEntity.ok(inquiries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inquiry> getInquiryById(@PathVariable Long id) {
        Inquiry inquiry = inquiryService.getInquiryById(id);
        return ResponseEntity.ok(inquiry);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inquiry> updateInquiry(@PathVariable Long id,
                                                 @RequestBody Inquiry inquiryDetails,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Inquiry updatedInquiry = inquiryService.updateInquiry(id, inquiryDetails, username);
        return ResponseEntity.ok(updatedInquiry);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInquiry(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        inquiryService.deleteInquiry(id, username);
        return ResponseEntity.ok().build();
    }
}