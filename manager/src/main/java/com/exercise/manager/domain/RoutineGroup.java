package com.exercise.manager.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class RoutineGroup {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member  member;
    @OneToMany(mappedBy = "routineGroup", cascade = CascadeType.ALL, orphanRemoval= true)
    private List<Routine> routines = new ArrayList<>();

    public void addRoutine(Routine routine) {
        routines.add(routine);
        routine.setRoutineGroup(this);
    }
//GETTER SETTER
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public List<Routine> getRoutines() {
        return routines;
    }

    public void setRoutines(List<Routine> routines) {
        this.routines = routines;
    }

    // 화면 표시용: 이 그룹에 포함된 운동 부위를 중복 없이 콤마로 이어붙임 (예: "어깨, 삼두")
    public String getBodyParts() {
        return routines.stream()
                .map(Routine::getPart)
                .filter(p -> p != null && !p.isBlank())
                .distinct()
                .collect(Collectors.joining(", "));
    }

    // 화면 표시용: 이 그룹에 포함된 운동 부위를 중복 없이 리스트로 반환 (부위 태그 pill 렌더링용)
    public List<String> getDistinctParts() {
        return routines.stream()
                .map(Routine::getPart)
                .filter(p -> p != null && !p.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

}
