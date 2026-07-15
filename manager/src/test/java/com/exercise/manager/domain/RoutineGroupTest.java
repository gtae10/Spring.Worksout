package com.exercise.manager.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class RoutineGroupTest {

    private Routine newRoutine(String part, int sets, int reps, double weight) {
        Routine r = new Routine();
        r.setPart(part);
        r.setSets(sets);
        r.setReps(reps);
        r.setWeight(weight);
        return r;
    }

    @Test
    void 부위태그는_중복없이_등장순서대로_모은다() {
        RoutineGroup g = new RoutineGroup();
        g.addRoutine(newRoutine("어깨", 3, 10, 20));
        g.addRoutine(newRoutine("어깨", 3, 10, 20)); // 중복
        g.addRoutine(newRoutine("삼두", 3, 10, 10));
        g.addRoutine(newRoutine(null, 3, 10, 10));   // null은 제외
        g.addRoutine(newRoutine("  ", 3, 10, 10));   // 공백은 제외

        assertThat(g.getDistinctParts()).containsExactly("어깨", "삼두");
        assertThat(g.getBodyParts()).isEqualTo("어깨, 삼두");
    }

    @Test
    void 운동이_없으면_부위태그도_비어있다() {
        RoutineGroup g = new RoutineGroup();

        assertThat(g.getDistinctParts()).isEmpty();
        assertThat(g.getBodyParts()).isEmpty();
    }

    @Test
    void 총볼륨_세트_반복은_그룹안의_모든_운동을_합산한다() {
        RoutineGroup g = new RoutineGroup();
        g.addRoutine(newRoutine("가슴", 3, 10, 50)); // 볼륨 1500, 세트 3, 반복 30
        g.addRoutine(newRoutine("가슴", 4, 8, 60));  // 볼륨 1920, 세트 4, 반복 32

        assertThat(g.getTotalVolume()).isEqualTo(3420.0);
        assertThat(g.getTotalSets()).isEqualTo(7);
        assertThat(g.getTotalReps()).isEqualTo(62);
    }

    @Test
    void 예상칼로리는_그룹안의_모든_운동의_칼로리를_합산한다() {
        RoutineGroup g = new RoutineGroup();
        g.addRoutine(newRoutine("가슴", 3, 10, 50)); // 3세트
        g.addRoutine(newRoutine("등", 4, 8, 60));    // 4세트

        // 70kg 기준: (70*3*0.1) + (70*4*0.1) = 21 + 28 = 49
        assertThat(g.getEstimatedCalories(70)).isCloseTo(49.0, offset(0.0001));
    }
}
