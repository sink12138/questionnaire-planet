package com.buaa.qp.service.impl;

import com.buaa.qp.dao.QuestionDao;
import com.buaa.qp.dao.TemplateDao;
import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;
import com.buaa.qp.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;

@Service
public class TemplateServiceImpl implements TemplateService {
    @Autowired
    private TemplateDao templateDao;

    @Autowired
    private QuestionDao questionDao;

    @Override
    public Template getTemplate(Integer templateId) {
        return templateDao.selectById(templateId);
    }

    @Override
    public ArrayList<Template> getMyTemplates(Integer owner) {
        return templateDao.selectByOwner(owner);
    }

    @Override
    public Integer submitTemplate(Template template, ArrayList<Question> questions) {
        templateDao.insert(template);
        Integer templateId = template.getTemplateId();
        for (Question question : questions) {
            question.setTemplateId(templateId);
            questionDao.insert(question);
        }
        return templateId;
    }

    @Override
    public void modifyTemplate(Template template, ArrayList<Question> questions) {
        template.setDuration(new Time(-28800000));
        templateDao.update(template);
        Integer templateId = template.getTemplateId();
        questionDao.deleteByTid(templateId);
        for (Question question : questions) {
            question.setTemplateId(templateId);
            questionDao.insert(question);
        }
    }

    @Override
    public void releaseTemplate(Integer templateId, Boolean release) {
        Template template = new Template();
        template.setTemplateId(templateId);
        template.setReleased(release);
        templateDao.updateReleased(template);
    }
}