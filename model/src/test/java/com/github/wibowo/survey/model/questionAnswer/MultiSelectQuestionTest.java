package com.github.wibowo.survey.model.questionAnswer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MultiSelectQuestionTest {

    private MultiSelectQuestion places;

    @BeforeEach
    void setUp() {
        places = new MultiSelectQuestion(Theme.Work, "Places");
    }

    @Test
    void test_create() {
        assertThat(places.theme()).isEqualTo(Theme.Work);
        assertThat(places.sentence()).isEqualTo("Places");
    }

    @Test
    void test_create_answer_with_one_selection() {
        final MultiSelectAnswer answer = places.createAnswerFrom("Melbourne");
        assertThat(answer.isNull()).isFalse();
        assertThat(answer.getSelection()).containsExactly("Melbourne");
    }


    @Test
    void test_create_answer_with_multi_selection() {
        final MultiSelectAnswer answer = places.createAnswerFrom("Sydney|Melbourne");
        assertThat(answer.isNull()).isFalse();
        assertThat(answer.getSelection()).containsExactlyInAnyOrder("Melbourne", "Sydney");
    }

    @Test
    void test_create_null_answer() {
        final MultiSelectAnswer answer = places.nullAnswer();
        assertThat(answer.isNull()).isTrue();
    }


}