package com.buaa.qp.service.impl;

import com.buaa.qp.dao.AnswerDao;
import com.buaa.qp.dao.QuestionDao;
import com.buaa.qp.dao.TemplateDao;
import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;
import com.buaa.qp.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class TemplateServiceImpl implements TemplateService {
    @Autowired
    private TemplateDao templateDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AnswerDao answerDao;

    @Override
    public Template getTemplate(Integer templateId) {
        Template template = templateDao.selectById(templateId);
        template.setDuration(templateDao.selectDuration(templateId));
        return template;
    }

    @Override
    public ArrayList<Template> getMyTemplates(Integer owner) {
        ArrayList<Template> templates = templateDao.selectByOwner(owner);
        for (Template template : templates) {
            template.setDuration(templateDao.selectDuration(template.getTemplateId()));
        }
        return templates;
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
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        template.setDuration(sdf.format(new Date(0)));
        templateDao.update(template);
        Integer templateId = template.getTemplateId();
        answerDao.deleteByTid(templateId);
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

    @Override
    public void removeTemplate(Integer templateId, Boolean remove) {
        Template template = new Template();
        template.setTemplateId(templateId);
        template.setDeleted(remove);
        templateDao.updateDeleted(template);
    }

    @Override
    public ArrayList<Question> getQuestionsByTid(Integer templateId) {
        return questionDao.selectByTid(templateId);
    }

    @Override
    public void deleteTemplate(Integer templateId) {
        answerDao.deleteByTid(templateId);
        questionDao.deleteByTid(templateId);
        templateDao.deleteByTid(templateId);
    }

    @Override
    public void adjustTemplate(Template template) {
        templateDao.update(template);
    }
}
