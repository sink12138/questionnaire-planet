package com.buaa.qp.service.impl;

import com.buaa.qp.dao.AnswerDao;
import com.buaa.qp.entity.Answer;
import com.buaa.qp.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerDao answerDao;

    @Override
    public ArrayList<Answer> getAnswersByTid(Integer templateId) {
        return answerDao.selectByTid(templateId);
    }

    @Override
    public Answer getAnswerById(Integer answerId) {
        return answerDao.selectById(answerId);
    }

    @Override
    public void clearAllAnswers(Integer templateId) {
        answerDao.deleteByTid(templateId);
    }

    @Override
    public void deleteById(Integer answerId) {
        answerDao.deleteById(answerId);
    }

    @Override
    public void submitAnswer(Answer answer) {
        answerDao.insert(answer);
    }

    @Override
    public int countAnswers(Integer templateId) {
        return answerDao.selectCountByTid(templateId);
    }

    @Override
    public Answer getOldAnswer(Integer templateId, Integer submitter) {
        Answer answer = new Answer();
        answer.setTemplateId(templateId);
        answer.setSubmitter(submitter);
        return answerDao.selectByTidAndSubmitter(answer);
    }

    @Override
    public ArrayList<Object> shuffleAnswer(ArrayList<Object> answers, Integer templateId, Integer accountId) {
        return null;
    }

    @Override
    public ArrayList<Object> reorderAnswer(ArrayList<Object> answers, Integer templateId, Integer accountId) {
        return null;
    }

}
