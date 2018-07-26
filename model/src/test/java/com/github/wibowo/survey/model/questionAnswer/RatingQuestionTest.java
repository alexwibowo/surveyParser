package com.github.wibowo.survey.model.questionAnswer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RatingQuestionTest {

    @Test
    void test_create() {
        final RatingQuestion question1 = new RatingQuestion(Theme.Work, "I like the kind of work I do.");
        final RatingQuestion question2 = new RatingQuestion(Theme.Place, "I feel empowered to get the work done for which I am responsible.");

        assertThat(question1.theme()).isEqualTo(Theme.Work);
        assertThat(question1.sentence()).isEqualTo("I like the kind of work I do.");
        assertThat(question2.theme()).isEqualTo(Theme.Place);
        assertThat(question2.sentence()).isEqualTo("I feel empowered to get the work done for which I am responsible.");
    }

    @Test
    void test_create_answer_with_correct_value() {
        final RatingAnswer answer = new RatingQuestion(Theme.Work, "I like the kind of work I do.")
                .createAnswerFrom("5");
        assertThat(answer.rating()).isEqualTo(5);
    }

    @Test
    void create_answer_with_non_integer_results_in_null_ratingAnswer() {
        assertTrue(new RatingQuestion(Theme.Work, "I like the kind of work I do.")
                .createAnswerFrom("abc").isNull());
    }

    @Test
    void create_answer_with_invalid_ratingValue_results_in_null_ratingAnswer() {
        assertTrue(new RatingQuestion(Theme.Work, "I like the kind of work I do.")
                .createAnswerFrom("6").isNull());

        assertTrue(new RatingQuestion(Theme.Work, "I like the kind of work I do.")
                .createAnswerFrom("-1").isNull());

        assertTrue(new RatingQuestion(Theme.Work, "I like the kind of work I do.")
                .createAnswerFrom("").isNull());
        assertTrue(new RatingQuestion(Theme.Work, "I like the kind of work I do.")
                .createAnswerFrom(" ").isNull());
        assertTrue(new RatingQuestion(Theme.Work, "I like the kind of work I do.")
                .createAnswerFrom(null).isNull());
    }

    @Test
    void toString_method() {
        assertThat(new RatingQuestion(Theme.Work, "I like the kind of work I do.").toString())
            .contains("RatingQuestion")
            .contains("theme=Work")
            .contains("sentence='I like the kind of work I do.'");
    }

    @Test
    void test_equality() {
        assertThat(new RatingQuestion(Theme.Work, "I like the kind of work I do."))
                .isEqualTo(new RatingQuestion(Theme.Work, "I like the kind of work I do."));
        assertThat(new RatingQuestion(Theme.Work, "I like the kind of work I do.").hashCode())
                .isEqualTo(new RatingQuestion(Theme.Work, "I like the kind of work I do.").hashCode());

        // different theme
        assertThat(new RatingQuestion(Theme.Work, "I like the kind of work I do."))
                .isNotEqualTo(new RatingQuestion(Theme.Place, "I like the kind of work I do."));
        assertThat(new RatingQuestion(Theme.Work, "I like the kind of work I do.").hashCode())
                .isNotEqualTo(new RatingQuestion(Theme.Place, "I like the kind of work I do.").hashCode());

        // different sentence
        assertThat(new RatingQuestion(Theme.Work, "I like the kind of work I do."))
                .isNotEqualTo(new RatingQuestion(Theme.Work, "Another question"));
        assertThat(new RatingQuestion(Theme.Work, "I like the kind of work I do.").hashCode())
                .isNotEqualTo(new RatingQuestion(Theme.Work, "Another question").hashCode());

    }



}