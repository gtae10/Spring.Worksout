package com.exercise.manager.service;

import com.exercise.manager.domain.Member;
import com.exercise.manager.domain.WeightLog;
import com.exercise.manager.repository.WeightLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WeightLogService {

    private final WeightLogRepository weightLogRepository;

    public WeightLogService(WeightLogRepository weightLogRepository) {
        this.weightLogRepository = weightLogRepository;
    }

    // 오늘 날짜로 체중 기록 저장 (이미 오늘 기록이 있으면 덮어씀)
    public void logWeight(Member member, double weight) {
        LocalDate today = LocalDate.now();
        WeightLog log = weightLogRepository.findByMemberAndDate(member, today).orElseGet(WeightLog::new);
        log.setMember(member);
        log.setDate(today);
        log.setWeight(weight);
        weightLogRepository.save(log);
    }

    public List<WeightLog> findByMember(Member member) {
        return weightLogRepository.findByMemberOrderByDateAsc(member);
    }
}
