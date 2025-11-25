package com.exercise.manager.repository;

import com.exercise.manager.domain.WorksOut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorksOutRepository extends JpaRepository<WorksOut, Long> {

    WorksOut save(WorksOut worksOut);
    Optional<WorksOut>  findById(Long id);
    List<WorksOut> findAll();
    List<WorksOut> findByPart(String part);
}
