package com.buaa.qp.service;

import com.buaa.qp.entity.Question;
import com.buaa.qp.exception.ParameterFormatException;

import java.util.ArrayList;

public interface QuestionService {
    void updateRemains(Question question);

    Integer shuffleQuestions(ArrayList<Question> questions, Integer accountId, Integer templateId) throws ParameterFormatException;

    void makeShuffledQuestions(ArrayList<Question> questions, Integer shuffleId) throws ParameterFormatException;

    void makeShuffledQuestions(ArrayList<Question> questions, Integer accountId, Integer templateId) throws ParameterFormatException;
}
