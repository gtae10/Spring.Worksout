package com.exercise.manager.repository;

import com.exercise.manager.domain.Member;
import com.exercise.manager.domain.RoutineGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoutineGroupRepository extends JpaRepository<RoutineGroup,Long> {

    RoutineGroup save(RoutineGroup routineGroup);
    Optional<RoutineGroup> findById(Long id);
    List<RoutineGroup> findAll();
    void deleteById(Long id);

    // fetch join으로 routines를 한 번에 같이 가져와서 N+1 방지
    // (홈 화면/루틴목록 화면에서 그룹마다 group.getRoutines()를 호출할 때 그룹 개수만큼 쿼리가 따로 나가던 문제)
    @Query("SELECT DISTINCT rg FROM RoutineGroup rg LEFT JOIN FETCH rg.routines WHERE rg.member = :member")
    List<RoutineGroup> findByMember(@Param("member") Member member);
}
