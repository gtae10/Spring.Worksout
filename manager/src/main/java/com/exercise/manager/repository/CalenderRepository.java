package com.exercise.manager.repository;

import com.exercise.manager.domain.Calender;
import com.exercise.manager.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface CalenderRepository extends JpaRepository<Calender,Long> {

    Calender findByMemberAndDate(Member member, LocalDate date);
    List<Calender> findByMember(Member member);
    boolean existsByMemberAndDate(Member member, LocalDate date);
}
