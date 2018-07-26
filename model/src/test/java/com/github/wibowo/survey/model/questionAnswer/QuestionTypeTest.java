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
        assertThat(exception.getMessage()).isEqualTo("Unsupported question type []. Supported values are [ratingquestion,singleselect]");
    }

    @Test
    void fail_when_given_invalid_questionType() {
        final SurveyException exception = org.junit.jupiter.api.Assertions.assertThrows(SurveyException.class,
                () -> QuestionType.questionTypeFor("abcdef")
        );
        assertThat(exception.getMessage()).isEqualTo("Unsupported question type [abcdef]. Supported values are [ratingquestion,singleselect]");
    }

}