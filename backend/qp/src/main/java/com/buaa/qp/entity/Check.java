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
public class Check {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer checkId;

    private Integer accountId;

    private String code;

    private Date checkingTime;
}
