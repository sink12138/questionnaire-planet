package com.buaa.qp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.buaa.qp.entity.Account;
import com.buaa.qp.entity.Answer;
import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Template;
import com.buaa.qp.exception.ExtraMessageException;
import com.buaa.qp.exception.LoginVerificationException;
import com.buaa.qp.exception.ObjectNotFoundException;
import com.buaa.qp.exception.ParameterFormatException;
import com.buaa.qp.service.AccountService;
import com.buaa.qp.service.AnswerService;
import com.buaa.qp.service.TemplateService;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class DataController {
    @Autowired
    private AnswerService answerService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

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
            ArrayList<ArrayList<String>> answersInFormat = getData(answers, questions, template.getType().equals("exam"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("Europe/London"));
            ArrayList<Map<String, Object>> answerMaps = new ArrayList<>();
            for (int i = 0; i < answers.size(); i++) {
                Map<String, Object> answerMap = new HashMap<>();
                answerMap.put("answerId", answers.get(i).getAnswerId());
                answerMap.put("answerTime", answersInFormat.get(i + 1).get(questions.size() + 1));
                answerMap.put("username", answersInFormat.get(i + 1).get(questions.size() + 2));
                for (int j = 0; j < questions.size(); j ++) {
                    answerMap.put(answersInFormat.get(0).get(j + 1), answersInFormat.get(i + 1).get(j + 1));
                }
                if (template.getType().equals("exam"))
                    answerMap.put("points", answersInFormat.get(i + 1).get(questions.size() + 3));
                answerMaps.add(answerMap);
            }
            map.put("answers", answerMaps);

            ArrayList<String> stems = new ArrayList<>();
            int i = 1;
            for (Question q: questions) {
                stems.add(i + "." + q.getStem());
                i ++;
            }
            map.put("stems", stems);
            map.put("success", true);
        } catch (LoginVerificationException | ObjectNotFoundException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }

    @GetMapping("/excel")
    public void excel(@RequestParam(value = "templateId", required = false) String idStr) {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        File file = new File(System.currentTimeMillis() + ".xls");
        try {
            // Parameter checks
            int templateId;
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

            // Existence checks
            Template template = templateService.getTemplate(templateId);
            if (template == null)
                throw new ObjectNotFoundException();

            // Authority checks
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (!template.getOwner().equals(accountId))
                throw new LoginVerificationException();
            if (template.getDeleted())
                throw new ExtraMessageException("无法操作已删除的问卷");

            ArrayList<Answer> answers = answerService.getAnswersByTid(templateId);
            ArrayList<Question> questions = templateService.getQuestionsByTid(templateId);
            ArrayList<ArrayList<String>> formContent = getData(answers, questions, template.getType().equals("exam"));
            if (file.exists() && !file.delete())
                throw new ExtraMessageException("File already exists");
            if(!file.createNewFile())
                throw new ExtraMessageException("File already exists");
            WritableWorkbook workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("数据汇总", 0);
            for (int i = 0; i < formContent.get(0).size(); ++i) {
                sheet.addCell(new Label(i, 0, formContent.get(0).get(i)));
            }
            for (int i = 1; i < formContent.size(); ++i) {
                sheet.addCell(new Number(0, i, i));
                for (int j = 1; j < formContent.get(i).size(); ++j) {
                    sheet.addCell(new Label(j, i, formContent.get(i).get(j)));
                }
            }
            workbook.write();
            workbook.close();
            response.setContentType("application/force-download");
            response.addHeader(
                    "Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(
                            template.getTitle() + "_数据汇总.xls", "UTF-8"
                    ).replaceAll("\\+", "%20")
            );
            byte[] buffer = new byte[1024];
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        }
        catch (LoginVerificationException | ParameterFormatException |
                ObjectNotFoundException | ExtraMessageException exc) {
            System.out.println(exc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (bis != null) bis.close();
                if (fis != null) fis.close();
                if (file.exists() && !file.delete()) System.out.println("Failed to delete file");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/sum")
    public Map<String, Object> sum(@RequestParam("templateId") Integer templateId) {
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
            ArrayList<Answer> answers = answerService.getAnswersByTid(templateId);
            ArrayList<Question> questions = templateService.getQuestionsByTid(templateId);
            ArrayList<Map<String, Object>> results = new ArrayList<>();
            for (Question question : questions) {
                Map<String, Object> result = new HashMap<>();
                result.put("stem", question.getStem());
                result.put("type", question.getType());
                ArrayList<String> choicesInFormat = new ArrayList<>();
                ArrayList<Integer> counts = new ArrayList<>();
                if (!question.getType().equals("filling")) {
                    Map<String, Object> argsMap = JSON.parseObject(question.getArgs());
                    ArrayList<String> choices;
                    if (question.getType().equals("grade")) {
                        choices = (ArrayList<String>) JSON.parseArray(argsMap.get("grades").toString(), String.class);
                    } else {
                        choices = (ArrayList<String>) JSON.parseArray(argsMap.get("choices").toString(), String.class);
                    }
                    for (int j = 0; j < choices.size(); j++) {
                        if (question.getType().equals("choice") || question.getType().equals("multi-choice")) {
                            choicesInFormat.add((char) ((int) 'A' + j) + "." + choices.get(j));
                        } else if (question.getType().equals("dropdown") || question.getType().equals("vote") || question.getType().equals("sign-up")) {
                            choicesInFormat.add(choices.get(j));
                        } else {
                            choicesInFormat.add(choices.get(j) + "(" + (j + 1) + ")");
                        }
                        counts.add(0);
                    }
                }
                result.put("answers", choicesInFormat);
                result.put("counts", counts);
                if (question.getType().equals("grade")) {
                    result.put("avg", "0.0");
                }
                results.add(result);
            }
            Map<Integer, Integer> sumMap = new HashMap<>(); // for calculate avg of grade question
            ArrayList<ArrayList<String>> answersInFormat = getData(answers, questions, template.getType().equals("exam"));
            for (int t = 1; t < answersInFormat.size(); t++) {
                ArrayList<String> answerInFormat = answersInFormat.get(t);
                for (int i = 0; i < questions.size(); i++) {
                    switch (questions.get(i).getType()) {
                        case "vote": {
                        }
                        case "sign-up": {
                        }
                        case "multi-choice": {
                            if (!answerInFormat.get(i + 1).equals("")) {
                                String choiceStr = JSONArray.parseArray(answers.get(t - 1).getContent(), Object.class).get(i).toString();
                                ArrayList<Integer> chIndexes = (ArrayList<Integer>) JSON.parseArray(choiceStr, Integer.class);
                                assert chIndexes != null;
                                if (!chIndexes.isEmpty()) {
                                    for (Integer j : chIndexes) {
                                        @SuppressWarnings("unchecked")
                                        ArrayList<Integer> choiceCounts = (ArrayList<Integer>) results.get(i).get("counts");
                                        Integer number = choiceCounts.get(j);
                                        number += 1;
                                        choiceCounts.set(j, number);
                                    }
                                }
                            }
                            break;
                        }
                        case "choice":
                        case "dropdown": {
                            if (!answerInFormat.get(i + 1).equals("")) {
                                String chStr = answerInFormat.get(i + 1);
                                @SuppressWarnings("unchecked")
                                ArrayList<String> choiceContents = (ArrayList<String>) results.get(i).get("answers");
                                @SuppressWarnings("unchecked")
                                ArrayList<Integer> choiceCounts = (ArrayList<Integer>) results.get(i).get("counts");
                                int answerIndex = choiceContents.indexOf(chStr);
                                int number = choiceCounts.get(answerIndex);
                                number += 1;
                                choiceCounts.set(answerIndex, number);
                            }
                            break;
                        }
                        case "grade": {
                            int sumNum;
                            if (sumMap.containsKey(i)) {
                                sumNum = sumMap.get(i);
                                sumNum += 1;
                            } else {
                                sumNum = 1;
                            }
                            Integer chIndex = (Integer) JSONArray.parseArray(answers.get(t - 1).getContent(), Object.class).get(i);
                            sumMap.put(i, sumNum);
                            double originalAvg = Double.parseDouble((String) results.get(i).get("avg"));
                            double newAvg = ((originalAvg * (sumNum - 1)) + (chIndex + 1) ) / sumNum;
                            results.get(i).put("avg", String.format("%.1f", newAvg));
                            if (!answerInFormat.get(i + 1).equals("")) {
                                @SuppressWarnings("unchecked")
                                ArrayList<Integer> choiceCounts = (ArrayList<Integer>) results.get(i).get("counts");
                                Integer number = choiceCounts.get(chIndex);
                                number += 1;
                                choiceCounts.set(chIndex, number);
                            }
                            break;
                        }
                        case "filling": {
                            if (!answerInFormat.get(i + 1).equals("")) {
                                @SuppressWarnings("unchecked")
                                ArrayList<String> choiceContents = (ArrayList<String>) results.get(i).get("answers");
                                @SuppressWarnings("unchecked")
                                ArrayList<Integer> choiceCounts = (ArrayList<Integer>) results.get(i).get("counts");
                                if (choiceContents.contains(answerInFormat.get(i + 1))) {
                                    int answerIndex = choiceContents.indexOf(answerInFormat.get(i + 1));
                                    int number = choiceCounts.get(answerIndex);
                                    number += 1;
                                    choiceCounts.set(answerIndex, number);
                                } else {
                                    choiceContents.add(answerInFormat.get(i + 1));
                                    choiceCounts.add(1);
                                }
                            }
                            break;
                        }
                    }
                }
            }
            map.put("results", results);
            if (template.getType().equals("exam")) {
                String totalPoints = null;
                double sumDouble = 0.0;
                ArrayList<Integer> counts = new ArrayList<>();
                ArrayList<String> pointsStr = new ArrayList<>();
                for (int i = 1; i < answersInFormat.size(); i++) {
                    ArrayList<String> answerInFormat = answersInFormat.get(i);
                    String[] p_array = answerInFormat.get(questions.size() + 3).split("/");
                    if (totalPoints == null) {
                        totalPoints = p_array[1];
                    }
                    sumDouble += Double.parseDouble(p_array[0]);
                    if (pointsStr.contains(p_array[0])) {
                        int index = pointsStr.indexOf(p_array[0]);
                        int number = counts.get(index);
                        number ++;
                        counts.set(index, number);
                    } else {
                        pointsStr.add(p_array[0]);
                        counts.add(1);
                    }
                }
                map.put("totalPoints", totalPoints);
                map.put("allPoints", pointsStr);
                map.put("counts", counts);
                map.put("avgPoints", String.format("%.1f", sumDouble / answers.size()));
            }
            map.put("success", true);
        } catch (LoginVerificationException | ObjectNotFoundException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }

    @PostMapping("/clear")
    public Map<String, Object> clear(@RequestBody Map<String, Object> requestMap) {
        Map<String, Object> map = new HashMap<>();
        try {
            // check login
            Integer accountId = (Integer) request.getSession().getAttribute("accountId");
            if (accountId == null)
                throw new LoginVerificationException();
            Integer templateId;
            Integer answerId;
            try {
                templateId = (Integer) requestMap.get("templateId");
                answerId = (Integer) requestMap.get("answerId");
            }
            catch (ClassCastException cce) {
                throw new ParameterFormatException();
            }
            if (templateId != null && answerId != null) {
                throw new ParameterFormatException();
            }
            if (templateId != null) {
                if (templateId <= 0)
                    throw new ParameterFormatException();
                Template template = templateService.getTemplate(templateId);
                if (template == null || template.getDeleted()) {
                    throw new ObjectNotFoundException();
                } else if (!template.getOwner().equals(accountId)) {
                    throw new LoginVerificationException();
                }
                answerService.clearAllAnswers(templateId);
            } else if (answerId != null) {
                if (answerId <= 0)
                    throw new ParameterFormatException();
                Answer answer = answerService.getAnswerById(answerId);
                if (answer == null) {
                    throw new ObjectNotFoundException();
                }
                Template template = templateService.getTemplate(answer.getTemplateId());
                if (template == null || template.getDeleted()) {
                    throw new ObjectNotFoundException();
                } else if (!template.getOwner().equals(accountId)) {
                    throw new LoginVerificationException();
                }
                answerService.deleteById(answerId);
            } else {
                throw new ParameterFormatException();
            }
            map.put("success", true);
        } catch (LoginVerificationException | ObjectNotFoundException
                | ParameterFormatException exc) {
            map.put("success", false);
            map.put("message", exc.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
            map.put("success", false);
            map.put("message", "操作失败");
        }
        return map;
    }

    @Autowired
    AccountService accountService;

    private ArrayList<ArrayList<String>> getData(ArrayList<Answer> answers, ArrayList<Question> questions, Boolean isExam) {
        ArrayList<ArrayList<String>> answersInFormat = new ArrayList<>();
        ArrayList<String> firstRow = new ArrayList<>();
        firstRow.add("序号");
        int indexOfStem = 1;
        for (Question question : questions) {
            firstRow.add(indexOfStem + "." + question.getStem());
            indexOfStem += 1;
        }
        firstRow.add("提交时间");
        firstRow.add("用户名");
        if (isExam) {
            firstRow.add("得分情况");
        }
        answersInFormat.add(firstRow);
        for (Answer answer : answers) {
            ArrayList<String> answerInFormat = new ArrayList<>();
            answerInFormat.add(answer.getAnswerId().toString());
            List<Object> answerContents = JSONArray.parseArray(answer.getContent(), Object.class);
            for (int i = 0; i < questions.size(); i++) {
                Question current_question = questions.get(i);
                if (answerContents.get(i) == null) {
                    answerInFormat.add("");
                } else {
                    Map<String, Object> argsMap = JSON.parseObject(current_question.getArgs());
                    switch (current_question.getType()) {
                        case "choice": {
                            Integer chIndex = (Integer) answerContents.get(i);
                            if (chIndex < 0) {
                                answerInFormat.add("");
                            } else {
                                ArrayList<String> choices = (ArrayList<String>) JSON.parseArray(argsMap.get("choices").toString(), String.class);
                                String answerStr = (char) ((int) 'A' + chIndex) + "." + choices.get(chIndex);
                                answerInFormat.add(answerStr);
                            }
                            break;
                        }
                        case "vote":
                        case "sign-up":
                        case "multi-choice": {
                            ArrayList<String> choices = (ArrayList<String>) JSON.parseArray(argsMap.get("choices").toString(), String.class);
                            String choiceStr = answerContents.get(i).toString();
                            ArrayList<Integer> chIndexes = (ArrayList<Integer>) JSON.parseArray(choiceStr, Integer.class);
                            StringBuilder stringBuilder = new StringBuilder();
                            assert chIndexes != null;
                            if (chIndexes.isEmpty()) {
                                answerInFormat.add("");
                            } else {
                                for (Integer j : chIndexes) {
                                    if (current_question.getType().equals("multi-choice"))
                                        stringBuilder.append((char) ((int) 'A' + j)).append(".").append(choices.get(j));
                                    else
                                        stringBuilder.append(choices.get(j));
                                    if (chIndexes.indexOf(j) != chIndexes.size() - 1) {
                                        stringBuilder.append(";");
                                    }
                                }
                                String answerStr = stringBuilder.toString();
                                answerInFormat.add(answerStr);
                            }
                            break;
                        }
                        case "filling": {
                            String answerStr = (String) answerContents.get(i);
                            answerInFormat.add(answerStr);
                            break;
                        }
                        case "grade": {
                            Integer chIndex = (Integer) answerContents.get(i);
                            if (chIndex < 0) {
                                answerInFormat.add("");
                            } else {
                                ArrayList<String> choices = (ArrayList<String>) JSON.parseArray(argsMap.get("grades").toString(), String.class);
                                answerInFormat.add(choices.get(chIndex) + "(" + (chIndex + 1) + ")");
                            }
                            break;
                        }
                        case "dropdown": {
                            Integer chIndex = (Integer) answerContents.get(i);
                            if (chIndex < 0) {
                                answerInFormat.add("");
                            } else {
                                ArrayList<String> choices = (ArrayList<String>) JSON.parseArray(argsMap.get("choices").toString(), String.class);
                                answerInFormat.add(choices.get(chIndex));
                            }
                            break;
                        }
                    }
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            answerInFormat.add(sdf.format(new Date(answer.getSubmitTime().getTime() - 28800000)));
            Integer accountId = answer.getSubmitter();
            if (accountId != null) {
                Account account = accountService.getAccountById(accountId);
                answerInFormat.add(account.getUsername());
            } else {
                answerInFormat.add(null);
            }
            if (isExam) {
                answerInFormat.add(answer.getPoints());
            }
            answersInFormat.add(answerInFormat);
        }
        return answersInFormat;
    }
}
