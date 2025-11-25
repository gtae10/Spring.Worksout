package com.exercise.manager.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Calender {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    private RoutineGroup routineGroup;

    //Getter Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public RoutineGroup getRoutineGroup() {
        return routineGroup;
    }

    public void setRoutineGroup(RoutineGroup routineGroup) {
        this.routineGroup = routineGroup;
    }
}
