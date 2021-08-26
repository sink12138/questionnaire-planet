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

    private Integer submitter;

    /**
     * This attribute does not exist in the database!
     * Therefore, it shall not be used in any "insert" expressions.
     */
    private String submitterName;

    public Answer() {

    }

    public Answer(Integer templateId, String content) {
        this.templateId = templateId;
        this.content = content;
    }

    public Answer(Integer templateId, String content, Integer submitter) {
        this.templateId = templateId;
        this.content = content;
        this.submitter = submitter;
    }

    public Answer(Integer templateId, String content, Date submitTime, Integer submitter, String submitterName) {
        this.templateId = templateId;
        this.content = content;
        this.submitTime = submitTime;
        this.submitter = submitter;
        this.submitterName = submitterName;
    }

    public Answer(Integer answerId, Integer templateId, String content, Date submitTime, Integer submitter, String submitterName) {
        this.answerId = answerId;
        this.templateId = templateId;
        this.content = content;
        this.submitTime = submitTime;
        this.submitter = submitter;
        this.submitterName = submitterName;
    }
}
