package com.buaa.qp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.buaa.qp.entity.Answer;
import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;
import com.buaa.qp.exception.ExtraMessageException;
import com.buaa.qp.exception.LoginVerificationException;
import com.buaa.qp.exception.ObjectNotFoundException;
import com.buaa.qp.exception.ParameterFormatException;
import com.buaa.qp.service.AnswerService;
import com.buaa.qp.service.QuestionService;
import com.buaa.qp.service.TemplateService;
import com.buaa.qp.util.ClassParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class CollectionController {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionService questionService;

    private static final Object quotaLock = new Object();

    @GetMapping("/attempt")
    public Map<String, Object> locked(@RequestParam(value = "templateId", required = false) String idStr) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Parameter checks
            int templateId;
            if (idStr == null)
                throw new ParameterFormatException();
            try {
                templateId = Integer.parseInt(idStr);
            } catch (Exception e) {
                throw new ParameterFormatException();
            }
            if (templateId < 0) {
                throw new ParameterFormatException();
            }
            Template template = templateService.getTemplate(templateId);
            if (template == null || template.getDeleted()) {
                throw new ObjectNotFoundException();
            } else if (!template.getReleased()) {
                throw new ExtraMessageException("问卷尚未发布, 无法访问");
            }
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (template.getType().equals("vote")) {
                Answer oldAnswer = answerService.getOldAnswer(templateId, accountId);
                if (oldAnswer != null)
                    throw new ExtraMessageException("已填过问卷");
            }
            Boolean locked = template.getPassword() != null;
            Boolean login = !template.getType().equals("normal") && accountId == null;
            map.put("success", true);
            map.put("locked", locked);
            map.put("login", login);
        } catch (ParameterFormatException | ObjectNotFoundException | ExtraMessageException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }

    @GetMapping("/details")
    public Map<String, Object> details(@RequestParam(value = "templateId", required = false) String idStr,
                                       @RequestParam(value = "password", required = false) String password,
                                       @RequestParam(value = "visitor", required = false) String vStr) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Parameter checks
            int templateId;
            boolean visitor = Boolean.parseBoolean(vStr);
            if (idStr == null)
                throw new ParameterFormatException();
            try {
                templateId = Integer.parseInt(idStr);
            }
            catch (NumberFormatException nfe) {
                throw new ParameterFormatException();
            }
            if (templateId <= 0)
                throw new ParameterFormatException();

            if (password != null && password.isEmpty()) password = null;

            // Existence checks
            Template template = templateService.getTemplate(templateId);
            if (template == null)
                throw new ObjectNotFoundException();

            // Login checks
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (!template.getType().equals("normal") && accountId == null)
                throw new LoginVerificationException();

            // Authority checks
            boolean allowed = false;
            boolean isOwner = false;
            if (template.getOwner().equals(accountId)) {
                allowed = true;
                isOwner = true;
            }
            else if (template.getReleased()) {
                String pwd = template.getPassword();
                if (pwd == null || pwd.equals(password))
                    allowed = true;
                else
                    throw new ExtraMessageException("密码错误");
            }
            if (!allowed)
                throw new ExtraMessageException("问卷不存在或无权访问");
            Integer quota = template.getQuota();
            if (isOwner) {
                map.put("conclusion", template.getConclusion());
                map.put("quota", quota);
            }
            if (!template.getType().equals("normal") && (visitor || !isOwner)) {
                Answer oldAnswer = answerService.getOldAnswer(templateId, accountId);
                if (oldAnswer != null)
                    throw new ExtraMessageException("已填过问卷");
            }
            if (quota != null) {
                int remain = quota - answerService.countAnswers(templateId);
                if (remain == 0 && (visitor || !isOwner))
                    throw new ExtraMessageException("名额已满");
                map.put("remain", remain);
            }

            ArrayList<Question> questions = templateService.getQuestionsByTid(templateId);
            ArrayList<Map<String, Object>> questionMaps = new ArrayList<>();
            for (Question question : questions) {
                Map<String, Object> questionMap = new HashMap<>();
                questionMap.put("type", question.getType());
                questionMap.put("stem", question.getStem());
                String dsc = question.getDescription();
                if (dsc != null)
                    questionMap.put("description", dsc);
                questionMap.put("required", question.getRequired());
                Map<String, Object> argsMap = JSONObject.parseObject(question.getArgs());
                questionMap.putAll(argsMap);
                questionMaps.add(questionMap);
            }
            map.put("success", true);
            map.put("type", template.getType());
            map.put("title", template.getTitle());
            String dsc = template.getDescription();
            String pwd = template.getPassword();
            if (dsc != null)
                map.put("description", dsc);
            if (pwd != null)
                map.put("password", pwd);
            map.put("questions", questionMaps);
        }
        catch (ParameterFormatException | ObjectNotFoundException |
                ExtraMessageException | LoginVerificationException exc) {
            map.clear();
            map.put("success", false);
            map.put("message", exc.toString());
        }
        catch (Exception exception) {
            exception.printStackTrace();
            map.clear();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }

    @PostMapping("/answer")
    public Map<String, Object> answer(@RequestBody Map<String, Object> requestMap) {
        Map<String, Object> map = new HashMap<>();
        try {
            // General parameter checks
            Integer templateId;
            String password;
            ArrayList<Object> answers;
            ClassParser parser = new ClassParser();
            try {
                templateId = (Integer) requestMap.get("templateId");
                password = (String) requestMap.get("password");
                answers = parser.toObjectList(requestMap.get("answers"));
            }
            catch (ClassCastException cce) {
                throw new ParameterFormatException();
            }
            if (templateId == null || templateId <= 0)
                throw new ParameterFormatException();
            if (answers == null)
                throw new ParameterFormatException();

            if (password != null && password.isEmpty()) password = null;

            // Existence checks
            Template template = templateService.getTemplate(templateId);
            if (template == null)
                throw new ObjectNotFoundException();

            // Login checks
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (!template.getType().equals("normal") && accountId == null)
                throw new LoginVerificationException();

            // Authority checks
            String pwd = template.getPassword();
            if (!template.getReleased())
                throw new ExtraMessageException("问卷可能已经关闭");
            if (pwd != null && !pwd.equals(password))
                throw new ExtraMessageException("密码错误");
            if (template.getType().equals("vote")) {
                Answer oldAnswer = answerService.getOldAnswer(templateId, accountId);
                if (oldAnswer != null)
                    throw new ExtraMessageException("已填过问卷");
            }

            // Detailed parameter checks
            ArrayList<Question> questions = templateService.getQuestionsByTid(templateId);
            if (answers.size() < questions.size())
                throw new ParameterFormatException();
            for (int i = 0; i < answers.size(); ++i) {
                Object answerObject = answers.get(i);
                Question question = questions.get(i);
                try {
                    Map<String, Object> argsMap = JSON.parseObject(question.getArgs());
                    switch (question.getType()) {
                        case "choice":
                        case "dropdown":
                        case "grade": {
                            Integer choice = (Integer) answerObject;
                            if (choice < 0) {
                                if (question.getRequired()) {
                                    throw new ParameterFormatException();
                                }
                                else
                                    answers.set(i, -1);
                            }
                            else if (choice > parser.toStringList(argsMap.get("choices")).size() - 1)
                                throw new ParameterFormatException();
                            break;
                        }
                        case "sign-up":
                        case "multi-choice":
                        case "vote": {
                            int maxIndex = parser.toStringList(argsMap.get("choices")).size() - 1;
                            ArrayList<Integer> choices = parser.toIntegerList(answerObject);
                            Set<Integer> choiceSet = new HashSet<>(choices);
                            int size = choiceSet.size();
                            if (size == 0) {
                                if (question.getRequired())
                                    throw new ParameterFormatException();
                            }
                            else if (size < (int) argsMap.get("min") || size > (int) argsMap.get("max"))
                                throw new ParameterFormatException();
                            for (Integer choice : choiceSet) {
                                if (choice < 0 || choice > maxIndex)
                                    throw new ParameterFormatException();
                            }
                            choices = new ArrayList<>(choiceSet);
                            Collections.sort(choices);
                            answers.set(i, choices);
                            break;
                        }
                        case "filling": {
                            String text = (String) answerObject;
                            if (text.isEmpty() && question.getRequired())
                                throw new ParameterFormatException();
                            break;
                        }
                        default: {
                            throw new Exception("Nested exception: unknown question type \""
                                    + question.getType() + "\"");
                        }
                    }
                }
                catch (ClassCastException | NumberFormatException |
                        NullPointerException exc) {
                    throw new ParameterFormatException();
                }
            }
            Answer answer = new Answer(templateId, JSON.toJSONString(answers), accountId);
            if (!answerService.submitAnswer(answer)) {
                throw new ExtraMessageException("名额已满");
            }
            else
                map.put("conclusion", template.getConclusion());

            // vote results
            if (template.getType().equals("vote")) {
                ArrayList<Answer> allAnswers = answerService.getAnswersByTid(templateId);
                ArrayList<Map<String, Object>> results = new ArrayList<>();
                for (Question question : questions) {
                    if (question.getType().equals("vote")) {
                        Map<String, Object> result = new HashMap<>();
                        ArrayList<String> answerContents = new ArrayList<>();
                        ArrayList<Integer> answerCounts = new ArrayList<>();
                        result.put("stem", question.getStem());
                        Map<String, Object> argsMap = JSON.parseObject(question.getArgs());
                        ArrayList<String> choices = (ArrayList<String>) JSON.parseArray(argsMap.get("choices").toString(), String.class);
                        for (String choice : choices) {
                            answerContents.add(choice);
                            answerCounts.add(0);
                        }
                        result.put("answers", answerContents);
                        result.put("counts", answerCounts);
                        results.add(result);
                    }
                }
                for (Answer answerData: allAnswers) {
                    int qIndex = 0;
                    for (int i = 0; i < questions.size(); i++) {
                        if (questions.get(i).getType().equals("vote")) {
                            List<Object> answerContents = JSONArray.parseArray(answerData.getContent(), Object.class);
                            String choiceStr = answerContents.get(i).toString();
                            ArrayList<Integer> chIndexes = (ArrayList<Integer>) JSON.parseArray(choiceStr, Integer.class);
                            for (Integer j : chIndexes) {
                                @SuppressWarnings("unchecked")
                                ArrayList<Integer> choiceCounts = (ArrayList<Integer>) results.get(qIndex).get("counts");
                                Integer number = choiceCounts.get(j);
                                number += 1;
                                choiceCounts.set(j, number);
                            }
                            qIndex ++;
                        }
                    }
                }
                map.put("results", results);
            }
            // Sign-up synchronization
            else if (template.getType().equals("sign-up")) {
                synchronized (quotaLock) {
                    questions = templateService.getQuestionsByTid(templateId);
                    for (int i = 0; i < questions.size(); ++i) {
                        Question question = questions.get(i);
                        if (question.getType().equals("sign-up")) {
                            ArrayList<Integer> choices = parser.toIntegerList(answers.get(i));
                            Map<String, Object> argsMap = JSON.parseObject(question.getArgs());
                            ArrayList<Integer> remains = parser.toIntegerList(argsMap.get("remains"));
                            for (Integer ch : choices) {
                                if (remains.get(ch) <= 0) {
                                    throw new ExtraMessageException("名额已满");
                                }
                            }
                            for (Integer ch : choices) {
                                remains.set(ch, remains.get(ch) - 1);
                            }
                            argsMap.put("remains", remains);
                            String argsStr = JSON.toJSONString(argsMap);
                            Question newQuestion = new Question();
                            newQuestion.setArgs(argsStr);
                            newQuestion.setQuestionId(question.getQuestionId());
                            questionService.updateRemains(newQuestion);
                        }
                    }
                }
            }
            map.put("success", true);
        }
        catch (ParameterFormatException | ObjectNotFoundException |
                ExtraMessageException | LoginVerificationException exc) {
            map.clear();
            map.put("success", false);
            map.put("message", exc.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
            map.clear();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }
}
