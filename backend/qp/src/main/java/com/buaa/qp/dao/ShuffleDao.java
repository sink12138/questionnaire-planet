package com.buaa.qp.dao;

import com.buaa.qp.entity.Shuffle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShuffleDao {
    Shuffle selectById(Integer shuffleId);

    Integer selectIdByAccountTemplateId(@Param("accountId") Integer accountId, @Param("templateId") Integer templateId);

    Integer insert(Shuffle shuffle);

    void deleteByTid(Integer templateId);

    void deleteAnsweredByTid(Integer templateId);

    void deleteByAccountTemplateId(@Param("accountId") Integer accountId, @Param("templateId") Integer templateId);
}
