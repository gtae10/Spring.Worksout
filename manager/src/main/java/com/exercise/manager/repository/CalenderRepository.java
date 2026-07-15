package com.exercise.manager.repository;

import com.exercise.manager.domain.Calender;
import com.exercise.manager.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface CalenderRepository extends JpaRepository<Calender,Long> {

    Calender findByMemberAndDate(Member member, LocalDate date);
    boolean existsByMemberAndDate(Member member, LocalDate date);

    // fetch join으로 routineGroup + 그 안의 routines까지 한 번에 가져와서 N+1 방지
    // (통계 화면에서 캘린더 기록마다 c.getRoutineGroup().getRoutines()를 호출할 때
    //  기록 개수만큼 쿼리가 두 배로 나가던 문제)
    @Query("SELECT DISTINCT c FROM Calender c " +
            "LEFT JOIN FETCH c.routineGroup rg " +
            "LEFT JOIN FETCH rg.routines " +
            "WHERE c.member = :member")
    List<Calender> findByMember(@Param("member") Member member);
}
