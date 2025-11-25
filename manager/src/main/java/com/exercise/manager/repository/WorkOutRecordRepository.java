package com.exercise.manager.repository;

import com.exercise.manager.domain.Calender;
import com.exercise.manager.domain.WorkOutRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOutRecordRepository extends JpaRepository<WorkOutRecord,Long> {

    List<WorkOutRecord> findByCalender(Calender calender);
}
