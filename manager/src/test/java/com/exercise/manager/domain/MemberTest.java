package com.exercise.manager.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

class MemberTest {

    @Test
    void 체중과_키가_모두_있으면_BMI를_계산한다() {
        Member m = new Member();
        m.setWeight(70.0);
        m.setHeight(175.0);

        // BMI = 70 / (1.75 * 1.75) = 약 22.86
        assertThat(m.getBmi()).isCloseTo(22.857, offset(0.01));
    }

    @Test
    void 체중이_없으면_BMI는_null이다() {
        Member m = new Member();
        m.setHeight(175.0);

        assertThat(m.getBmi()).isNull();
    }

    @Test
    void 키가_없으면_BMI는_null이다() {
        Member m = new Member();
        m.setWeight(70.0);

        assertThat(m.getBmi()).isNull();
    }

    @Test
    void 키가_0이하이면_BMI는_null이다() {
        Member m = new Member();
        m.setWeight(70.0);
        m.setHeight(0.0);

        assertThat(m.getBmi()).isNull();
    }
}
