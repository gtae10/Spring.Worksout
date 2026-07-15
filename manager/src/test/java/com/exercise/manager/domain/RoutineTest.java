package com.exercise.manager.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class RoutineTest {

    private Routine newRoutine(int sets, int reps, double weight) {
        Routine r = new Routine();
        r.setSets(sets);
        r.setReps(reps);
        r.setWeight(weight);
        return r;
    }

    @Test
    void 세트별_상세기록이_없으면_등록당시_값으로_볼륨을_계산한다() {
        // 3세트 x 10회 x 50kg
        Routine r = newRoutine(3, 10, 50);

        assertThat(r.getVolume()).isEqualTo(1500.0);
        assertThat(r.getTotalSetsCount()).isEqualTo(3);
        assertThat(r.getTotalRepsCount()).isEqualTo(30);
    }

    @Test
    void 세트별_상세기록이_있으면_그값을_우선사용해서_볼륨을_계산한다() {
        Routine r = newRoutine(3, 10, 50); // 등록 당시 값 (사용되면 안 됨)

        r.addSet(40.0, 15); // 40 x 15 = 600
        r.addSet(50.0, 12); // 50 x 12 = 600
        r.addSet(60.0, 8);  // 60 x 8 = 480

        assertThat(r.getVolume()).isEqualTo(1680.0); // 600 + 600 + 480
        assertThat(r.getTotalSetsCount()).isEqualTo(3);
        assertThat(r.getTotalRepsCount()).isEqualTo(35); // 15 + 12 + 8
    }

    @Test
    void addSet은_세트번호를_순서대로_매기고_sets필드를_자동갱신한다() {
        Routine r = newRoutine(0, 0, 0);

        RoutineSet s1 = r.addSet(40.0, 12);
        RoutineSet s2 = r.addSet(45.0, 10);

        assertThat(s1.getSetOrder()).isEqualTo(1);
        assertThat(s2.getSetOrder()).isEqualTo(2);
        assertThat(r.getSets()).isEqualTo(2); // addSet 호출할 때마다 sets 필드도 같이 갱신됨
        assertThat(r.getRoutineSets()).hasSize(2);
    }

    @Test
    void addSet에_무게나_반복을_안넘기면_0으로_기본값이_들어간다() {
        Routine r = newRoutine(0, 0, 0);

        RoutineSet s = r.addSet(null, null);

        assertThat(s.getWeight()).isEqualTo(0.0);
        assertThat(s.getReps()).isEqualTo(0);
    }

    @Test
    void 체중이_0이하면_예상칼로리는_0이다() {
        Routine r = newRoutine(4, 10, 50);

        assertThat(r.getEstimatedCalories(0)).isEqualTo(0);
        assertThat(r.getEstimatedCalories(-10)).isEqualTo(0);
    }

    @Test
    void 예상칼로리는_체중_x_세트수_x_0점1이다() {
        Routine r = newRoutine(5, 10, 50); // 5세트

        // 70kg 체중 기준: 70 * 5 * 0.1 = 35
        assertThat(r.getEstimatedCalories(70)).isCloseTo(35.0, offset(0.0001));
    }
}
