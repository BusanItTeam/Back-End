package com.shop.backend.services;

import com.shop.backend.models.FAQ;
import com.shop.backend.models.Inquiry;

import java.util.List;
import java.util.Optional;

public interface FaqService {


   List<FAQ> getAllFaqs();
   FAQ createFaq(FAQ faq);
   void deleteFaq(Long id);
   FAQ updateFaq(Long id, FAQ faq);



}
