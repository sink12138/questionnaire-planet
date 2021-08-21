package com.buaa.qp.service;

import com.buaa.qp.entity.Question;

import java.util.ArrayList;

public interface QuestionService {
    ArrayList<Question> getQuestions(Integer templateId);
}
