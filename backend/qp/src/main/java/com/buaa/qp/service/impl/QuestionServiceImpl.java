package com.buaa.qp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.buaa.qp.dao.QuestionDao;
import com.buaa.qp.dao.ShuffleDao;
import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Shuffle;
import com.buaa.qp.exception.ParameterFormatException;
import com.buaa.qp.service.QuestionService;
import com.buaa.qp.util.ClassParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private ShuffleDao shuffleDao;

    @Override
    public void updateRemains(Question question) {
        questionDao.updateRemains(question);
    }

    @Override
    public Integer shuffleQuestions(ArrayList<Question> questions, Integer accountId, Integer templateId) throws ParameterFormatException {
        ArrayList<Integer> numbersList;
        Map<String, ArrayList<Integer>> choicesMap;
        ArrayList<Map<String, Object>> argMaps = new ArrayList<>();
        ClassParser parser = new ClassParser();
        numbersList = new ArrayList<>();
        choicesMap = new HashMap<>();
        for (Question question : questions) {
            numbersList.add(null);
            argMaps.add(JSON.parseObject(question.getArgs()));
        }
        ArrayList<Integer> shuffleNums = new ArrayList<>();
        for (int i = 0; i < questions.size(); ++i) {
            Question question = questions.get(i);
            if (!question.getShuffle()) {
                numbersList.set(i, i);
            }
            else {
                shuffleNums.add(i);
                Map<String, Object> args = argMaps.get(i);
                if (args.containsKey("choices")) {
                    ArrayList<Integer> shuffleChs = new ArrayList<>();
                    for (int j = 0; j < parser.toStringList(args.get("choices")).size(); ++j) {
                        shuffleChs.add(j);
                    }
                    Collections.shuffle(shuffleChs);
                    choicesMap.put(String.valueOf(i), shuffleChs);
                }
            }
        }
        Collections.shuffle(shuffleNums);
        int index = 0;
        for (int i = 0; i < numbersList.size(); ++i) {
            if (numbersList.get(i) == null)
                numbersList.set(i, shuffleNums.get(index++));
        }
        Shuffle shuffle = new Shuffle(accountId, templateId, JSON.toJSONString(numbersList), JSON.toJSONString(choicesMap));
        shuffleDao.insert(shuffle);
        Integer shuffleId = shuffle.getShuffleId();
        makeShuffledQuestions(questions, shuffleId);
        return shuffleId;
    }

    @Override
    public void makeShuffledQuestions(ArrayList<Question> questions, Integer shuffleId) throws ParameterFormatException {
        Shuffle shuffle = shuffleDao.selectById(shuffleId);
        ArrayList<Integer> numbersList;
        Map<String, ArrayList<Integer>> choicesMap;
        ArrayList<Map<String, Object>> argMaps = new ArrayList<>();
        ClassParser parser = new ClassParser();
        numbersList = new ArrayList<>(JSON.parseArray(shuffle.getNumbers(), Integer.class));
        choicesMap = new HashMap<>();
        JSONObject choicesJSON = JSON.parseObject(shuffle.getChoices());
        for (String key : choicesJSON.keySet()) {
            choicesMap.put(key, parser.toIntegerList(choicesJSON.get(key)));
        }
        for (Question question : questions) {
            argMaps.add(JSON.parseObject(question.getArgs()));
        }
        if (numbersList.size() != questions.size())
            throw new ParameterFormatException();
        for (String indexStr : choicesMap.keySet()) {
            int index = Integer.parseInt(indexStr);
            Question question = questions.get(index);
            ArrayList<Integer> choicesList = choicesMap.get(indexStr);
            ArrayList<String> choices = parser.toStringList(argMaps.get(index).get("choices"));
            if (choicesList == null || choices == null || choicesList.size() != choices.size())
                throw new ParameterFormatException();
            ArrayList<String> shuffleChs = new ArrayList<>();
            for (int num : choicesList) {
                shuffleChs.add(choices.get(num));
            }
            argMaps.get(index).put("choices", shuffleChs);
            question.setArgs(JSON.toJSONString(argMaps.get(index)));
            if (question.getAnswer() != null) {
                if (question.getType().equals("choice")) {
                    Integer correctAnswer = Integer.parseInt(question.getAnswer());
                    question.setAnswer(Integer.toString(choicesList.indexOf(correctAnswer)));
                }
                else {
                    ArrayList<Integer> correctAnswers = new ArrayList<>(JSON.parseArray(question.getAnswer(), Integer.class));
                    for (int i = 0; i < correctAnswers.size(); ++i) {
                        correctAnswers.set(i, choicesList.indexOf(correctAnswers.get(i)));
                    }
                    Collections.sort(correctAnswers);
                    question.setAnswer(JSON.toJSONString(correctAnswers));
                }
            }
        }
        ArrayList<Question> shuffledQues = new ArrayList<>();
        for (int index : numbersList) {
            shuffledQues.add(questions.get(index));
        }
        questions.clear();
        questions.addAll(shuffledQues);
    }

    @Override
    public void makeShuffledQuestions(ArrayList<Question> questions, Integer accountId, Integer templateId) throws ParameterFormatException {
        Integer shuffleId = shuffleDao.selectIdByAccountTemplateId(accountId, templateId);
        makeShuffledQuestions(questions, shuffleId);
    }

}
