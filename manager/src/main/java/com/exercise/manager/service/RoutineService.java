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
}
