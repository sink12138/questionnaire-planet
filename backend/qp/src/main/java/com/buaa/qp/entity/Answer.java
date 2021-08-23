package com.buaa.qp.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer answerId;

    private Integer templateId;

    private String content;

    private Date submitTime;

    private String Ip;

    public Answer() {

    }

    public Answer(Integer templateId, String content) {
        this.templateId = templateId;
        this.content = content;
    }

    public Answer(Integer templateId, String content, String Ip) {
        this.templateId = templateId;
        this.content = content;
        this.Ip = Ip;
    }
}
