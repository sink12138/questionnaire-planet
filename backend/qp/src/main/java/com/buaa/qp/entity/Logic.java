package com.buaa.qp.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class Logic implements Comparable<Logic> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logicId;

    private Integer templateId;

    private Integer m;

    private Integer c;

    private Integer n;

    public Logic() {
    }

    @Override
    public int compareTo(Logic logic) {
        if (!this.m.equals(logic.m))
            return this.m > logic.m ? 1 : -1;
        if (!this.c.equals(logic.c))
            return this.c > logic.c ? 1 : -1;
        if (!this.n.equals(logic.n))
            return this.n > logic.n ? 1 : -1;
        return 0;
    }

    public ArrayList<Integer> toList() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(m);
        list.add(c);
        list.add(n);
        return list;
    }

    public Logic(List<Integer> logicTriplet) {
        this.m = logicTriplet.get(0);
        this.c = logicTriplet.get(1);
        this.n = logicTriplet.get(2);
    }

    public Logic(Integer m, Integer c, Integer n) {
        this.m = m;
        this.c = c;
        this.n = n;
    }

    public Logic(Integer logicId, Integer templateId, Integer m, Integer c, Integer n) {
        this.logicId = logicId;
        this.templateId = templateId;
        this.m = m;
        this.c = c;
        this.n = n;
    }

}
