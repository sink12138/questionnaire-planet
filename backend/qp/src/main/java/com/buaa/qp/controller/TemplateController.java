package com.buaa.qp.controller;

import com.alibaba.fastjson.JSON;
import com.buaa.qp.dao.AnswerDao;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class TemplateController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TemplateService templateService;

    @Autowired
    AnswerDao answerDao;

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
            Boolean showIndex;
            Date startTime = null;
            Date endTime = null;
            ArrayList<Map<String, Object>> questionMaps;
            ClassParser parser = new ClassParser();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            // Parameter checks of template
            try {
                templateId = (Integer) requestMap.get("templateId");
                title = (String) requestMap.get("title");
                type = (String) requestMap.get("type");
                showIndex = (Boolean) requestMap.get("showIndex");
                if (showIndex == null) showIndex = true;
                description = (String) requestMap.get("description");
                if (description != null && description.isEmpty()) description = null;
                password = (String) requestMap.get("password");
                if (password != null && password.isEmpty()) password = null;
                conclusion = (String) requestMap.get("conclusion");
                if (conclusion != null && conclusion.isEmpty()) conclusion = null;
                quota = (Integer) requestMap.get("quota");
                if (quota != null && quota <= 0) quota = null;
                String startStr = (String) requestMap.get("startTime");
                String endStr = (String) requestMap.get("endTime");
                if (startStr != null && !startStr.isEmpty()) startTime = sdf.parse(startStr);
                if (endStr != null && !endStr.isEmpty()) endTime = sdf.parse(endStr);
                questionMaps = parser.toMapList(requestMap.get("questions"));
            }
            catch (ClassCastException | ParseException cce) {
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
            Date now = sdf.parse(sdf.format(new Date()));
            if (startTime != null && !startTime.after(now))
                throw new ExtraMessageException("自动发布时间不得早于当前时间");
            if (endTime != null && !endTime.after(now))
                throw new ExtraMessageException("自动关闭时间不得早于当前时间");
            if (startTime != null && endTime != null && !startTime.before(endTime))
                throw new ExtraMessageException("开始时间不得晚于结束时间");

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

            // Parameter checks of questions
            ArrayList<Question> questions = new ArrayList<>();
            boolean hasSpecialQuestion = !type.equals("vote") && !(type.equals("sign-up") && quota == null);
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
                        if (width < 300) width = 300;
                        else if (width > 800) width = 800;
                        argsMap.put("height", height);
                        argsMap.put("width", width + "px");
                        break;
                    }
                    case "grade": {
                        ArrayList<String> grades;
                        try {
                            grades = parser.toStringList(questionMap.get("grades"));
                        }
                        catch (ClassCastException e) {
                            throw new ParameterFormatException();
                        }
                        if (grades == null)
                            throw new ParameterFormatException();
                        if (grades.size() != 5)
                            throw new ParameterFormatException();
                        argsMap.put("grades", grades);
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
            Template newTemplate = new Template(type, accountId, title, description, password, conclusion,
                    quota, showIndex, startTime, endTime);
            if (templateId == 0) {
                templateId = templateService.submitTemplate(newTemplate, questions);
            }
            else {
                newTemplate.setTemplateId(templateId);
                templateService.modifyTemplate(newTemplate, questions);
            }
            String code = templateService.getTemplate(templateId).getCode();
            map.put("success", true);
            map.put("templateId", templateId);
            map.put("code", code);
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
            Boolean showIndex;
            Date startTime = null;
            Date endTime = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            // Parameter checks of template
            try {
                templateId = (Integer) requestMap.get("templateId");
                title = (String) requestMap.get("title");
                description = (String) requestMap.get("description");
                password = (String) requestMap.get("password");
                conclusion = (String) requestMap.get("conclusion");
                quota = (Integer) requestMap.get("quota");
                showIndex = (Boolean) requestMap.get("showIndex");
                String startStr = (String) requestMap.get("startTime");
                String endStr = (String) requestMap.get("endTime");
                if (startStr != null && !startStr.isEmpty()) startTime = sdf.parse(startStr);
                if (endStr != null && !endStr.isEmpty()) endTime = sdf.parse(endStr);
            }
            catch (ClassCastException cce) {
                throw new ParameterFormatException();
            }
            if (description != null && description.isEmpty()) description = null;
            if (password != null && password.isEmpty()) password = null;
            if (conclusion != null && conclusion.isEmpty()) conclusion = null;
            if (quota != null && quota <= 0) quota = null;
            if (showIndex == null)
                throw new ParameterFormatException();
            if (templateId == null || templateId < 0)
                throw new ParameterFormatException();
            if (title == null || title.isEmpty())
                throw new ParameterFormatException();
            Date now = sdf.parse(sdf.format(new Date()));
            if (startTime != null && !startTime.after(now))
                throw new ExtraMessageException("自动发布时间不得早于当前时间");
            if (endTime != null && !endTime.after(now))
                throw new ExtraMessageException("自动关闭时间不得早于当前时间");
            if (startTime != null && endTime != null && !startTime.before(endTime))
                throw new ExtraMessageException("开始时间不得晚于结束时间");

            Template template = templateService.getTemplate(templateId);
            if (template == null)
                throw new ObjectNotFoundException();
            if (!template.getOwner().equals(accountId))
                throw new LoginVerificationException();
            if (template.getDeleted())
                throw new ExtraMessageException("已删除的问卷不能编辑");
            if (template.getReleased())
                throw new ExtraMessageException("已发布的问卷不能编辑");
            if (quota != null && quota < answerDao.selectCountByTid(templateId))
                throw new ExtraMessageException("限额不得少于已收集份数");

            template.setTitle(title);
            template.setDescription(description);
            template.setPassword(password);
            template.setConclusion(conclusion);
            template.setQuota(quota);
            template.setShowIndex(showIndex);
            template.setStartTime(startTime);
            template.setEndTime(endTime);
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
