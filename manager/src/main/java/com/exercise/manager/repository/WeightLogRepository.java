package com.exercise.manager.repository;

import com.exercise.manager.domain.Member;
import com.exercise.manager.domain.WeightLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeightLogRepository extends JpaRepository<WeightLog, Long> {
    List<WeightLog> findByMemberOrderByDateAsc(Member member);
    Optional<WeightLog> findByMemberAndDate(Member member, LocalDate date);
}
