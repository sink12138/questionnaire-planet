package com.buaa.qp.service.impl;

import com.buaa.qp.dao.AnswerDao;
import com.buaa.qp.dao.QuestionDao;
import com.buaa.qp.dao.ShuffleDao;
import com.buaa.qp.dao.TemplateDao;
import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;
import com.buaa.qp.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Random;

@Service
public class TemplateServiceImpl implements TemplateService {
    @Autowired
    private TemplateDao templateDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private ShuffleDao shuffleDao;

    @Override
    public Template getTemplate(Integer templateId) {
        Template template = templateDao.selectById(templateId);
        if (template == null)
            return null;
        template.setDuration(templateDao.selectDuration(templateId));
        return template;
    }

    @Override
    public Template getTemplate(String code) {
        return templateDao.selectByCode(code);
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
        if (template.getStartTime() != null || template.getEndTime() != null)
            templateDao.createEvents(template);
        Integer templateId = template.getTemplateId();
        String code = generateCode();
        while (templateDao.selectByCode(code) != null) {
            code = generateCode();
        }
        template.setCode(code);
        templateDao.updateCode(template);
        for (Question question : questions) {
            question.setTemplateId(templateId);
            questionDao.insert(question);
        }
        return templateId;
    }

    @Override
    public void modifyTemplate(Template template, ArrayList<Question> questions) {
        template.setDuration("00:00:00");
        templateDao.update(template);
        Integer templateId = template.getTemplateId();
        answerDao.deleteByTid(templateId);
        questionDao.deleteByTid(templateId);
        shuffleDao.deleteByTid(templateId);
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
        shuffleDao.deleteByTid(templateId);
        answerDao.deleteByTid(templateId);
        questionDao.deleteByTid(templateId);
        templateDao.deleteByTid(templateId);
    }

    @Override
    public void adjustTemplate(Template template) {
        templateDao.update(template);
    }

    @Override
    public String updateCode(Integer templateId) {
        Template template = templateDao.selectById(templateId);
        if (template == null)
            return null;
        String code = generateCode();
        while (templateDao.selectByCode(code) != null) {
            code = generateCode();
        }
        template.setCode(code);
        templateDao.updateCode(template);
        return code;
    }

    private String generateCode() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int number = random.nextInt(62);
            stringBuilder.append(str.charAt(number));
        }
        return stringBuilder.toString();
    }
}
