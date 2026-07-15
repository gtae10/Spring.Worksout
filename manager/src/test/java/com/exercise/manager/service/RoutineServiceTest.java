package com.exercise.manager.service;

import com.exercise.manager.domain.Member;
import com.exercise.manager.domain.Routine;
import com.exercise.manager.dto.PrEntry;
import com.exercise.manager.repository.RoutineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoutineServiceTest {

    @Mock
    private RoutineRepository routineRepository;

    @InjectMocks
    private RoutineService routineService;

    private Routine routine(String workout, String part, int sets, int reps, double weight) {
        Routine r = new Routine();
        r.setWorkout(workout);
        r.setPart(part);
        r.setSets(sets);
        r.setReps(reps);
        r.setWeight(weight);
        return r;
    }

    @Test
    void 같은운동이면_반복횟수와_상관없이_최고무게가_PR이_된다() {
        Member member = new Member();
        Routine light = routine("벤치프레스", "가슴", 4, 15, 60); // 60kg x 15회
        Routine heavy = routine("벤치프레스", "가슴", 3, 5, 80);  // 80kg x 5회 (반복은 더 적지만 더 무거움)

        when(routineRepository.findByMember(any())).thenReturn(List.of(light, heavy));

        List<PrEntry> result = routineService.findPersonalRecords(member);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getWorkout()).isEqualTo("벤치프레스");
        assertThat(result.get(0).getWeight()).isEqualTo(80.0);
        assertThat(result.get(0).getReps()).isEqualTo(5);
    }

    @Test
    void 세트별_상세기록이_있으면_그중_최고무게를_PR로_잡는다() {
        Member member = new Member();
        Routine r = routine("스쿼트", "하체", 3, 10, 40); // 등록 당시 값(사용되면 안 됨)
        r.addSet(60.0, 12);
        r.addSet(80.0, 8);  // 이게 최고 무게여야 함
        r.addSet(70.0, 10);

        when(routineRepository.findByMember(any())).thenReturn(List.of(r));

        List<PrEntry> result = routineService.findPersonalRecords(member);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getWeight()).isEqualTo(80.0);
        assertThat(result.get(0).getReps()).isEqualTo(8);
    }

    @Test
    void 무게가_0이하인_기록은_무시된다() {
        Member member = new Member();
        Routine zero = routine("맨몸스쿼트", "하체", 3, 20, 0);

        when(routineRepository.findByMember(any())).thenReturn(List.of(zero));

        List<PrEntry> result = routineService.findPersonalRecords(member);

        assertThat(result).isEmpty();
    }

    @Test
    void 운동종류가_다르면_각각_따로_PR을_잡고_무게순으로_정렬한다() {
        Member member = new Member();
        Routine bench = routine("벤치프레스", "가슴", 3, 10, 60);
        Routine squat = routine("스쿼트", "하체", 3, 10, 100);
        Routine curl = routine("바벨컬", "팔", 3, 10, 30);

        when(routineRepository.findByMember(any())).thenReturn(List.of(bench, squat, curl));

        List<PrEntry> result = routineService.findPersonalRecords(member);

        assertThat(result).hasSize(3);
        // 무게 내림차순: 스쿼트(100) > 벤치프레스(60) > 바벨컬(30)
        assertThat(result.get(0).getWorkout()).isEqualTo("스쿼트");
        assertThat(result.get(1).getWorkout()).isEqualTo("벤치프레스");
        assertThat(result.get(2).getWorkout()).isEqualTo("바벨컬");
    }
}
