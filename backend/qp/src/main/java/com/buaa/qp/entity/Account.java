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
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;

    private String email;

    private String username;

    private String password;

    private Boolean verified;

    public Account() {

    }

    public Account(String email, String username, String password, Boolean verified) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.verified = verified;
    }
}
