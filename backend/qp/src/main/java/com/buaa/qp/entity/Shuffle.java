package com.buaa.qp.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Shuffle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer shuffleId;

    private Integer accountId;

    private Integer templateId;

    private String numbers;

    private String choices;

    public Shuffle() {

    }

    public Shuffle(String numbers, String choices) {
        this.numbers = numbers;
        this.choices = choices;
    }

    public Shuffle(Integer accountId, Integer templateId, String numbers, String choices) {
        this.accountId = accountId;
        this.templateId = templateId;
        this.numbers = numbers;
        this.choices = choices;
    }
}
