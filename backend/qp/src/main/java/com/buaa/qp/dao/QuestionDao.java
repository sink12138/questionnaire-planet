package com.buaa.qp.dao;

import com.buaa.qp.entity.Question;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface QuestionDao {
    ArrayList<Question> selectByTid(Integer templateId);

    Integer insert(Question question);

    void deleteByTid(Integer templateId);

    void updateRemains(Question question);
}
