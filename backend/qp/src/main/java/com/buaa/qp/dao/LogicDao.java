package com.buaa.qp.dao;

import com.buaa.qp.entity.Logic;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;

@Mapper
public interface LogicDao {
    ArrayList<Logic> selectByTid(Integer templateId);

    Integer insert(Logic logic);

    void deleteByTid(Integer templateId);
}
