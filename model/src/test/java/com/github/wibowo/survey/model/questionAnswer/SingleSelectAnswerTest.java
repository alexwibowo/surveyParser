package com.github.wibowo.survey.model.questionAnswer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SingleSelectAnswerTest {

    @Test
    void must_be_constructed_with_singleSelectQuestion() {
        final NullPointerException exception = Assertions.assertThrows(NullPointerException.class,
                () -> SingleSelectAnswer.createAnswer(null, "Mr. Burns")
        );
        assertThat(exception.getMessage()).isEqualTo("Answer must be constructed with a question.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Sally", "John"})
    void construction_of_valid_answer(String answerValue) {
        final SingleSelectQuestion question = new SingleSelectQuestion(0, Theme.Place, "Who is your manager?");
        final SingleSelectAnswer answer =  SingleSelectAnswer.createAnswer(question, answerValue);

        assertThat(answer.question()).isSameAs(question);
        assertThat(answer.selection()).isSameAs(answerValue);
        assertFalse(answer.isNull());
    }

    @Test
    void null_answer() {
        final SingleSelectQuestion question = new SingleSelectQuestion(0, Theme.Place, "Who is your manager?");
        final SingleSelectAnswer answer =  SingleSelectAnswer.nullAnswer(question);
        assertTrue(answer.isNull());
    }
}