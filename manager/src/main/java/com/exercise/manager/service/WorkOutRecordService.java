package com.exercise.manager.service;

import com.exercise.manager.domain.Calender;
import com.exercise.manager.domain.WorkOutRecord;
import com.exercise.manager.repository.WorkOutRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkOutRecordService {

    private final WorkOutRecordRepository workOutRecordRepository;

    public WorkOutRecordService(WorkOutRecordRepository workOutRecordRepository) {
        this.workOutRecordRepository = workOutRecordRepository;
    }

    public WorkOutRecord save(WorkOutRecord workOutRecord) {
        return workOutRecordRepository.save(workOutRecord);
    }

    public List<WorkOutRecord> findByCalender(Calender calender) {
        return workOutRecordRepository.findByCalender(calender);
    }
}
