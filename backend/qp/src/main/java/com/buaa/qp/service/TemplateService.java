package com.buaa.qp.service;

import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;

import java.util.ArrayList;

public interface TemplateService {
    void submitTemplate(Template template, ArrayList<Question> questions);

    void modifyTemplate(Template template, ArrayList<Question> questions);
}
