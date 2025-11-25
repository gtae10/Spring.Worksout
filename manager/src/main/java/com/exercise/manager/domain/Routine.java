package com.exercise.manager.domain;

import com.exercise.manager.repository.RoutineGroupRepository;
import jakarta.persistence.*;

@Entity
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // Exercise의 id(FK)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worksout_id") // Exercise의 id(FK)
    private WorksOut worksOut;
    private String workout;
    private int sets;
    private int reps;
    private double weight;
    private String part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_group_id")
    private RoutineGroup routineGroup;


    //Getter Setter
    public Member getMember() {
        return member;
    }
    public void setMember(Member member) {
        this.member = member;
    }

    public WorksOut getWorksOut() {
        return worksOut;
    }
    public void setWorksOut(WorksOut worksOut) {
        this.worksOut = worksOut;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkout() {
        return workout;
    }
    public void setWorkout(String workout) {
        this.workout = workout;
    }

    public int getSets() {
        return sets;
    }
    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }
    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getPart() {
        return part;
    }
    public void setPart(String part) {
        this.part = part;
    }

    public RoutineGroup getRoutineGroup() {
        return routineGroup;
    }

    public void setRoutineGroup(RoutineGroup routineGroup) {
        this.routineGroup = routineGroup;
    }

}
