package com.buaa.qp.dao;

import com.buaa.qp.entity.Template;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TemplateDao {
    Integer insert(Template template);

    Integer update(Template template);
}
