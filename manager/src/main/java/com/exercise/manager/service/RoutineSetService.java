package com.exercise.manager.service;

import com.exercise.manager.domain.Routine;
import com.exercise.manager.domain.RoutineSet;
import com.exercise.manager.repository.RoutineRepository;
import com.exercise.manager.repository.RoutineSetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoutineSetService {

    private final RoutineSetRepository routineSetRepository;
    private final RoutineRepository routineRepository;

    public RoutineSetService(RoutineSetRepository routineSetRepository, RoutineRepository routineRepository) {
        this.routineSetRepository = routineSetRepository;
        this.routineRepository = routineRepository;
    }

    public List<RoutineSet> findByRoutine(Long routineId) {
        return routineSetRepository.findByRoutineIdOrderBySetOrderAsc(routineId);
    }

    @Transactional
    public void addSet(Long routineId, Double weight, Integer reps) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 루틴이 존재하지 않습니다. id=" + routineId));

        // 무게/반복을 안 넘겼으면 마지막 세트 값을 그대로 이어받음 (없으면 0)
        if (weight == null || reps == null) {
            List<RoutineSet> existing = routineSetRepository.findByRoutineIdOrderBySetOrderAsc(routineId);
            if (!existing.isEmpty()) {
                RoutineSet last = existing.get(existing.size() - 1);
                if (weight == null) weight = last.getWeight();
                if (reps == null) reps = last.getReps();
            }
        }

        routine.addSet(weight, reps);
        // routine.sets는 addSet 내부에서 세트 개수로 자동 갱신됨 (영속 상태라 flush 시 반영)
    }

    @Transactional
    public void updateSet(Long setId, double weight, int reps) {
        RoutineSet set = routineSetRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("해당 세트가 존재하지 않습니다. id=" + setId));
        set.setWeight(weight);
        set.setReps(reps);
    }

    @Transactional
    public void toggleCompleted(Long setId) {
        RoutineSet set = routineSetRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("해당 세트가 존재하지 않습니다. id=" + setId));
        set.setCompleted(!set.isCompleted());
    }

    @Transactional
    public void deleteSet(Long setId) {
        RoutineSet set = routineSetRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("해당 세트가 존재하지 않습니다. id=" + setId));
        Routine routine = set.getRoutine();
        routineSetRepository.deleteById(setId);

        // 남은 세트들의 순번을 1부터 다시 매기고, Routine.sets 개수도 갱신
        List<RoutineSet> remaining = routineSetRepository.findByRoutineIdOrderBySetOrderAsc(routine.getId());
        int order = 1;
        for (RoutineSet s : remaining) {
            s.setSetOrder(order++);
        }
        routine.setSets(remaining.size());
    }
}
