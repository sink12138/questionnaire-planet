package com.buaa.qp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.buaa.qp.entity.Answer;
import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;
import com.buaa.qp.exception.ExtraMessageException;
import com.buaa.qp.exception.LoginVerificationException;
import com.buaa.qp.exception.ObjectNotFoundException;
import com.buaa.qp.exception.ParameterFormatException;
import com.buaa.qp.service.AnswerService;
import com.buaa.qp.service.TemplateService;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DataController {
    @Autowired
    private AnswerService answerService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/data")
    public Map<String, Object> data(@RequestParam("templateId") Integer templateId) {
        Map<String, Object> map = new HashMap<>();
        try {
            // check login
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (accountId == null)
                throw new LoginVerificationException();

            Template template = templateService.getTemplate(templateId);
            if (template == null || template.getDeleted()) {
                throw new ObjectNotFoundException();
            } else if (!template.getOwner().equals(accountId)) {
                throw new LoginVerificationException();
            }

            // get data
            ArrayList<Answer> answers = answerService.getAnswersByTid(templateId);
            ArrayList<Question> questions = templateService.getQuestionsByTid(templateId);
            ArrayList<ArrayList<String>> answersInFormat = new ArrayList<>();
            ArrayList<String> stems = new ArrayList<>();
            stems.add(null);
            int indexOfStem = 1;
            for (Question question : questions) {
                stems.add(indexOfStem + "." + question.getStem());
                indexOfStem += 1;
            }
            answersInFormat.add(stems);
            for (Answer answer : answers) {
                ArrayList<String> answerInFormat = new ArrayList<>();
                answerInFormat.add(answer.getAnswerId().toString());
                List<Object> answerContents = JSONArray.parseArray(answer.getContent(), Object.class);
                for (int i = 0; i < questions.size(); i++) {
                    Question current_question = questions.get(i);
                    if (answerContents.get(i) == null) {
                        answerInFormat.add("");
                    } else {
                        Map<String, Object> argsMap = (Map<String, Object>) JSON.parse(current_question.getArgs());
                        switch (current_question.getType()) {
                            case "choice": {
                                ArrayList<String> choices = (ArrayList<String>) JSON.parseArray(argsMap.get("choices").toString(), String.class);
                                Integer chIndex = (Integer) answerContents.get(i);
                                String answerStr = (char) ((int) 'A' + chIndex) + "." + choices.get(chIndex);
                                answerInFormat.add(answerStr);
                                break;
                            }
                            case "multi-choice": {
                                ArrayList<String> choices = (ArrayList<String>) JSON.parseArray(argsMap.get("choices").toString(), String.class);
                                String choiceStr = answerContents.get(i).toString();
                                ArrayList<Integer> chIndexes = (ArrayList<Integer>) JSON.parseArray(choiceStr, Integer.class);
                                StringBuilder stringBuilder = new StringBuilder();
                                assert chIndexes != null;
                                for (Integer j : chIndexes) {
                                    stringBuilder.append((char) ((int) 'A' + j)).append(".").append(choices.get(j));
                                    if (chIndexes.indexOf(j) != chIndexes.size() - 1) {
                                        stringBuilder.append(";");
                                    }
                                }
                                String answerStr = stringBuilder.toString();
                                answerInFormat.add(answerStr);
                                break;
                            }
                            case "filling": {
                                String answerStr = (String) answerContents.get(i);
                                answerInFormat.add(answerStr);
                                break;
                            }
                            case "grade": {
                                Integer chIndex = (Integer) answerContents.get(i);
                                ArrayList<String> scores = (ArrayList<String>) JSON.parseArray(argsMap.get("scores").toString(), String.class);
                                ArrayList<String> choices = (ArrayList<String>) JSON.parseArray(argsMap.get("choices").toString(), String.class);
                                answerInFormat.add(choices.get(chIndex) + "(" + scores.get(chIndex) + ")");
                                break;
                            }
                            case "dropdown": {
                                Integer chIndex = (Integer) answerContents.get(i);
                                ArrayList<String> choices = (ArrayList<String>) JSON.parseArray(argsMap.get("choices").toString(), String.class);
                                answerInFormat.add(choices.get(chIndex));
                                break;
                            }
                        }
                    }
                }
                answersInFormat.add(answerInFormat);
            }
            map.put("answers", answersInFormat);
            map.put("success", true);

        } catch (LoginVerificationException | ObjectNotFoundException exc) {
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
