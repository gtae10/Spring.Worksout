package com.exercise.manager.service;

import com.exercise.manager.domain.Routine;
import com.exercise.manager.domain.RoutineSet;
import com.exercise.manager.repository.RoutineRepository;
import com.exercise.manager.repository.RoutineSetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoutineSetServiceTest {

    @Mock
    private RoutineSetRepository routineSetRepository;

    @Mock
    private RoutineRepository routineRepository;

    @InjectMocks
    private RoutineSetService routineSetService;

    private RoutineSet setOf(double weight, int reps, int order) {
        RoutineSet s = new RoutineSet();
        s.setWeight(weight);
        s.setReps(reps);
        s.setSetOrder(order);
        return s;
    }

    @Test
    void 무게와_반복을_직접_넘기면_그값을_그대로_사용한다() {
        Routine routine = new Routine();
        when(routineRepository.findById(1L)).thenReturn(Optional.of(routine));

        routineSetService.addSet(1L, 55.0, 12);

        assertThat(routine.getRoutineSets()).hasSize(1);
        assertThat(routine.getRoutineSets().get(0).getWeight()).isEqualTo(55.0);
        assertThat(routine.getRoutineSets().get(0).getReps()).isEqualTo(12);
    }

    @Test
    void 무게와_반복을_안넘기면_직전세트값을_그대로_이어받는다() {
        Routine routine = new Routine();
        when(routineRepository.findById(1L)).thenReturn(Optional.of(routine));
        when(routineSetRepository.findByRoutineIdOrderBySetOrderAsc(1L))
                .thenReturn(List.of(setOf(40.0, 15, 1), setOf(60.0, 10, 2))); // 마지막 세트: 60kg x 10회

        routineSetService.addSet(1L, null, null);

        assertThat(routine.getRoutineSets()).hasSize(1);
        assertThat(routine.getRoutineSets().get(0).getWeight()).isEqualTo(60.0);
        assertThat(routine.getRoutineSets().get(0).getReps()).isEqualTo(10);
    }

    @Test
    void 이전세트가_하나도_없는데_값도_안넘기면_0으로_추가된다() {
        Routine routine = new Routine();
        when(routineRepository.findById(1L)).thenReturn(Optional.of(routine));
        when(routineSetRepository.findByRoutineIdOrderBySetOrderAsc(1L)).thenReturn(List.of());

        routineSetService.addSet(1L, null, null);

        assertThat(routine.getRoutineSets()).hasSize(1);
        assertThat(routine.getRoutineSets().get(0).getWeight()).isEqualTo(0.0);
        assertThat(routine.getRoutineSets().get(0).getReps()).isEqualTo(0);
    }

    @Test
    void 무게만_없고_반복은_넘기면_무게만_이전값을_이어받는다() {
        Routine routine = new Routine();
        when(routineRepository.findById(1L)).thenReturn(Optional.of(routine));
        when(routineSetRepository.findByRoutineIdOrderBySetOrderAsc(1L))
                .thenReturn(List.of(setOf(70.0, 8, 1)));

        routineSetService.addSet(1L, null, 20); // 반복은 20으로 직접 지정, 무게는 이어받아야 함

        RoutineSet added = routine.getRoutineSets().get(0);
        assertThat(added.getWeight()).isEqualTo(70.0); // 이어받음
        assertThat(added.getReps()).isEqualTo(20);     // 직접 넘긴 값 그대로
    }

    @Test
    void 존재하지_않는_루틴이면_예외를_던진다() {
        when(routineRepository.findById(anyLong())).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> routineSetService.addSet(999L, 10.0, 5)
        );
    }
}
