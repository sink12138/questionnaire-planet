package com.buaa.qp.service;

import com.buaa.qp.entity.Logic;
import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;

import java.util.ArrayList;
import java.util.TreeSet;

public interface TemplateService {
    Template getTemplate(Integer templateId);

    ArrayList<Template> getMyTemplates(Integer owner);

    Integer submitTemplate(Template template, ArrayList<Question> questions, TreeSet<Logic> logics);

    void modifyTemplate(Template template, ArrayList<Question> questions, TreeSet<Logic> logics);

    void releaseTemplate(Integer templateId, Boolean release);

    void removeTemplate(Integer templateId, Boolean remove);

    ArrayList<Question> getQuestionsByTid(Integer templateId);

    TreeSet<Logic> getLogicsByTid(Integer templateId);

    void deleteTemplate(Integer templateId);

    void adjustTemplate(Template template);

    String updateCode(Integer templateId);

    Template getTemplate(String code);
}
