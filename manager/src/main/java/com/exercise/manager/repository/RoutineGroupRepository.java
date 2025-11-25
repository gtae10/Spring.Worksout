package com.exercise.manager.repository;

import com.exercise.manager.domain.Member;
import com.exercise.manager.domain.RoutineGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoutineGroupRepository extends JpaRepository<RoutineGroup,Long> {

    RoutineGroup save(RoutineGroup routineGroup);
    Optional<RoutineGroup> findById(Long id);
    List<RoutineGroup> findAll();
    List<RoutineGroup> findByMember(Member member);
    void deleteById(Long id);
}
