package com.exercise.manager.service;

import com.exercise.manager.domain.Calender;
import com.exercise.manager.domain.Member;
import com.exercise.manager.repository.CalenderRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CalenderService {

    private final CalenderRepository calenderRepository;
    public CalenderService (CalenderRepository calenderRepository) {
        this.calenderRepository = calenderRepository;
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
}
