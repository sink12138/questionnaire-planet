package com.buaa.qp.dao;

import com.buaa.qp.entity.Check;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CheckDao {
    Check selectByCode(String code);

    Integer deleteById(Integer id);

    Integer insert(Check check);
}
