package com.exercise.manager.repository;

import com.exercise.manager.domain.Member;
import com.exercise.manager.domain.Routine;
import com.exercise.manager.domain.RoutineGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoutineRepository extends JpaRepository<Routine,Long> {

    Routine save(Routine routine);
    List<Routine> findByRoutineGroup(RoutineGroup group);
    void deleteById(Long id);
    List<Routine> findByMember(Member member);
    List<Routine> findByRoutineGroupId(Long groupId);
}
