package com.buaa.qp.service.impl;

import com.buaa.qp.dao.QuestionDao;
import com.buaa.qp.dao.TemplateDao;
import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;
import com.buaa.qp.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TemplateServiceImpl implements TemplateService {
    @Autowired
    private TemplateDao templateDao;

    @Autowired
    private QuestionDao questionDao;

    @Override
    public void submitTemplate(Template template, ArrayList<Question> questions) {
        Integer templateId = templateDao.insert(template);
        for (Question question : questions) {
            question.setTemplateId(templateId);
            questionDao.insert(question);
        }
    }

    @Override
    public void modifyTemplate(Template template, ArrayList<Question> questions) {
        templateDao.update(template);
        Integer templateId = template.getTemplateId();
        questionDao.deleteByTid(templateId);
        for (Question question : questions) {
            question.setTemplateId(templateId);
            questionDao.insert(question);
        }
    }
}
