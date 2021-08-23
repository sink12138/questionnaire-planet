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
    @Transactional(rollbackFor = Exception.class)
    public boolean submitAnswer(Answer answer) {
        Integer templateId = answer.getTemplateId();
        Integer quota = templateDao.selectQuotaById(templateId);
        if (quota != null) {
            Integer answerCount = answerDao.selectCountByTid(templateId);
            if (answerCount >= quota)
                return false;
        }
        answerDao.insert(answer);
        return true;
    }

    @Override
    public int countAnswers(Integer templateId) {
        return answerDao.selectCountByTid(templateId);
    }

}
