package com.buaa.qp.service;

import com.buaa.qp.entity.Answer;
import com.buaa.qp.exception.ExtraMessageException;
import com.buaa.qp.exception.ParameterFormatException;

import java.util.ArrayList;

public interface AnswerService {
    ArrayList<Answer> getAnswersByTid(Integer TemplateId);

    Answer getAnswerById(Integer answerId);

    void clearAllAnswers(Integer templateId);

    void deleteById(Integer answerId);

    Integer submitAnswer(Answer answer);

    int countAnswers(Integer templateId);

    Answer getOldAnswer(Integer templateId, Integer submitter);

    Answer getOldAnswer(Integer answerId);

    ArrayList<Object> reorderAnswer(ArrayList<Object> answers, Integer shuffleId);

    ArrayList<Object> reorderAnswer(ArrayList<Object> answers, Integer accountId, Integer templateId);

    void shuffleAnswer(ArrayList<Object> answers, Integer shuffleId) throws ParameterFormatException;

    void shuffleAnswer(ArrayList<Object> answers, Integer accountId, Integer templateId) throws ParameterFormatException;
}
