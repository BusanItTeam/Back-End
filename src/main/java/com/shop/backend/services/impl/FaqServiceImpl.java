package com.shop.backend.services.impl;

import com.shop.backend.models.FAQ;
import com.shop.backend.repository.FaqRepository;
import com.shop.backend.services.FaqService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FaqServiceImpl implements FaqService {

    @Autowired
    FaqRepository faqRepository;




    @Override
    public List<FAQ> getAllFaqs() {
        return faqRepository.findAll();
    }

    @Override
    public FAQ createFaq(FAQ faq) {
        return faqRepository.save(faq);
    }

    @Override
    public void deleteFaq(Long id){
       faqRepository.deleteById(id);
    }

    @Override
    public FAQ updateFaq(Long id, FAQ faq) {
        FAQ faq1 = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ 를 찾지못했습니다"));
        faq1.setQuestion(faq.getQuestion());
        faq1.setAnswer(faq.getAnswer());
        return faqRepository.save(faq1);
    }



}
