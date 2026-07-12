package com.exercise.manager.domain;

import jakarta.persistence.*;

@Entity
public class RoutineSet {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    private int setOrder;   // 1세트, 2세트 ...
    private double weight;
    private int reps;
    private boolean completed;

    //Getter Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Routine getRoutine() {
        return routine;
    }
    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public int getSetOrder() {
        return setOrder;
    }
    public void setSetOrder(int setOrder) {
        this.setOrder = setOrder;
    }

    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }
    public void setReps(int reps) {
        this.reps = reps;
    }

    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
