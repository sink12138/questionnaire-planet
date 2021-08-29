package com.buaa.qp.service.impl;

import com.buaa.qp.dao.*;
import com.buaa.qp.entity.Logic;
import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;
import com.buaa.qp.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

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

    @Autowired
    private LogicDao logicDao;

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
    public Integer submitTemplate(Template template, ArrayList<Question> questions, TreeSet<Logic> logics) {
        String code = generateCode();
        while (templateDao.selectByCode(code) != null) {
            code = generateCode();
        }
        template.setCode(code);
        templateDao.insert(template);
        if (template.getStartTime() != null || template.getEndTime() != null)
            templateDao.createEvents(template);
        Integer templateId = template.getTemplateId();
        for (Question question : questions) {
            question.setTemplateId(templateId);
            questionDao.insert(question);
        }
        for (Logic logic : logics) {
            logic.setTemplateId(templateId);
            logicDao.insert(logic);
        }
        return templateId;
    }

    @Override
    public void modifyTemplate(Template template, ArrayList<Question> questions, TreeSet<Logic> logics) {
        template.setDuration("00:00:00");
        templateDao.update(template);
        Integer templateId = template.getTemplateId();
        answerDao.deleteByTid(templateId);
        questionDao.deleteByTid(templateId);
        logicDao.deleteByTid(templateId);
        shuffleDao.deleteByTid(templateId);
        for (Question question : questions) {
            question.setTemplateId(templateId);
            questionDao.insert(question);
        }
        for (Logic logic : logics) {
            logic.setTemplateId(templateId);
            logicDao.insert(logic);
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
    public TreeSet<Logic> getLogicsByTid(Integer templateId) {
        return new TreeSet<>(logicDao.selectByTid(templateId));
    }

    @Override
    public void deleteTemplate(Integer templateId) {
        shuffleDao.deleteByTid(templateId);
        answerDao.deleteByTid(templateId);
        logicDao.deleteByTid(templateId);
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
