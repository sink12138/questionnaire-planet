package com.buaa.qp.controller;

import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;
import com.buaa.qp.exception.LoginVerificationException;
import com.buaa.qp.exception.ParameterFormatException;
import com.buaa.qp.service.TemplateService;
import com.buaa.qp.util.ClassParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TemplateController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private TemplateService templateService;

    @PostMapping("/normal/submit")
    public Map<String, Object> normalSubmit(@RequestBody Map<String, Object> requestMap) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Login checks
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (accountId == null)
                throw new LoginVerificationException();

            Integer templateId;
            String title;
            String description;
            String password;
            ArrayList<Map<String, Object>> questionMaps;
            ClassParser parser = new ClassParser();

            // Parameter checks of template
            try {
                templateId = (Integer) requestMap.get("templateId");
                title = (String) requestMap.get("title");
                description = (String) requestMap.get("description");
                if (description.isEmpty()) description = null;
                password = (String) requestMap.get("password");
                if (password.isEmpty()) password = null;
                questionMaps = parser.toMapList(requestMap.get("questions"));
            }
            catch (ClassCastException cce) {
                throw new ParameterFormatException();
            }
            if (templateId == null || templateId < 0)
                throw new ParameterFormatException();
            if (title == null || title.isEmpty())
                throw new ParameterFormatException();
            if (questionMaps.isEmpty())
                throw new ParameterFormatException();
            ArrayList<Question> questions = new ArrayList<>();

            // Parameter checks of questions
            for (Map<String, Object> questionMap : questionMaps) {
                String questionType;
                String questionStem;
                String questionDescription;
                Boolean questionRequired;
                try {
                    questionType = (String) questionMap.get("type");
                    questionStem = (String) questionMap.get("stem");
                    questionDescription = (String) questionMap.get("description");
                    questionRequired = (Boolean) questionMap.get("required");
                }
                catch (ClassCastException cce) {
                    throw new ParameterFormatException();
                }
                Map<String, Object> argsMap = new HashMap<>();
                switch (questionType) {
                    case "choice":
                    case "dropdown": {
                        ArrayList<String> choices;
                        try {
                            choices = parser.toStringList(questionMap.get("choices"));
                        }
                        catch (ClassCastException cce) {
                            throw new ParameterFormatException();
                        }
                        if (choices == null || choices.size() < 2)
                            throw new ParameterFormatException();
                        argsMap.put("choices", choices);
                        break;
                    }
                    case "multi-choice": {
                        ArrayList<String> choices;
                        Integer max;
                        Integer min;
                        try {
                            choices = parser.toStringList(questionMap.get("choices"));
                            max = (Integer) questionMap.get("max");
                            min = (Integer) questionMap.get("min");
                        }
                        catch (ClassCastException cce) {
                            throw new ParameterFormatException();
                        }
                        if (choices == null || choices.size() < 2)
                            throw new ParameterFormatException();
                        if (max == null || max > choices.size()) max = choices.size();
                        if (min == null || min < 1) min = 1;
                        if (max < 2 || max < min || max.equals(min) && min == choices.size())
                            throw new ParameterFormatException();
                        argsMap.put("choices", choices);
                        argsMap.put("max", max);
                        argsMap.put("min", min);
                        break;
                    }
                    case "filling": {
                        Integer height;
                        Integer width;
                        try {
                            height = (Integer) questionMap.get("height");
                            width = (Integer) questionMap.get("width");
                        }
                        catch (ClassCastException cce) {
                            throw new ParameterFormatException();
                        }
                        if (height == null || height <= 0)
                            throw new ParameterFormatException();
                        if (width == null || width <= 0)
                            throw new ParameterFormatException();
                        if (height > 10) height = 10;
                        if (width < 30) width = 30;
                        else if (width > 500) width = 500;
                        argsMap.put("height", height);
                        argsMap.put("width", width);
                        break;
                    }
                    case "grade": {
                        ArrayList<String> choices;
                        ArrayList<Integer> scores;
                        try {
                            choices = parser.toStringList(questionMap.get("choices"));
                            scores = parser.toIntegerList(questionMap.get("scores"));
                        }
                        catch (ClassCastException | NumberFormatException e) {
                            throw new ParameterFormatException();
                        }
                        if (choices == null || scores == null)
                            throw new ParameterFormatException();
                        if (choices.size() < 2 || choices.size() != scores.size())
                            throw new ParameterFormatException();
                        argsMap.put("choices", choices);
                        argsMap.put("scores", scores);
                        break;
                    }
                    default:
                        throw new ParameterFormatException();
                }
                String args = new JSONObject(argsMap).toString();
                questions.add(new Question(questionType, questionStem, questionDescription, questionRequired, args));
            }

            Template template = new Template(title, description, password, accountId);
            if (templateId == 0) {
                templateService.submitTemplate(template, questions);
            }
            else {
                template.setTemplateId(templateId);
                templateService.modifyTemplate(template, questions);
            }
            map.put("success", true);
        }
        catch (LoginVerificationException | ParameterFormatException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        }
        catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", exception.toString());
        }
        return map;
    }


}
