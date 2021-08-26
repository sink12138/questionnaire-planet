package com.buaa.qp.dao;

import com.buaa.qp.entity.Template;
import org.apache.ibatis.annotations.Mapper;
import java.util.ArrayList;

@Mapper
public interface TemplateDao {
    Template selectById(Integer templateId);

    ArrayList<Template> selectByOwner(Integer owner);

    Integer selectQuotaById(Integer templateId);

    Integer insert(Template template);

    Integer update(Template template);

    void updateReleased(Template template);

    void updateDeleted(Template template);

    int deleteByTid(Integer templateId);

    String selectDuration(Integer templateId);

    Template selectByCode(String code);

    void updateCode(Template template);
}
