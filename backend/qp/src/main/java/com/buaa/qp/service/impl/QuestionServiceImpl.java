package com.buaa.qp.service.impl;

import com.buaa.qp.dao.QuestionDao;
import com.buaa.qp.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionDao questionDao;

}
