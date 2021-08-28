package com.buaa.qp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.buaa.qp.dao.AnswerDao;
import com.buaa.qp.dao.ShuffleDao;
import com.buaa.qp.entity.Answer;
import com.buaa.qp.entity.Shuffle;
import com.buaa.qp.service.AnswerService;
import com.buaa.qp.util.ClassParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private ShuffleDao shuffleDao;

    @Override
    public ArrayList<Answer> getAnswersByTid(Integer templateId) {
        return answerDao.selectByTid(templateId);
    }

    @Override
    public Answer getAnswerById(Integer answerId) {
        return answerDao.selectById(answerId);
    }

    @Override
    public void clearAllAnswers(Integer templateId) {
        shuffleDao.deleteAnsweredByTid(templateId);
        answerDao.deleteByTid(templateId);
    }

    @Override
    public void deleteById(Integer answerId) {
        Answer answer = answerDao.selectById(answerId);
        if (answer.getSubmitter() != null)
            shuffleDao.deleteByAccountTemplateId(answer.getSubmitter(), answer.getTemplateId());
        answerDao.deleteById(answerId);
    }

    @Override
    public void submitAnswer(Answer answer) {
        answerDao.insert(answer);
    }

    @Override
    public int countAnswers(Integer templateId) {
        return answerDao.selectCountByTid(templateId);
    }

    @Override
    public Answer getOldAnswer(Integer templateId, Integer submitter) {
        Answer answer = new Answer();
        answer.setTemplateId(templateId);
        answer.setSubmitter(submitter);
        return answerDao.selectByTidAndSubmitter(answer);
    }

    @Override
    public ArrayList<Object> reorderAnswer(ArrayList<Object> answers, Integer shuffleId) {
        Shuffle shuffle = shuffleDao.selectById(shuffleId);
        ArrayList<Integer> numbersList = new ArrayList<>(JSON.parseArray(shuffle.getNumbers(), Integer.class));
        ArrayList<Object> reorderedAns = new ArrayList<>();
        Map<String, ArrayList<Integer>> choicesMap = new HashMap<>();
        JSONObject choicesJSON = JSON.parseObject(shuffle.getChoices());
        ClassParser parser = new ClassParser();
        for (int i = 0; i < numbersList.size(); ++i) {
            reorderedAns.add(null);
        }
        for (String key : choicesJSON.keySet()) {
            choicesMap.put(key, parser.toIntegerList(choicesJSON.get(key)));
        }
        for (int i = 0; i < numbersList.size(); ++i) {
            int index = numbersList.get(i);
            reorderedAns.set(index, answers.get(i));
        }
        for (String indexStr : choicesMap.keySet()) {
            int index = Integer.parseInt(indexStr);
            ArrayList<Integer> choicesList = choicesMap.get(indexStr);
            Object answerObject = reorderedAns.get(index);
            if (answerObject != null) {
                if (answerObject instanceof Integer) {
                    int choice = (int) answerObject;
                    if (choice >= 0)
                        reorderedAns.set(index, choicesList.get(choice));
                }
                else {
                    ArrayList<Integer> choices = parser.toIntegerList(answerObject);
                    for (int i = 0; i < choices.size(); ++i) {
                        int choice = choices.get(i);
                        choices.set(i, choicesList.get(choice));
                    }
                    Collections.sort(choices);
                    reorderedAns.set(index, choices);
                }
            }
        }
        return reorderedAns;
    }

    @Override
    public ArrayList<Object> reorderAnswer(ArrayList<Object> answers, Integer accountId, Integer templateId) {
        Integer shuffleId = shuffleDao.selectIdByAccountTemplateId(accountId, templateId);
        return reorderAnswer(answers, shuffleId);
    }
}
