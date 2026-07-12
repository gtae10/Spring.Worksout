package com.exercise.manager.domain;

import com.exercise.manager.repository.RoutineGroupRepository;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    // 운동 하나 안의 세트별 상세 기록 (세트마다 다른 무게/반복 관리)
    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineSet> routineSets = new ArrayList<>();

    public RoutineSet addSet(Double weight, Integer reps) {
        RoutineSet set = new RoutineSet();
        set.setRoutine(this);
        set.setWeight(weight != null ? weight : 0);
        set.setReps(reps != null ? reps : 0);
        set.setSetOrder(routineSets.size() + 1);
        routineSets.add(set);
        this.sets = routineSets.size();
        return set;
    }

    // 화면 표시용: 이 운동의 실제 세트 수 (세트별 상세 기록이 있으면 그 개수, 없으면 등록 당시 세트 수)
    public int getTotalSetsCount() {
        return !routineSets.isEmpty() ? routineSets.size() : sets;
    }

    // 화면 표시용: 이 운동의 총 반복 횟수 합 (세트별로 반복이 다를 수 있어서 각 세트의 반복을 다 더함)
    public int getTotalRepsCount() {
        if (!routineSets.isEmpty()) {
            return routineSets.stream().mapToInt(RoutineSet::getReps).sum();
        }
        return reps * sets;
    }

    // 화면 표시용: 이 운동의 총 볼륨(무게 x 반복의 합). 세트별 상세 기록이 있으면 그걸 우선 사용하고,
    // 아직 없으면 등록 당시의 세트/반복/무게 값으로 계산.
    public double getVolume() {
        if (!routineSets.isEmpty()) {
            return routineSets.stream()
                    .mapToDouble(s -> s.getWeight() * s.getReps())
                    .sum();
        }
        return weight * reps * sets;
    }

    // 화면 표시용: 이 운동의 예상 소모 칼로리 (체중 기반 대략치)
    // 저항운동 MET 6.0 가정 + 세트당 약 1분(운동+휴식) 가정 => kcal = 체중(kg) x 세트수 x 0.1
    public double getEstimatedCalories(double bodyWeightKg) {
        if (bodyWeightKg <= 0) return 0;
        return bodyWeightKg * getTotalSetsCount() * 0.1;
    }

    public List<RoutineSet> getRoutineSets() {
        return routineSets.stream()
                .sorted(Comparator.comparingInt(RoutineSet::getSetOrder))
                .toList();
    }

    public void setRoutineSets(List<RoutineSet> routineSets) {
        this.routineSets = routineSets;
    }

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

