package com.github.wibowo.survey.model.questionAnswer;

import com.github.wibowo.survey.model.SurveyException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionTypeTest {

    @Test
    void parse_question_type() {
        assertThat(QuestionType.questionTypeFor("ratingquestion"))
                .isEqualTo(QuestionType.RatingQuestion);
        assertThat(QuestionType.questionTypeFor("singleselect"))
                .isEqualTo(QuestionType.SingleSelectQuestion);
        assertThat(QuestionType.questionTypeFor("multiselect"))
                .isEqualTo(QuestionType.MultiSelectQuestion);
    }


    @Test
    void creating_question() {
        final Question singleSelectQuestion = QuestionType.createFrom(Theme.Work, "singleselect", "Manager");
        assertThat(singleSelectQuestion).isInstanceOf(SingleSelectQuestion.class)
                .isEqualTo(new SingleSelectQuestion(Theme.Work, "Manager"));

        final Question multiSelectQuestion = QuestionType.createFrom(Theme.Work, "multiselect", "Place");
        assertThat(multiSelectQuestion).isInstanceOf(MultiSelectQuestion.class)
                .isEqualTo(new MultiSelectQuestion(Theme.Work, "Place"));

        final Question ratingQuestion = QuestionType.createFrom(Theme.Work, "ratingquestion", "How happy are you");
        assertThat(ratingQuestion).isInstanceOf(RatingQuestion.class)
                .isEqualTo(new RatingQuestion(Theme.Work, "How happy are you"));

    }

    @Test
    void parsing_is_case_insensitive() {
        assertThat(QuestionType.questionTypeFor("ratingquestion"))
                .isEqualTo(QuestionType.questionTypeFor("ratiNGquestion"))
                .isEqualTo(QuestionType.questionTypeFor("ratingQuestion"))
                .isEqualTo(QuestionType.RatingQuestion);
    }

    @Test
    void trim_before_matching() {
        assertThat(QuestionType.questionTypeFor("  ratingquestion  "))
                .isEqualTo(QuestionType.RatingQuestion);
    }

    @Test
    void fail_when_given_null() {
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
                () -> QuestionType.questionTypeFor(null)
        );
    }

    @Test
    void fail_when_given_emptyString() {
        final SurveyException exception = org.junit.jupiter.api.Assertions.assertThrows(SurveyException.class,
                () -> QuestionType.questionTypeFor("")
        );
        assertThat(exception.getMessage()).isEqualTo("Unsupported question type []. Supported values are [ratingquestion,singleselect,multiselect]");
    }

    @Test
    void fail_when_given_invalid_questionType() {
        final SurveyException exception = org.junit.jupiter.api.Assertions.assertThrows(SurveyException.class,
                () -> QuestionType.questionTypeFor("abcdef")
        );
        assertThat(exception.getMessage()).isEqualTo("Unsupported question type [abcdef]. Supported values are [ratingquestion,singleselect,multiselect]");
    }

}