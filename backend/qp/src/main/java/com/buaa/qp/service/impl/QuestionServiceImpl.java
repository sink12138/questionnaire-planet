package com.buaa.qp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.buaa.qp.dao.QuestionDao;
import com.buaa.qp.dao.ShuffleDao;
import com.buaa.qp.entity.Question;
import com.buaa.qp.entity.Shuffle;
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
    public Integer shuffleQuestions(ArrayList<Question> questions, Integer shuffleId) {
        Shuffle shuffle;
        ArrayList<Integer> numbersList;
        Map<Integer, ArrayList<Integer>> choicesMap;
        ArrayList<Map<String, Object>> argMaps = new ArrayList<>();
        ClassParser parser = new ClassParser();
        if (shuffleId == null) {
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
                        choicesMap.put(i, shuffleChs);
                    }
                }
            }
            Collections.shuffle(shuffleNums);
            int index = 0;
            for (int i = 0; i < numbersList.size(); ++i) {
                if (numbersList.get(i) == null)
                    numbersList.set(i, shuffleNums.get(index++));
            }
            shuffle = new Shuffle(JSON.toJSONString(numbersList), JSON.toJSONString(choicesMap));
            shuffleDao.insert(shuffle);
            shuffleId = shuffle.getShuffleId();
        }
        else {
            shuffle = shuffleDao.selectById(shuffleId);
            numbersList = new ArrayList<>(JSON.parseArray(shuffle.getNumbers(), Integer.class));
            choicesMap = new HashMap<>();
            JSONObject choicesJSON = JSON.parseObject(shuffle.getChoices());
            for (String keyStr : choicesJSON.keySet()) {
                choicesMap.put(Integer.parseInt(keyStr), parser.toIntegerList(choicesJSON.get(keyStr)));
            }
            for (Question question : questions) {
                argMaps.add(JSON.parseObject(question.getArgs()));
            }
        }
        for (int index : choicesMap.keySet()) {
            ArrayList<String> choices = parser.toStringList(argMaps.get(index).get("choices"));
            ArrayList<String> shuffleChs = new ArrayList<>();
            for (int num : choicesMap.get(index)) {
                shuffleChs.add(choices.get(num));
            }
            argMaps.get(index).put("choices", shuffleChs);
            questions.get(index).setArgs(JSON.toJSONString(argMaps.get(index)));
        }
        ArrayList<Question> shuffledQues = new ArrayList<>();
        for (int index : numbersList) {
            shuffledQues.add(questions.get(index));
        }
        questions.clear();
        questions.addAll(shuffledQues);
        return shuffleId;
    }

    @Override
    public Integer shuffleQuestions(ArrayList<Question> questions, Integer accountId, Integer templateId) {
        Integer shuffleId = shuffleDao.selectIdByAccountTemplateId(accountId, templateId);
        return shuffleQuestions(questions, shuffleId);
    }

}
