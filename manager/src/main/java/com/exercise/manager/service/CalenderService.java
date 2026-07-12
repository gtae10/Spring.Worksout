package com.exercise.manager.service;

import com.exercise.manager.domain.Calender;
import com.exercise.manager.domain.Member;
import com.exercise.manager.repository.CalenderRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CalenderService {

    private final CalenderRepository calenderRepository;
    private final WorkOutRecordService workOutRecordService;
    public CalenderService (CalenderRepository calenderRepository, WorkOutRecordService workOutRecordService) {
        this.calenderRepository = calenderRepository;
        this.workOutRecordService = workOutRecordService;
    }

    public void save(Calender calender) {
        if (calenderRepository.existsByMemberAndDate(calender.getMember(), calender.getDate())) {
            return;
        }
        calenderRepository.save(calender);
    }
    public Calender findById(long id) {
        return calenderRepository.findById(id).get();
    }
    public Calender findByMemberAndDate(Member member, LocalDate date) {
        return  calenderRepository.findByMemberAndDate(member, date);
    }
    public List<Calender> findByMember(Member member) {
        return calenderRepository.findByMember(member);
    }

    public boolean existsByMemberAndDate(Member member, LocalDate date) {
        return calenderRepository.existsByMemberAndDate(member, date);
    }

    // 날짜에 배정된 루틴을 삭제 (그날 기록해둔 세트 기록도 함께 정리 - FK 제약 때문에 먼저 지워야 함)
    @Transactional
    public void deleteByMemberAndDate(Member member, LocalDate date) {
        Calender calender = calenderRepository.findByMemberAndDate(member, date);
        if (calender == null) return;
        workOutRecordService.deleteByCalender(calender);
        calenderRepository.delete(calender);
    }
}
