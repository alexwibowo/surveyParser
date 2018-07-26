package com.github.wibowo.survey.model.questionAnswer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SingleSelectQuestionTest {

    @Test
    void test_create() {
        final SingleSelectQuestion question1 = new SingleSelectQuestion(Theme.Work, "Manager");
        final SingleSelectQuestion question2 = new SingleSelectQuestion(Theme.Demographic, "City");

        assertThat(question1.theme()).isEqualTo(Theme.Work);
        assertThat(question1.sentence()).isEqualTo("Manager");
        assertThat(question2.theme()).isEqualTo(Theme.Demographic);
        assertThat(question2.sentence()).isEqualTo("City");
    }

    @Test
    void test_createAnswer_using_nonEmpty_value() {
        final SingleSelectAnswer answer = new SingleSelectQuestion(Theme.Work, "Manager")
                .createAnswerFrom("Bruce Lee");
        assertThat(answer.selection()).isEqualTo("Bruce Lee");
        assertFalse(answer.isNull());
    }

    @Test
    void test_createAnswer_with_invalid_answer_results_in_null_answer() {
        assertTrue(new SingleSelectQuestion(Theme.Work, "Manager")
                .createAnswerFrom(" ").isNull());
        assertTrue(new SingleSelectQuestion(Theme.Work, "Manager")
                .createAnswerFrom(null).isNull());
    }

    @Test
    void toString_method() {
        assertThat(new SingleSelectQuestion(Theme.Work, "Manager").toString())
                .contains("SingleSelectQuestion")
                .contains("theme=Work")
                .contains("sentence='Manager'");
    }

    @Test
    void test_equality() {
        assertThat(new SingleSelectQuestion(Theme.Work, "Manager"))
                .isEqualTo(new SingleSelectQuestion(Theme.Work, "Manager"));
        assertThat(new SingleSelectQuestion(Theme.Work, "Manager").hashCode())
                .isEqualTo(new SingleSelectQuestion(Theme.Work, "Manager").hashCode());

        // different theme
        assertThat(new SingleSelectQuestion(Theme.Work, "Manager"))
                .isNotEqualTo(new SingleSelectQuestion(Theme.Place, "Manager"));
        assertThat(new SingleSelectQuestion(Theme.Work, "Manager").hashCode())
                .isNotEqualTo(new SingleSelectQuestion(Theme.Place, "Manager").hashCode());

        // different sentence
        assertThat(new SingleSelectQuestion(Theme.Work, "Manager"))
                .isNotEqualTo(new SingleSelectQuestion(Theme.Work, "Office"));
        assertThat(new SingleSelectQuestion(Theme.Work, "Manager").hashCode())
                .isNotEqualTo(new SingleSelectQuestion(Theme.Work, "Office").hashCode());
    }

    @Test
    void is_not_equal_to_ratingQuestion() {
        assertThat(new SingleSelectQuestion(Theme.Work, "Manager"))
                .isNotEqualTo(new RatingQuestion(Theme.Work, "Manager"));
    }


}