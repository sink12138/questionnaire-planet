package com.buaa.qp.controller;

import com.alibaba.fastjson.JSON;
import com.buaa.qp.entity.Logic;
import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;
import com.buaa.qp.exception.ExtraMessageException;
import com.buaa.qp.exception.LoginVerificationException;
import com.buaa.qp.exception.ObjectNotFoundException;
import com.buaa.qp.exception.ParameterFormatException;
import com.buaa.qp.service.AnswerService;
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
    AnswerService answerService;

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
            Boolean limited;
            ArrayList<Map<String, Object>> questionMaps;
            ArrayList<Object> logicList;
            ClassParser parser = new ClassParser();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            // Parameter checks of template
            try {
                templateId = (Integer) requestMap.get("templateId");
                title = (String) requestMap.get("title");
                type = (String) requestMap.get("type");
                showIndex = (Boolean) requestMap.get("showIndex");
                if (showIndex == null) showIndex = true;
                limited = (Boolean) requestMap.get("limited");
                if (limited == null) limited = false;
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
                logicList = parser.toObjectList(requestMap.get("logic"));
                if (logicList == null) logicList = new ArrayList<>();
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
            if (type == null)
                throw new ParameterFormatException();
            if (!type.equals("normal") && !type.equals("vote") && !type.equals("sign-up")
                    && !type.equals("exam") && !type.equals("epidemic"))
                throw new ParameterFormatException();
            Date now = sdf.parse(sdf.format(new Date()));
            if (startTime != null && !startTime.after(now))
                throw new ExtraMessageException("??????????????????????????????????????????");
            if (endTime != null && !endTime.after(now))
                throw new ExtraMessageException("??????????????????????????????????????????");
            if (startTime != null && endTime != null && !startTime.before(endTime))
                throw new ExtraMessageException("????????????????????????????????????");

            if (templateId > 0) {
                Template template = templateService.getTemplate(templateId);
                if (template == null)
                    throw new ObjectNotFoundException();
                if (!template.getOwner().equals(accountId))
                    throw new LoginVerificationException();
                if (template.getReleased())
                    throw new ExtraMessageException("??????????????????????????????");
                if (template.getDeleted())
                    throw new ExtraMessageException("??????????????????????????????");
            }

            // Parameter checks of questions
            ArrayList<Question> questions = makeQuestions(type, quota, questionMaps);

            // Parameter checks of logics
            TreeSet<Logic> logics = new TreeSet<>();
            try {
                int size = questions.size();
                for (Object logicObject : logicList) {
                    ArrayList<Integer> logic = parser.toIntegerList(logicObject);
                    if (logic == null)
                        continue;
                    if (logic.size() < 3)
                        throw new ParameterFormatException();
                    if (logic.size() > 3)
                        logic = new ArrayList<>(logic.subList(0, 3));
                    if (logic.contains(null))
                        throw new ParameterFormatException();
                    // Logic tuple: (m, c, n)
                    int m = logic.get(0), c = logic.get(1), n = logic.get(2);
                    if (m < 0 || m >= size || n < 0 || n >= size)
                        throw new ParameterFormatException();
                    String questionType = questions.get(logic.get(0)).getType();
                    if (questionType.equals("filling") || questionType.equals("grade") || questionType.equals("location"))
                        throw new ExtraMessageException("????????????????????????????????????????????????");
                    if (c < 0 || c >= parser.toObjectList(questionMaps.get(m).get("choices")).size())
                        throw new ParameterFormatException();
                    if (m == n) {
                        throw new ExtraMessageException("?????????????????????????????????????????????");
                    }
                    if (m > n)
                        throw new ExtraMessageException("???????????????????????????????????????????????????");
                    if (questions.get(m).getShuffle() || questions.get(n).getShuffle())
                        throw new ExtraMessageException("?????????????????????????????????????????????");
                    logics.add(new Logic(logic));
                }
            }
            catch (ClassCastException | NumberFormatException e) {
                throw new ParameterFormatException();
            }

            Template newTemplate = new Template(type, accountId, title, description, password, conclusion,
                    quota, showIndex, startTime, endTime, limited);

            if (templateId == 0) {
                templateId = templateService.submitTemplate(newTemplate, questions, logics);
            }
            else {
                newTemplate.setTemplateId(templateId);
                templateService.modifyTemplate(newTemplate, questions, logics);
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
            map.put("message", "????????????");
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
            Boolean limited;
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
                limited = (Boolean) requestMap.get("limited");
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
            if (showIndex == null) showIndex = true;
            if (limited == null) limited = false;

            if (templateId == null || templateId < 0)
                throw new ParameterFormatException();
            if (title == null || title.isEmpty())
                throw new ParameterFormatException();
            Date now = sdf.parse(sdf.format(new Date()));
            if (startTime != null && !startTime.after(now))
                throw new ExtraMessageException("??????????????????????????????????????????");
            if (endTime != null && !endTime.after(now))
                throw new ExtraMessageException("??????????????????????????????????????????");
            if (startTime != null && endTime != null && !startTime.before(endTime))
                throw new ExtraMessageException("????????????????????????????????????");

            Template template = templateService.getTemplate(templateId);
            if (template == null)
                throw new ObjectNotFoundException();
            if (!template.getOwner().equals(accountId))
                throw new LoginVerificationException();
            if (template.getDeleted())
                throw new ExtraMessageException("??????????????????????????????");
            if (template.getReleased() && startTime != null)
                throw new ExtraMessageException("????????????????????????????????????????????????");

            template.setTitle(title);
            template.setDescription(description);
            template.setPassword(password);
            template.setConclusion(conclusion);
            template.setQuota(quota);
            template.setShowIndex(showIndex);
            template.setLimited(limited);
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
            map.put("message", "????????????");
        }
        return map;
    }

    private ArrayList<Question> makeQuestions(String templateType,Integer templateQuota,
                                              ArrayList<Map<String, Object>> questionMaps)
            throws ParameterFormatException, ExtraMessageException {
        ArrayList<Question> questions = new ArrayList<>();
        ClassParser parser = new ClassParser();
        boolean hasSpecialQuestion = !templateType.equals("vote") && !(templateType.equals("sign-up") && templateQuota == null);
        boolean isExam = templateType.equals("exam");
        for (Map<String, Object> questionMap : questionMaps) {
            String questionType;
            String questionStem;
            String questionDescription;
            Boolean questionRequired;
            String questionAnswer = null;
            Double questionPoints = null;
            boolean questionShuffle = false;
            try {
                questionType = (String) questionMap.get("type");
                questionStem = (String) questionMap.get("stem");
                questionDescription = (String) questionMap.get("description");
                if (questionDescription != null && questionDescription.isEmpty()) questionDescription = null;
                questionRequired = (Boolean) questionMap.get("required");
                if (questionRequired == null) questionRequired = true;
            }
            catch (ClassCastException cce) {
                throw new ParameterFormatException();
            }
            if (questionType == null || questionStem == null)
                throw new ParameterFormatException();
            Map<String, Object> argsMap = new HashMap<>();
            switch (questionType) {
                case "choice": {
                    ArrayList<String> choices;
                    Integer answerChoice = null;
                    try {
                        choices = parser.toStringList(questionMap.get("choices"));
                        if (isExam) {
                            answerChoice = (Integer) questionMap.get("answer");
                            if (questionMap.get("points") != null) {
                                questionPoints = Double.parseDouble(questionMap.get("points").toString());
                                if (questionPoints <= 0) questionPoints = null;
                            }
                            if (questionMap.get("shuffle") != null)
                                questionShuffle = Boolean.parseBoolean(questionMap.get("shuffle").toString());
                        }
                    }
                    catch (ClassCastException | NumberFormatException e) {
                        throw new ParameterFormatException();
                    }
                    if (choices == null || choices.size() < 2)
                        throw new ParameterFormatException();
                    if (questionPoints != null && answerChoice == null)
                        throw new ExtraMessageException("?????????????????????????????????????????????");
                    if (answerChoice != null) {
                        if (answerChoice < 0 || answerChoice > choices.size() - 1)
                            throw new ParameterFormatException();
                        questionAnswer = answerChoice.toString();
                    }
                    argsMap.put("choices", choices);
                    break;
                }
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
                    ArrayList<Integer> answerChoices = null;
                    Integer max;
                    Integer min;
                    try {
                        choices = parser.toStringList(questionMap.get("choices"));
                        max = (Integer) questionMap.get("max");
                        min = (Integer) questionMap.get("min");
                        if (isExam) {
                            answerChoices = parser.toIntegerList(questionMap.get("answer"));
                            if (answerChoices != null && answerChoices.isEmpty()) answerChoices = null;
                            if (questionMap.get("points") != null) {
                                questionPoints = Double.parseDouble(questionMap.get("points").toString());
                                if (questionPoints <= 0) questionPoints = null;
                            }
                            if (questionMap.get("shuffle") != null)
                                questionShuffle = Boolean.parseBoolean(questionMap.get("shuffle").toString());
                        }
                    }
                    catch (ClassCastException | NumberFormatException e) {
                        throw new ParameterFormatException();
                    }
                    if (choices == null || choices.size() < 2)
                        throw new ParameterFormatException();
                    if (max == null || max > choices.size()) max = choices.size();
                    if (min == null || min < 1) min = 1;
                    if (max < 2 || max < min || max.equals(min) && min == choices.size())
                        throw new ParameterFormatException();
                    if (questionPoints != null && answerChoices == null)
                        throw new ExtraMessageException("?????????????????????????????????????????????");
                    if (answerChoices != null) {
                        int maxIndex = choices.size() - 1;
                        answerChoices = new ArrayList<>(new TreeSet<>(answerChoices));
                        int size = answerChoices.size();
                        if (size < min || size > max)
                            throw new ParameterFormatException();
                        for (Integer answerIndex : answerChoices) {
                            if (answerIndex < 0 || answerIndex > maxIndex)
                                throw new ParameterFormatException();
                        }
                        questionAnswer = JSON.toJSONString(answerChoices);
                    }
                    argsMap.put("choices", choices);
                    argsMap.put("max", max);
                    argsMap.put("min", min);
                    break;
                }
                case "filling": {
                    Integer height;
                    Integer width;
                    ArrayList<String> answerTexts = null;
                    try {
                        height = (Integer) questionMap.get("height");
                        width = (Integer) questionMap.get("width");
                        if (isExam) {
                            answerTexts = parser.toStringList(questionMap.get("answer"));
                            if (answerTexts!= null && answerTexts.isEmpty()) answerTexts = null;
                            if (answerTexts != null) {
                                while (answerTexts.contains(""))
                                    answerTexts.remove("");
                                if (answerTexts.isEmpty()) answerTexts = null;
                            }
                            if (questionMap.get("points") != null) {
                                questionPoints = Double.parseDouble(questionMap.get("points").toString());
                                if (questionPoints <= 0) questionPoints = null;
                            }
                            if (questionMap.get("shuffle") != null)
                                questionShuffle = Boolean.parseBoolean(questionMap.get("shuffle").toString());
                        }
                    }
                    catch (ClassCastException cce) {
                        throw new ParameterFormatException();
                    }
                    if (height == null || height <= 0) height = 1;
                    else if (height > 10) height = 10;
                    if (width == null) width = 400;
                    else if (width < 200) width = 200;
                    else if (width > 650) width = 650;
                    argsMap.put("height", height);
                    argsMap.put("width", width + "px");
                    if (questionPoints != null && answerTexts == null)
                        throw new ExtraMessageException("?????????????????????????????????????????????");
                    if (answerTexts != null) {
                        questionAnswer = JSON.toJSONString(answerTexts);
                    }
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
                    if (!templateType.equals("vote"))
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
                    if (!templateType.equals("sign-up"))
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
                        if (q < 1)
                            throw new ExtraMessageException("??????????????????");
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
                case "location": {
                    break;
                }
                default:
                    throw new ParameterFormatException();
            }
            String args = JSON.toJSONString(argsMap);
            if (isExam) {
                String pointsFormat = null;
                if (questionPoints != null) {
                    double rounded = Math.round(questionPoints * 2) / 2.0;
                    if (rounded >= 100.25)
                        throw new ExtraMessageException("????????????");
                    if (rounded < 0.25)
                        throw new ExtraMessageException("????????????");
                    pointsFormat = String.format("%.1f", rounded);
                }
                questions.add(new Question(questionType, questionStem, questionDescription, questionRequired, args,
                        questionAnswer, pointsFormat, questionShuffle));
            }
            else {
                questions.add(new Question(questionType, questionStem, questionDescription, questionRequired, args));
            }
        }
        if (!hasSpecialQuestion) {
            switch (templateType) {
                case "vote": throw new ExtraMessageException("?????????????????????????????????");
                case "sign-up": throw new ExtraMessageException("????????????????????????????????????????????????");
            }
            throw new ExtraMessageException("?????????????????????????????????");
        }
        return questions;
    }
}
