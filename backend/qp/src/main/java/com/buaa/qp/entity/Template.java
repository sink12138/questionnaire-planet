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
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer templateId;

    private Integer owner;

    private String title;

    private String description;

    private String password;

    public Template() {

    }

    public Template(String title, String description, String password, Integer owner) {
        this.title = title;
        this.description = description;
        this.password = password;
        this.owner = owner;
    }

    public Template(Integer templateId, String title, String description, String password, Integer owner) {
        this.templateId = templateId;
        this.title = title;
        this.description = description;
        this.password = password;
        this.owner = owner;
    }
}
