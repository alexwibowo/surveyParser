package com.github.wibowo.survey.model.questionAnswer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnswerTest {

    @Test
    void answerType_should_follow_questionType() {
        final IllegalStateException exception = Assertions.assertThrows(IllegalStateException.class,
                () -> new AnswerWithWrongQuestionType(
                        new RatingQuestion(0, Theme.Demographic, "This should fail"),
                        "Answer to wrong question")
        );
        assertThat(exception.getMessage())
                .contains("Attempt to create answer of type [com.github.wibowo.survey.model.questionAnswer.AnswerWithWrongQuestionType] using question of type [com.github.wibowo.survey.model.questionAnswer.RatingQuestion].")
                .contains("Expected answer is of type [com.github.wibowo.survey.model.questionAnswer.RatingAnswer].");
    }
}
