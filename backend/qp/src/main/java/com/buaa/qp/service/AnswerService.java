package com.buaa.qp.service;

import com.buaa.qp.entity.Answer;

import java.util.ArrayList;

public interface AnswerService {
    ArrayList<Answer> getAnswersByTid(Integer TemplateId);

    Answer getAnswerById(Integer answerId);

    void clearAllAnswers(Integer templateId);

    void deleteById(Integer answerId);

    void submitAnswer(Answer answer);

    int countAnswers(Integer templateId);

    Answer getOldAnswer(Integer templateId, Integer submitter);

    ArrayList<Object> reorderAnswer(ArrayList<Object> answers, Integer shuffleId);

    ArrayList<Object> reorderAnswer(ArrayList<Object> answers, Integer accountId, Integer templateId);
}
