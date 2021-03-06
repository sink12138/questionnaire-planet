package com.buaa.qp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.buaa.qp.entity.Answer;
import com.buaa.qp.entity.Logic;
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

import static com.buaa.qp.service.AnswerService.quotaLock;

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

    @GetMapping("/attempt")
    public Map<String, Object> attempt(@RequestParam(value = "code", required = false) String code) {
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

            boolean answered = false;
            if (template.getLimited()) {
                Answer oldAnswer = answerService.getOldAnswer(templateId, accountId);
                if (oldAnswer != null)
                    answered = true;
            }
            Integer quota = template.getQuota();
            if (quota != null && quota <= answerService.countAnswers(templateId) && !answered)
                throw new ExtraMessageException("问卷名额已满");

            Boolean locked = template.getPassword() != null;
            Boolean login = template.getLimited() && accountId == null;
            map.put("success", true);
            map.put("locked", locked);
            map.put("login", login);
            map.put("answered", answered);
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
            if (template.getLimited() && accountId == null)
                throw new LoginVerificationException();

            // Authority checks
            boolean allowed = false;
            boolean isOwner = false;
            if (template.getOwner().equals(accountId)) {
                allowed = !visitor;
                isOwner = !visitor;
            }
            String pwd = template.getPassword();
            if (!isOwner && template.getReleased()) {
                if (pwd == null || pwd.equals(password))
                    allowed = true;
                else
                    throw new ExtraMessageException("密码错误");
            }
            if (!allowed)
                throw new ExtraMessageException("问卷不存在或无权访问");

            String conclusion = template.getConclusion();
            Integer quota = template.getQuota();
            if (template.getLimited() && (visitor || !isOwner)) {
                Answer oldAnswer = answerService.getOldAnswer(templateId, accountId);
                if (oldAnswer != null)
                    throw new ExtraMessageException("已填过问卷");
            }
            if (quota != null) {
                int remain = quota - answerService.countAnswers(templateId);
                if (remain < 0) remain = 0;
                if (remain == 0 && (visitor || !isOwner))
                    throw new ExtraMessageException("问卷已满额");
                map.put("remain", remain);
            }

            if (isOwner) {
                if (pwd != null)
                    map.put("password", pwd);
                if (conclusion != null)
                    map.put("conclusion", conclusion);
                if (quota != null)
                    map.put("quota", quota);
                map.put("limited", template.getLimited());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startTime = template.getStartTime();
            Date endTime = template.getEndTime();
            if (startTime != null) {
                map.put("startTime", sdf.format(new Date(startTime.getTime() - 28800000)));
            }
            if (endTime != null)
                map.put("endTime", sdf.format(new Date(endTime.getTime() - 28800000)));

            // Questions
            ArrayList<Question> questions = templateService.getQuestionsByTid(templateId);
            if (template.getType().equals("exam")) {
                int shuffleId;
                if (!isOwner) {
                    shuffleId = questionService.shuffleQuestions(questions, accountId, templateId);
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
                        questionMap.put("shuffle", question.getShuffle());
                        if (question.getAnswer() != null) {
                            switch (question.getType()) {
                                case "filling":
                                    questionMap.put("answer", JSON.parseArray(question.getAnswer(), String.class));
                                    break;
                                case "choice":
                                    questionMap.put("answer", Integer.parseInt(question.getAnswer()));
                                    break;
                                case "multi-choice":
                                    questionMap.put("answer", JSON.parseArray(question.getAnswer(), Integer.class));
                                    break;
                            }
                        }
                    }
                    if (question.getPoints() != null)
                        questionMap.put("points", question.getPoints());
                }
                questionMap.put("required", question.getRequired());
                Map<String, Object> argsMap = JSONObject.parseObject(question.getArgs());
                questionMap.putAll(argsMap);
                questionMaps.add(questionMap);
            }

            // Logics
            TreeSet<Logic> logics = templateService.getLogicsByTid(templateId);
            ArrayList<ArrayList<Integer>> logicTriplets = new ArrayList<>();
            for (Logic logic : logics) {
                logicTriplets.add(logic.toList());
            }

            map.put("success", true);
            map.put("type", template.getType());
            map.put("title", template.getTitle());
            String dsc = template.getDescription();
            if (dsc != null)
                map.put("description", dsc);
            map.put("showIndex", template.getShowIndex());
            map.put("questions", questionMaps);
            map.put("logic", logicTriplets);
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

    @GetMapping("/remains")
    public Map<String, Object> remains(@RequestParam(value = "code", required = false) String code,
                                       @RequestParam(value = "password", required = false) String password) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Parameter checks
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
            if (template.getLimited() && accountId == null)
                throw new LoginVerificationException();

            // Authority checks
            if (!template.getReleased())
                throw new ExtraMessageException("问卷不存在或无权访问");
            String pwd = template.getPassword();
            if (pwd != null && !pwd.equals(password))
                throw new ExtraMessageException("密码错误");
            Integer quota = template.getQuota();
            if (quota == null && !template.getType().equals("sign-up"))
                throw new ExtraMessageException("此问卷无限额信息");
            if (template.getLimited()) {
                Answer oldAnswer = answerService.getOldAnswer(templateId, accountId);
                if (oldAnswer != null)
                    throw new ExtraMessageException("已填过问卷");
            }

            if (quota != null) {
                int remain = quota - answerService.countAnswers(templateId);
                if (remain < 0) remain = 0;
                map.put("overall", remain);
            }
            ArrayList<Map<String, Object>> detailedMaps = new ArrayList<>();
            ClassParser parser = new ClassParser();
            ArrayList<Question> questions = templateService.getQuestionsByTid(templateId);
            for (int i = 0; i < questions.size(); ++i) {
                Question question = questions.get(i);
                if (question.getType().equals("sign-up")) {
                    Map<String, Object> detailedMap = new HashMap<>();
                    detailedMap.put("index", i);
                    Map<String, Object> argsMap = JSON.parseObject(question.getArgs());
                    detailedMap.put("remains", parser.toIntegerList(argsMap.get("remains")));
                    detailedMaps.add(detailedMap);
                }
            }
            map.put("detailed", detailedMaps);
            map.put("success", true);
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
                if (shuffleId == null)
                    throw new ParameterFormatException();
            }

            // Login checks
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (template.getLimited() && accountId == null)
                throw new LoginVerificationException();

            // Authority checks
            String pwd = template.getPassword();
            if (!template.getReleased())
                throw new ExtraMessageException("问卷可能已经关闭");
            if (pwd != null && !pwd.equals(password))
                throw new ExtraMessageException("密码错误");
            if (template.getLimited()) {
                Answer oldAnswer = answerService.getOldAnswer(templateId, accountId);
                if (oldAnswer != null)
                    throw new ExtraMessageException("已填过问卷");
            }

            // Detailed parameter checks
            ArrayList<Question> questions = templateService.getQuestionsByTid(templateId);
            TreeSet<Logic> logics = templateService.getLogicsByTid(templateId);
            if (answers.size() < questions.size())
                throw new ParameterFormatException();
            if (template.getType().equals("exam")) {
                questionService.makeShuffledQuestions(questions, shuffleId);
            }
            boolean[] checkList = new boolean[questions.size()];
            Arrays.fill(checkList, true);
            for (Logic logic : logics) {
                checkList[logic.getN()] = false;
            }
            for (int i = 0; i < answers.size(); ++i) {
                Object answerObject = answers.get(i);
                Question question = questions.get(i);
                if (!checkList[i]) {
                    switch (question.getType()) {
                        case "choice":
                        case "dropdown":
                        case "grade": {
                            answers.set(i, -1);
                            break;
                        }
                        case "multi-choice":
                        case "vote":
                        case "sign-up": {
                            answers.set(i, new ArrayList<>());
                        }
                        case "filling":
                        case "location": {
                            answers.set(i, "");
                        }
                    }
                    continue;
                }
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
                            for (Logic logic : logics) {
                                if (logic.getM().equals(i) && logic.getC().equals(choice))
                                    checkList[logic.getN()] = true;
                            }
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
                                choices = new ArrayList<>(new TreeSet<>(choices));
                                int size = choices.size();
                                if (size < (int) argsMap.get("min") || size > (int) argsMap.get("max"))
                                    throw new ParameterFormatException();
                                for (Integer choice : choices) {
                                    if (choice < 0 || choice > maxIndex)
                                        throw new ParameterFormatException();
                                }
                                answers.set(i, choices);
                                for (Logic logic : logics) {
                                    if (logic.getM().equals(i) && choices.contains(logic.getC()))
                                        checkList[logic.getN()] = true;
                                }
                            }
                            break;
                        }
                        case "filling":
                        case "location": {
                            String text = (String) answerObject;
                            if (text == null || text.isEmpty()) {
                                if (!question.getRequired())
                                    answers.set(i, "");
                                else
                                    throw new ExtraMessageException("有必答题未作答");
                            }
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
            if (!template.getLimited())
                accountId = null;
            if (template.getType().equals("exam")) {
                answer = new Answer(templateId, JSON.toJSONString(answerService.reorderAnswer(answers, shuffleId)), accountId);
            }
            else
                answer = new Answer(templateId, JSON.toJSONString(answers), accountId);

            // Calculate the score if this is an exam
            if (template.getType().equals("exam")) {
                Map<String, Object> calculateResult = examCalculate(answers, questions);
                String points = (String) calculateResult.get("points");
                // Set the points of the answer for database storage
                answer.setPoints(points);
            }

            // Submit the answer
            Integer answerId;
            Integer quota = template.getQuota();
            if (quota == null && !template.getType().equals("sign-up"))
                answerId = answerService.submitAnswer(answer);
            else synchronized (quotaLock) {
                int answerCount = answerService.countAnswers(templateId);
                if (quota != null && answerCount >= quota)
                    throw new ExtraMessageException("问卷已满额");

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
                                throw new ExtraMessageException("部分选项已满额");
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
                answerId = answerService.submitAnswer(answer);
            }
            map.put("success", true);
            map.put("answerId", answerId);
        }
        catch (ParameterFormatException | ObjectNotFoundException |
                ExtraMessageException | LoginVerificationException exc) {
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

    @GetMapping("/results")
    public Map<String, Object> results(@RequestParam(value = "code", required = false) String code,
                                       @RequestParam(value = "shuffleId", required = false) String shuffleStr,
                                       @RequestParam(value = "answerId", required = false) String answerStr) {
        Map<String, Object> map = new HashMap<>();
        try {
            // Parameter checks
            if (code == null) {
                throw new ParameterFormatException();
            }
            Template template = templateService.getTemplate(code);
            if (template == null || template.getDeleted()) {
                throw new ObjectNotFoundException();
            }

            Integer templateId = template.getTemplateId();
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (template.getLimited() && accountId == null)
                throw new LoginVerificationException();

            Answer answer;
            if (template.getLimited()) {
                answer = answerService.getOldAnswer(templateId, accountId);
            }
            else {
                if (answerStr == null)
                    throw new ParameterFormatException();
                int answerId;
                try {
                    answerId = Integer.parseInt(answerStr);
                }
                catch (NumberFormatException nfe) {
                    throw new ParameterFormatException();
                }
                answer = answerService.getOldAnswer(answerId);
            }
            if (answer == null) {
                throw new ExtraMessageException("尚未填写此问卷");
            }

            ArrayList<Object> answers = (ArrayList<Object>) JSON.parseArray(answer.getContent(), Object.class);
            ArrayList<Question> questions = templateService.getQuestionsByTid(templateId);
            if (template.getType().equals("exam")) {
                if (shuffleStr == null)
                    throw new ParameterFormatException();
                int shuffleId;
                try {
                    shuffleId = Integer.parseInt(shuffleStr);
                }
                catch (NumberFormatException nfe) {
                    throw new ParameterFormatException();
                }
                if (template.getLimited()) {
                    answerService.shuffleAnswer(answers, accountId, templateId);
                    questionService.makeShuffledQuestions(questions, accountId, templateId);
                }
                else {
                    answerService.shuffleAnswer(answers, shuffleId);
                    questionService.makeShuffledQuestions(questions, shuffleId);
                }
                Map<String, Object> calculateResults = examCalculate(answers, questions);
                map.put("results", calculateResults.get("results"));
                map.put("points", calculateResults.get("points"));
            } else if (template.getType().equals("vote")) {
                map.put("results", voteCalculate(templateId));
            }
            map.put("type", template.getType());

            if (template.getConclusion() != null)
                map.put("conclusion", template.getConclusion());
            map.put("success", true);
        } catch (ParameterFormatException | ObjectNotFoundException |
                ExtraMessageException | LoginVerificationException exc) {
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

    private ArrayList<Map<String, Object>> voteCalculate(Integer templateId) {
        ArrayList<Answer> allAnswers = answerService.getAnswersByTid(templateId);
        ArrayList<Question> questions = templateService.getQuestionsByTid(templateId);
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
        return results;
    }

    private Map<String, Object> examCalculate(ArrayList<Object> answers, ArrayList<Question> questions) {
        Map<String, Object> calculateResult = new HashMap<>();
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
                double getPoints = 0;
                if (current_q.getPoints() != null)
                    fullMarks += Double.parseDouble(current_q.getPoints());
                if (type.equals("filling")) {
                    result.put("yourAnswer", answers.get(i));
                    ArrayList<String> correctAnswers = (ArrayList<String>) JSON.parseArray(current_q.getAnswer(), String.class);
                    result.put("correctAnswer", correctAnswers);
                    if (correctAnswers.contains((String) answers.get(i))) {
                        getPoints = Double.parseDouble(current_q.getPoints());
                    }
                    result.put("points", String.format("%.2f/%.2f", getPoints, Double.parseDouble(current_q.getPoints())));
                } else if (type.equals("choice") || type.equals("multi-choice")){
                    Map<String, Object> argsMap = JSON.parseObject(current_q.getArgs());
                    ArrayList<String> choices = (ArrayList<String>) JSON.parseArray(argsMap.get("choices").toString(), String.class);
                    result.put("choices", choices);
                    if (type.equals("multi-choice")) {
                        ArrayList<Integer> correctChoices = (ArrayList<Integer>) JSON.parseArray(current_q.getAnswer(), Integer.class);
                        result.put("correctAnswer", correctChoices);
                        ClassParser parser = new ClassParser();
                        ArrayList<Integer> yourChoice = parser.toIntegerList(answers.get(i));
                        if (yourChoice.isEmpty())
                            result.put("yourAnswer", "");
                        else
                            result.put("yourAnswer", yourChoice);
                        if (current_q.getPoints() != null) {
                            if (yourChoice.size() <= correctChoices.size()) {
                                int correctNum = 0;
                                for (int index : correctChoices) {
                                    if (yourChoice.contains(index)) {
                                        correctNum++;
                                    }
                                }
                                if (correctNum == yourChoice.size()) {
                                    getPoints = Double.parseDouble(current_q.getPoints()) * ((double) correctNum / (double) correctChoices.size());
                                }
                            }
                            result.put("points", String.format("%.2f/%.2f", getPoints, Double.parseDouble(current_q.getPoints())));
                        }
                    } else {
                        int correctChoice = Integer.parseInt(current_q.getAnswer());
                        int yourChoice = (Integer) answers.get(i);
                        if (yourChoice < 0) {
                            result.put("yourAnswer", "");
                        } else {
                            result.put("yourAnswer", yourChoice);
                        }
                        result.put("correctAnswer", correctChoice);
                        if (current_q.getPoints() != null && yourChoice == correctChoice) {
                            getPoints = Double.parseDouble(current_q.getPoints());
                        }
                        assert current_q.getPoints() != null;
                        result.put("points", String.format("%.2f/%.2f", getPoints, Double.parseDouble(current_q.getPoints())));
                    }
                }
                totalMarks += getPoints;
                String stem = (String) (result.get("stem"));
                result.put("stem", (i + 1) + "." + stem);
                results.add(result);
            }
        }

        // Add results to the returned map
        calculateResult.put("results", results);
        String points = String.format("%.2f/%.2f", totalMarks, fullMarks);
        calculateResult.put("points", points);
        return calculateResult;
    }

}
