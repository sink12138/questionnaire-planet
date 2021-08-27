package com.buaa.qp.dao;

import com.buaa.qp.entity.Shuffle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShuffleDao {
    Shuffle selectById(Integer shuffleId);

    Integer selectIdByAccountTemplateId(Integer accountId, Integer templateId);

    Integer insert(Shuffle shuffle);
}
