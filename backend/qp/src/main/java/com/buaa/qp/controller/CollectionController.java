package com.buaa.qp.controller;

import com.alibaba.fastjson.JSON;
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
import java.text.SimpleDateFormat;
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
    public Map<String, Object> locked(@RequestParam(value = "code", required = false) String code) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Parameter checks
            if (code == null)
                throw new ParameterFormatException();

            // Authority checks
            Template template = templateService.getTemplate(code);
            if (template == null || template.getDeleted())
                throw new ObjectNotFoundException();
            else if (!template.getReleased())
                throw new ExtraMessageException("问卷尚未发布或已关闭");
            Integer templateId = template.getTemplateId();
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

    @GetMapping("/time")
    public Map<String, Object> time() {
        Map<String, Object> map = new HashMap<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            map.put("time", sdf.format(new Date()));
            map.put("success", true);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }

    @GetMapping("/details")
    public Map<String, Object> details(@RequestParam(value = "code", required = false) String code,
                                       @RequestParam(value = "password", required = false) String password,
                                       @RequestParam(value = "visitor", required = false) String vStr) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Parameter checks
            boolean visitor = Boolean.parseBoolean(vStr);
            if (code == null)
                throw new ParameterFormatException();

            if (password != null && password.isEmpty()) password = null;

            // Existence checks
            Template template = templateService.getTemplate(code);
            if (template == null)
                throw new ObjectNotFoundException();
            int templateId = template.getTemplateId();

            // Login checks
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (!template.getType().equals("normal") && accountId == null)
                throw new LoginVerificationException();

            // Authority checks
            boolean allowed = false;
            boolean isOwner = false;
            if (template.getOwner().equals(accountId)) {
                allowed = !visitor;
                isOwner = !visitor;
            }
            if (!isOwner && template.getReleased()) {
                String pwd = template.getPassword();
                if (pwd == null || pwd.equals(password))
                    allowed = true;
                else
                    throw new ExtraMessageException("密码错误");
            }
            if (!allowed)
                throw new ExtraMessageException("问卷不存在或无权访问");

            Integer quota = template.getQuota();
            String conclusion = template.getConclusion();
            if (isOwner) {
                if (conclusion != null)
                    map.put("conclusion", conclusion);
                if (quota != null)
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startTime = template.getStartTime();
            Date endTime = template.getEndTime();
            if (startTime != null) {
                map.put("startTime", sdf.format(new Date(startTime.getTime() - 28800000)));
            }
            if (endTime != null)
                map.put("endTime", sdf.format(new Date(endTime.getTime() - 28800000)));

            ArrayList<Question> questions = templateService.getQuestionsByTid(templateId);
            if (template.getType().equals("exam")) {
                int shuffleId;
                if (!isOwner) {
                    if (accountId == null) {
                        shuffleId = questionService.shuffleQuestions(questions, null);
                    } else {
                        shuffleId = questionService.shuffleQuestions(questions, accountId, templateId);
                    }
                    map.put("shuffleId", shuffleId);
                }
            }

            ArrayList<Map<String, Object>> questionMaps = new ArrayList<>();
            for (Question question : questions) {
                Map<String, Object> questionMap = new HashMap<>();
                questionMap.put("type", question.getType());
                questionMap.put("stem", question.getStem());
                String dsc = question.getDescription();
                if (dsc != null)
                    questionMap.put("description", dsc);
                if (template.getType().equals("exam")) {
                    if (isOwner) {
                        questionMap.put("answer", question.getAnswer());
                        questionMap.put("shuffle", question.getShuffle());
                    }
                    questionMap.put("points", question.getPoints());
                }
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
            map.put("showIndex", template.getShowIndex());
            map.put("questions", questionMaps);
        }
        catch (ParameterFormatException | ObjectNotFoundException |
                ExtraMessageException | LoginVerificationException exc) {
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
            String code;
            String password;
            Integer shuffleId = null;
            ArrayList<Object> answers;
            ClassParser parser = new ClassParser();
            try {
                code = (String) requestMap.get("code");
                password = (String) requestMap.get("password");
                answers = parser.toObjectListWithNulls(requestMap.get("answers"));
            }
            catch (ClassCastException cce) {
                throw new ParameterFormatException();
            }
            if (code == null)
                throw new ParameterFormatException();
            if (answers == null)
                throw new ParameterFormatException();

            if (password != null && password.isEmpty()) password = null;

            // Existence checks
            Template template = templateService.getTemplate(code);
            if (template == null)
                throw new ObjectNotFoundException();
            int templateId = template.getTemplateId();

            if (template.getType().equals("exam")) {
                try {
                    shuffleId = (Integer) requestMap.get("shuffleId");
                }
                catch (ClassCastException cce) {
                    throw new ParameterFormatException();
                }
            }

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
            if (!template.getType().equals("normal")) {
                Answer oldAnswer = answerService.getOldAnswer(templateId, accountId);
                if (oldAnswer != null)
                    throw new ExtraMessageException("已填过问卷");
            }

            // Detailed parameter checks
            ArrayList<Question> questions = templateService.getQuestionsByTid(templateId);
            if (answers.size() < questions.size())
                throw new ParameterFormatException();
            if (template.getType().equals("exam")) {
                questionService.shuffleQuestions(questions, shuffleId);
            }
            for (int i = 0; i < answers.size(); ++i) {
                Object answerObject = answers.get(i);
                Question question = questions.get(i);
                if (answerObject == null && question.getRequired())
                    throw new ExtraMessageException("有必答题未作答");
                try {
                    Map<String, Object> argsMap = JSON.parseObject(question.getArgs());
                    switch (question.getType()) {
                        case "choice":
                        case "dropdown": {
                            Integer choice = (Integer) answerObject;
                            if (choice == null) {
                                answers.set(i, -1);
                            }
                            else if (choice < 0 || choice > parser.toStringList(argsMap.get("choices")).size() - 1)
                                throw new ParameterFormatException();
                            break;
                        }
                        case "grade": {
                            Integer choice = (Integer) answerObject;
                            if (choice == null) {
                                answers.set(i, -1);
                            }
                            else if (choice < 0 || choice > 4)
                                throw new ParameterFormatException();
                            break;
                        }
                        case "sign-up":
                        case "multi-choice":
                        case "vote": {
                            ArrayList<Integer> choices = parser.toIntegerList(answerObject);
                            if (choices == null)
                                answers.set(i, new ArrayList<>());
                            else {
                                int maxIndex = parser.toStringList(argsMap.get("choices")).size() - 1;
                                Set<Integer> choiceSet = new HashSet<>(choices);
                                int size = choiceSet.size();
                                if (size < (int) argsMap.get("min") || size > (int) argsMap.get("max"))
                                    throw new ParameterFormatException();
                                for (Integer choice : choiceSet) {
                                    if (choice < 0 || choice > maxIndex)
                                        throw new ParameterFormatException();
                                }
                                choices = new ArrayList<>(choiceSet);
                                Collections.sort(choices);
                                answers.set(i, choices);
                            }
                            break;
                        }
                        case "filling": {
                            String text = (String) answerObject;
                            if (text == null || text.isEmpty())
                                answers.set(i, "");
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

            // Create answer entity
            Answer answer;
            if (template.getType().equals("normal"))
                answer = new Answer(templateId, JSON.toJSONString(answers));
            else if (template.getType().equals("exam")) {
                answer = new Answer(templateId, JSON.toJSONString(answerService.reorderAnswer(answers, shuffleId)), accountId);
            }
            else
                answer = new Answer(templateId, JSON.toJSONString(answers), accountId);

            // Calculate the score if this is an exam
            if (template.getType().equals("exam")) {
                ArrayList<Map<String, Object>> results = new ArrayList<>();
                double fullMarks = 0;
                double totalMarks = 0;
                for (int i = 0; i < questions.size(); i++) {
                    Question current_q = questions.get(i);
                    Map<String, Object> result = new HashMap<>();
                    if (current_q.getAnswer() != null){
                        result.put("stem", current_q.getStem());
                        String type = current_q.getType();
                        result.put("type", type);
                        if (type.equals("filling")) {
                            result.put("yourAnswer", answers.get(i));
                            result.put("correctAnswer", current_q.getAnswer());
                        } else if (type.equals("choice") || type.equals("multi-choice")){
                            Map<String, Object> argsMap = JSON.parseObject(current_q.getArgs());
                            ArrayList<String> choices = (ArrayList<String>) JSON.parseArray(argsMap.get("choices").toString(), String.class);
                            result.put("choices", choices);
                            double getPoints = 0;
                            fullMarks += Double.parseDouble(current_q.getPoints());
                            if (type.equals("multi-choice")) {
                                ArrayList<Integer> correctChoices = (ArrayList<Integer>) JSON.parseArray(current_q.getAnswer(), Integer.class);
                                result.put("correctAnswer", correctChoices);
                                ArrayList<Integer> yourChoice = parser.toIntegerList(answers.get(i));
                                if (yourChoice.isEmpty())
                                    result.put("yourAnswer", "");
                                if (yourChoice.size() <= correctChoices.size()) {
                                    int correctNum = 0;
                                    for (int index : correctChoices) {
                                        if (yourChoice.contains(index)) {
                                            correctNum ++;
                                        }
                                    }
                                    if (correctNum == yourChoice.size()) {
                                        getPoints = Double.parseDouble(current_q.getPoints()) * ((double) correctNum / (double) correctChoices.size());
                                    }
                                }
                                totalMarks += getPoints;
                                result.put("points", String.format("%.1f/%.1f", getPoints, Double.parseDouble(current_q.getPoints())));
                            } else {
                                int correctChoice = Integer.parseInt(current_q.getAnswer());
                                int yourChoice = (Integer) answers.get(i);
                                if (yourChoice < 0) {
                                    result.put("yourAnswer", "");
                                }
                                result.put("correctAnswer", correctChoice);
                                if (yourChoice == correctChoice) {
                                    totalMarks += Double.parseDouble(current_q.getPoints());
                                }
                            }
                        }
                    }
                    results.add(result);
                }
                int i = 1;
                for (Map<String, Object> result : results) {
                    if (template.getShowIndex()) {
                        String stem = (String) (result.get("stem"));
                        result.put("stem", i + "." + stem);
                    }
                    i ++;
                }
                map.put("results", results);
                String points = String.format("%.2f/%.2f", totalMarks, fullMarks);
                map.put("points", points);
                answer.setPoints(points);
            }

            // Submit the answer
            Integer quota = template.getQuota();
            if (quota == null && !template.getType().equals("sign-up"))
                answerService.submitAnswer(answer);
            else synchronized (quotaLock) {
                int answerCount = answerService.countAnswers(templateId);
                if (quota != null && answerCount >= quota)
                    throw new ExtraMessageException("问卷名额已满");

                // Synchronization for sign-up questionnaires
                if (template.getType().equals("sign-up")) {
                    questions = templateService.getQuestionsByTid(templateId);
                    ArrayList<Question> signUpQuestions = new ArrayList<>();
                    ArrayList<Map<String, Object>> argsMaps = new ArrayList<>();
                    ArrayList<ArrayList<Integer>> remainsList = new ArrayList<>();
                    ArrayList<ArrayList<Integer>> signUpAnswers = new ArrayList<>();
                    // Parsing JSON and args
                    for (int i = 0; i < questions.size(); ++i) {
                        Question question = questions.get(i);
                        if (question.getType().equals("sign-up")) {
                            signUpQuestions.add(question);
                            Map<String, Object> argsMap = JSON.parseObject(question.getArgs());
                            argsMaps.add(argsMap);
                            remainsList.add(parser.toIntegerList(argsMap.get("remains")));
                            signUpAnswers.add(parser.toIntegerList(answers.get(i)));
                        }
                    }
                    // Quota checks for every choice
                    for (int i = 0; i < signUpQuestions.size(); i++) {
                        ArrayList<Integer> remains = remainsList.get(i);
                        ArrayList<Integer> choices = signUpAnswers.get(i);
                        for (Integer ch : choices) {
                            if (remains.get(ch) <= 0)
                                throw new ExtraMessageException("部分选项名额已满");
                        }
                    }
                    // Update the remains
                    for (int i = 0; i < signUpQuestions.size(); i++) {
                        Question question = signUpQuestions.get(i);
                        Map<String, Object> argsMap = argsMaps.get(i);
                        ArrayList<Integer> remains = remainsList.get(i);
                        ArrayList<Integer> choices = signUpAnswers.get(i);
                        for (Integer ch : choices) {
                            remains.set(ch, remains.get(ch) - 1);
                        }
                        argsMap.put("remains", remains);
                        question.setArgs(JSON.toJSONString(argsMap));
                        questionService.updateRemains(question);
                    }
                }

                // Insert the answer into the database
                answerService.submitAnswer(answer);
                // Close the questionnaire if the quota has been reached
                if (quota != null && answerCount + 1 >= quota)
                    templateService.releaseTemplate(templateId, false);
            }

            // Collect the conclusion and results after the submission
            if (template.getConclusion() != null)
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
                            List<Object> answerContents = JSON.parseArray(answerData.getContent(), Object.class);
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
