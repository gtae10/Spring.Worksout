package com.exercise.manager.service;


import com.exercise.manager.domain.Member;
import com.exercise.manager.domain.Routine;
import com.exercise.manager.repository.RoutineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoutineService {

    private final RoutineRepository routineRepository;
    public RoutineService(RoutineRepository routineRepository){
        this.routineRepository = routineRepository;
    }

    public void saveRoutine(Member member,Routine routine){
        routine.setMember(member);
        routineRepository.save(routine);
    }

    public Routine findById(Long id){
        return routineRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 루틴이 존재하지 않습니다. id=" + id)
        );
    }

    public List<Routine> findByMember(Member member){
        return routineRepository.findByMember(member);
    }

    public void deleteById(Long id){
        routineRepository.deleteById(id);
    }

    public List<Routine> findByRoutineGroup(Long groupId){
        return  routineRepository.findByRoutineGroupId(groupId);
    }

    public void updateRoutine(Long id, int sets, int reps, Double weight) {
        Routine r = routineRepository.findById(id).orElseThrow();
        r.setSets(sets);
        r.setReps(reps);
        r.setWeight(weight);
    }

    // 회원이 각 운동 종목(worksOutId)별로 가장 최근에 사용한 무게/반복을 담은 맵 반환
    // (운동 검색 화면에서 "지난번 무게" 기본값으로 채워주는 용도)
    public java.util.Map<Long, Routine> findLastUsedByWorksOut(Member member) {
        java.util.Map<Long, Routine> lastUsed = new java.util.HashMap<>();
        for (Routine r : routineRepository.findByMemberOrderByIdDesc(member)) {
            if (r.getWorksOut() == null) continue;
            Long worksOutId = r.getWorksOut().getId();
            lastUsed.putIfAbsent(worksOutId, r); // id desc로 정렬돼있어서 처음 등장하는 게 가장 최근 것
        }
        return lastUsed;
    }

    // 운동명별 개인 기록(PR) 계산: 반복 횟수와 상관없이 실제로 들어본 무게가 가장 높았던 기록을 PR로 잡음
    public List<com.exercise.manager.dto.PrEntry> findPersonalRecords(Member member) {
        java.util.Map<String, com.exercise.manager.dto.PrEntry> best = new java.util.LinkedHashMap<>();

        for (Routine r : routineRepository.findByMember(member)) {
            if (r.getWorkout() == null) continue;

            if (!r.getRoutineSets().isEmpty()) {
                for (var s : r.getRoutineSets()) {
                    updateBest(best, r.getWorkout(), r.getPart(), s.getWeight(), s.getReps());
                }
            } else {
                updateBest(best, r.getWorkout(), r.getPart(), r.getWeight(), r.getReps());
            }
        }

        List<com.exercise.manager.dto.PrEntry> result = new java.util.ArrayList<>(best.values());
        result.sort((a, b) -> Double.compare(b.getWeight(), a.getWeight()));
        return result;
    }

    private void updateBest(java.util.Map<String, com.exercise.manager.dto.PrEntry> best,
                            String workout, String part, double weight, int reps) {
        if (weight <= 0) return;
        com.exercise.manager.dto.PrEntry current = best.get(workout);
        // 무게가 더 무거우면 갱신 (반복 횟수는 그때 몇 회 했는지 참고용으로만 같이 저장)
        if (current == null || weight > current.getWeight()) {
            double est1rm = reps <= 1 ? weight : weight * (1 + reps / 30.0);
            best.put(workout, new com.exercise.manager.dto.PrEntry(workout, part, weight, reps, est1rm));
        }
    }
}
