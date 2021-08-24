package com.buaa.qp.controller;

import com.alibaba.fastjson.JSON;
import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;
import com.buaa.qp.exception.ExtraMessageException;
import com.buaa.qp.exception.LoginVerificationException;
import com.buaa.qp.exception.ObjectNotFoundException;
import com.buaa.qp.exception.ParameterFormatException;
import com.buaa.qp.service.TemplateService;
import com.buaa.qp.util.ClassParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TemplateController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TemplateService templateService;

    @PostMapping("/submit")
    public Map<String, Object> submit(@RequestBody Map<String, Object> requestMap) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Login checks
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (accountId == null)
                throw new LoginVerificationException();

            Integer templateId;
            String title;
            String type;
            String description;
            String password;
            String conclusion;
            Integer quota;
            ArrayList<Map<String, Object>> questionMaps;
            ClassParser parser = new ClassParser();

            // Parameter checks of template
            try {
                templateId = (Integer) requestMap.get("templateId");
                title = (String) requestMap.get("title");
                type = (String) requestMap.get("type");
                description = (String) requestMap.get("description");
                if (description != null && description.isEmpty()) description = null;
                password = (String) requestMap.get("password");
                if (password != null && password.isEmpty()) password = null;
                conclusion = (String) requestMap.get("conclusion");
                if (conclusion != null && conclusion.isEmpty()) conclusion = null;
                quota = (Integer) requestMap.get("quota");
                if (quota != null && quota <= 0) quota = null;
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
            if (!type.equals("normal") && !type.equals("vote") && !type.equals("sign-up") && !type.equals("exam"))
                throw new ParameterFormatException();
            ArrayList<Question> questions = new ArrayList<>();

            if (templateId > 0) {
                Template template = templateService.getTemplate(templateId);
                if (template == null)
                    throw new ObjectNotFoundException();
                if (!template.getOwner().equals(accountId))
                    throw new LoginVerificationException();
                if (template.getReleased())
                    throw new ExtraMessageException("已发布的问卷不能编辑");
                if (template.getDeleted())
                    throw new ExtraMessageException("已删除的问卷不能编辑");
            }
            boolean hasSpecialQuestion = !type.equals("vote") && !(type.equals("sign-up") && quota == null);
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
                    if (questionDescription != null && questionDescription.isEmpty()) questionDescription = null;
                    questionRequired = (Boolean) questionMap.get("required");
                }
                catch (ClassCastException cce) {
                    throw new ParameterFormatException();
                }
                if (questionType == null || questionStem == null || questionRequired == null)
                    throw new ParameterFormatException();
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
                        argsMap.put("width", width + "px");
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
                        // Sort the two lists by scores
                        for (int i = 0; i < scores.size(); ++i) {
                            int min = i;
                            for (int j = i + 1; j < scores.size(); ++j) {
                                if (scores.get(j) < scores.get(min))
                                    min = j;
                            }
                            if (min != i) {
                                int tmpInt = scores.get(i);
                                scores.set(i, scores.get(min));
                                scores.set(min, tmpInt);
                                String tmpStr = choices.get(i);
                                choices.set(i, choices.get(min));
                                choices.set(min, tmpStr);
                            }
                        }
                        argsMap.put("choices", choices);
                        argsMap.put("scores", scores);
                        break;
                    }
                    case "vote": {
                        if (!type.equals("vote"))
                            throw new ParameterFormatException();
                        hasSpecialQuestion = true;
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
                        if (min.equals(max) && min == choices.size()) {
                            throw new ParameterFormatException();
                        }
                        else if (min > max) {
                            throw new ParameterFormatException();
                        }
                        argsMap.put("choices", choices);
                        argsMap.put("max", max);
                        argsMap.put("min", min);
                        break;
                    }
                    case "sign-up": {
                        if (!type.equals("sign-up"))
                            throw new ParameterFormatException();
                        hasSpecialQuestion = true;
                        ArrayList<String> choices;
                        ArrayList<Integer> quotas;
                        Integer max;
                        Integer min;
                        try {
                            choices = parser.toStringList(questionMap.get("choices"));
                            quotas = parser.toIntegerList(questionMap.get("quotas"));
                            max = (Integer) questionMap.get("max");
                            min = (Integer) questionMap.get("min");
                        }
                        catch (ClassCastException cce) {
                            throw new ParameterFormatException();
                        }
                        if (choices == null || choices.size() < 2)
                            throw new ParameterFormatException();
                        if (quotas == null || quotas.size() != choices.size()) {
                            throw new ParameterFormatException();
                        }
                        for (Integer q : quotas) {
                            if (q < 1) {
                                throw new ParameterFormatException();
                            }
                        }
                        if (max == null || max > choices.size()) max = choices.size();
                        if (min == null || min < 1) min = 1;
                        if (min.equals(max) && min == choices.size()) {
                            throw new ParameterFormatException();
                        }
                        else if (min > max) {
                            throw new ParameterFormatException();
                        }
                        argsMap.put("choices", choices);
                        argsMap.put("quotas", quotas);
                        ArrayList<Integer> remains = new ArrayList<>(quotas);
                        argsMap.put("remains", remains);
                        argsMap.put("max", max);
                        argsMap.put("min", min);
                        break;
                    }
                    default:
                        throw new ParameterFormatException();
                }
                String args = JSON.toJSONString(argsMap);
                questions.add(new Question(questionType, questionStem, questionDescription, questionRequired, args));
            }
            if (!hasSpecialQuestion) {
                throw new ExtraMessageException("特殊问卷未设置特殊题目");
            }
            Template newTemplate = new Template(type, accountId, title, description, password, conclusion, quota);
            if (templateId == 0) {
                templateId = templateService.submitTemplate(newTemplate, questions);
            }
            else {
                newTemplate.setTemplateId(templateId);
                templateService.modifyTemplate(newTemplate, questions);
            }
            map.put("success", true);
            map.put("templateId", templateId);
        }
        catch (LoginVerificationException | ParameterFormatException | ObjectNotFoundException | ExtraMessageException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        }
        catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }

    @PostMapping("/adjust")
    public Map<String, Object> adjust(@RequestBody Map<String, Object> requestMap) {
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
            String conclusion;
            Integer quota;

            // Parameter checks of template
            try {
                templateId = (Integer) requestMap.get("templateId");
                title = (String) requestMap.get("title");
                description = (String) requestMap.get("description");
                password = (String) requestMap.get("password");
                conclusion = (String) requestMap.get("conclusion");
                quota = (Integer) requestMap.get("quota");
            }
            catch (ClassCastException cce) {
                throw new ParameterFormatException();
            }
            if (description != null && description.isEmpty()) description = null;
            if (password != null && password.isEmpty()) password = null;
            if (conclusion != null && conclusion.isEmpty()) conclusion = null;
            if (quota != null && quota <= 0) quota = null;
            if (templateId == null || templateId < 0)
                throw new ParameterFormatException();
            if (title == null || title.isEmpty())
                throw new ParameterFormatException();
            Template template = templateService.getTemplate(templateId);
            if (template == null)
                throw new ObjectNotFoundException();
            if (!template.getOwner().equals(accountId))
                throw new LoginVerificationException();
            if (template.getDeleted())
                throw new ExtraMessageException("已删除的问卷不能编辑");
            if (template.getReleased())
                throw new ExtraMessageException("已发布的问卷不能编辑");
            template.setTitle(title);
            template.setDescription(description);
            template.setPassword(password);
            template.setConclusion(conclusion);
            template.setQuota(quota);
            templateService.adjustTemplate(template);
            map.put("success", true);

        } catch (LoginVerificationException | ExtraMessageException | ObjectNotFoundException
                | ParameterFormatException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        }
        catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }
}
