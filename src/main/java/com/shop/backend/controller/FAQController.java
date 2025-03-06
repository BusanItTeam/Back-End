package com.shop.backend.controller;

import com.shop.backend.models.Category;
import com.shop.backend.models.FAQ;
import com.shop.backend.services.FaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class FAQController {

    @Autowired
    private FaqService faqService;

    @GetMapping("/FAQ")
    public ResponseEntity<List<FAQ>> findAllFAQ(){
        return new ResponseEntity<>(faqService.getAllFaqs(), HttpStatus.OK);
    }

    @PostMapping("/FAQ")
    public ResponseEntity<FAQ> createFaq(@RequestBody FAQ faq) {
        FAQ savedFaq = faqService.createFaq(faq);
        return ResponseEntity.ok(savedFaq);
    }

    @DeleteMapping("/FAQ/{id}")
    public ResponseEntity<String> deleteFAQ(@PathVariable Long id) {
        faqService.deleteFaq(id);
        return ResponseEntity.ok("FAQ deleted successfully");
    }

    @PutMapping("/FAQ/{id}")
    public ResponseEntity<FAQ> updateFAQ(@PathVariable Long id, @RequestBody FAQ faq) {
    FAQ updateFaq = faqService.updateFaq(id, faq);
    return ResponseEntity.ok(updateFaq);
    }


}
