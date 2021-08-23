package com.buaa.qp.service.impl;

import com.buaa.qp.dao.AnswerDao;
import com.buaa.qp.dao.TemplateDao;
import com.buaa.qp.entity.Answer;
import com.buaa.qp.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class AnswerServiceImpl implements AnswerService {

    private static final Object quotaLock = new Object();

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private TemplateDao templateDao;

    @Override
    public ArrayList<Answer> getAnswersByTid(Integer templateId) {
        return answerDao.selectByTid(templateId);
    }

    @Override
    public void deleteAnswersByTid(Integer templateId) {
        answerDao.deleteByTid(templateId);
    }

    @Override
    public boolean submitAnswer(Answer answer) {
        Integer templateId = answer.getTemplateId();
        Integer quota = templateDao.selectQuotaById(templateId);
        if (quota != null) {
            synchronized (quotaLock) {
                Integer answerCount = answerDao.selectCountByTid(templateId);
                if (answerCount >= quota)
                    return false;
                answerDao.insert(answer);
            }
        }
        else
            answerDao.insert(answer);
        return true;
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
}
