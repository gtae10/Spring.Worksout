package com.exercise.manager.service;

import com.exercise.manager.domain.Member;
import com.exercise.manager.domain.RoutineGroup;
import com.exercise.manager.repository.RoutineGroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class RoutineGroupService {

    private RoutineGroupRepository routineGroupRepository;

    public RoutineGroupService(RoutineGroupRepository routineGroupRepository) {
        this.routineGroupRepository = routineGroupRepository;
    }

    public void save(Member member,RoutineGroup routineGroup){
        routineGroup.setMember(member);
        routineGroupRepository.save(routineGroup);
    }
    public List<RoutineGroup> findAll(){
        return  routineGroupRepository.findAll();
    }
    public List<RoutineGroup> findByMember(Member member){
        return routineGroupRepository.findByMember(member);
    }
    public RoutineGroup findById(Long id){
        return routineGroupRepository.findById(id).get();
    }
    public void delete(Long id){
        routineGroupRepository.deleteById(id);
    }

}
