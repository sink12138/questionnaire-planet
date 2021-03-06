package com.buaa.qp.dao;

import com.buaa.qp.entity.Answer;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface AnswerDao {
    void deleteByTid(Integer templateId);

    void deleteById(Integer answerId);

    void insert(Answer answer);

    Answer selectById(Integer answerId);

    ArrayList<Answer> selectByTid(Integer templateId);

    Integer selectCountByTid(Integer templateId);

    Answer selectByTidAndSubmitter(Answer answer);
}
