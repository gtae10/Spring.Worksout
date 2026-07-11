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
}
