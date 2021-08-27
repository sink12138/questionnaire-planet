package com.buaa.qp.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Setter
@Getter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer questionId;

    private Integer templateId;

    private String type;

    private String stem;

    private String description;

    private Boolean required;

    private String args;

    private String answer;

    private String points;

    private Boolean shuffle = false;

    public Question() {

    }

    public Question(String type, String stem, String description, Boolean required, String args) {
        this.type = type;
        this.stem = stem;
        this.description = description;
        this.required = required;
        this.args = args;
    }

    public Question(Integer templateId, String type, String stem, String description, Boolean required, String args) {
        this.templateId = templateId;
        this.type = type;
        this.stem = stem;
        this.description = description;
        this.required = required;
        this.args = args;
    }

    public Question(String type, String stem, String description, Boolean required, String args, String answer, String points, Boolean shuffle) {
        this.type = type;
        this.stem = stem;
        this.description = description;
        this.required = required;
        this.args = args;
        this.answer = answer;
        this.points = points;
        this.shuffle = shuffle;
    }

    public Question(Integer templateId, String type, String stem, String description, Boolean required, String args, String answer, String points, Boolean shuffle) {
        this.templateId = templateId;
        this.type = type;
        this.stem = stem;
        this.description = description;
        this.required = required;
        this.args = args;
        this.answer = answer;
        this.points = points;
        this.shuffle = shuffle;
    }

    public Question(Integer templateId, String type, String stem, String description, Boolean required, String args, String answer, Boolean shuffle) {
        this.templateId = templateId;
        this.type = type;
        this.stem = stem;
        this.description = description;
        this.required = required;
        this.args = args;
        this.answer = answer;
        this.shuffle = shuffle;
    }
}
