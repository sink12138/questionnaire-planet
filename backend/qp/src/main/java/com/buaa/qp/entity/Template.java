package com.buaa.qp.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Setter
@Getter
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer templateId;

    private String type;

    private Integer owner;

    private String title;

    private String description;

    private String password;

    private Boolean released = false;

    private Boolean deleted = false;

    private Date creationTime;

    private Date releaseTime;

    private String duration;

    private String conclusion;

    private Integer quota;

    private Boolean showIndex;

    public Template() {

    }

    public Template(String type, Integer owner, String title, String description, String password, String conclusion) {
        this.type = type;
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.password = password;
        this.conclusion = conclusion;
    }

    public Template(String type, Integer owner, String title, String description, String password, String conclusion, Integer quota, Boolean showIndex) {
        this.type = type;
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.password = password;
        this.conclusion = conclusion;
        this.quota = quota;
        this.showIndex = showIndex;
    }

    public Template(Integer templateId, String type, Integer owner, String title, String description, String password, Boolean released, Boolean deleted, Date creationTime, Date releaseTime, String duration) {
        this.templateId = templateId;
        this.type = type;
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.password = password;
        this.released = released;
        this.deleted = deleted;
        this.creationTime = creationTime;
        this.releaseTime = releaseTime;
        this.duration = duration;
    }
}
