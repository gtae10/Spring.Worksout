package com.exercise.manager.repository;

import com.exercise.manager.domain.Member;
import com.exercise.manager.domain.Routine;
import com.exercise.manager.domain.RoutineGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoutineRepository extends JpaRepository<Routine,Long> {

    Routine save(Routine routine);
    List<Routine> findByRoutineGroup(RoutineGroup group);
    void deleteById(Long id);
    List<Routine> findByMember(Member member);
    List<Routine> findByMemberOrderByIdDesc(Member member);

    // fetch join으로 routineSets까지 한 번에 가져와서 N+1 방지
    // (루틴상세 화면에서 운동마다 r.getVolume() 등을 호출할 때 운동 개수만큼
    //  세트 조회 쿼리가 따로 나가던 문제)
    @Query("SELECT DISTINCT r FROM Routine r LEFT JOIN FETCH r.routineSets WHERE r.routineGroup.id = :groupId")
    List<Routine> findByRoutineGroupId(@Param("groupId") Long groupId);
}
