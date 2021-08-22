package com.buaa.qp.service;

import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;

import java.util.ArrayList;

public interface TemplateService {
    Template getTemplate(Integer templateId);

    ArrayList<Template> getMyTemplates(Integer owner, boolean deleted);

    Integer submitTemplate(Template template, ArrayList<Question> questions);

    void modifyTemplate(Template template, ArrayList<Question> questions);

    void releaseTemplate(Integer templateId, Boolean release);

    void removeTemplate(Integer templateId, Boolean remove);

    ArrayList<Question> getQuestionsByTid(Integer templateId);

    void deleteTemplate(Integer templateId);

    void adjust(Template template);
}
