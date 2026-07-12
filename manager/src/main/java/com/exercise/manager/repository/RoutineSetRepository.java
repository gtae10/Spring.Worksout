package com.exercise.manager.repository;

import com.exercise.manager.domain.Routine;
import com.exercise.manager.domain.RoutineSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutineSetRepository extends JpaRepository<RoutineSet, Long> {
    List<RoutineSet> findByRoutineIdOrderBySetOrderAsc(Long routineId);
}
