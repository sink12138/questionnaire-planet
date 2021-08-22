package com.buaa.qp.service;

import com.buaa.qp.entity.Answer;

import java.util.ArrayList;

public interface AnswerService {
    ArrayList<Answer> getAnswersByTid(Integer TemplateId);

    void deleteAnswersByTid(Integer templateId);

    void submitAnswer(Answer answer);

    int countAnswers(Integer templateId);
}
