package com.github.wibowo.survey.model.questionAnswer;

import com.github.wibowo.survey.model.SurveyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionFactoryTest {

    @TestFactory
    Stream<DynamicTest> creation_of_RatingQuestion() {
        return Arrays.stream(Theme.values())
                .map(theme -> DynamicTest.dynamicTest(
                        String.format("Test creating RatingQuestion using theme %s", theme),
                        () -> {
                            final Question ratingquestion = QuestionType.createFrom(theme, "ratingquestion", "any text here. does not matter");
                            assertThat(ratingquestion).isInstanceOf(RatingQuestion.class);
                            assertThat(ratingquestion.theme()).isEqualTo(theme);
                            assertThat(ratingquestion.sentence()).isEqualTo("any text here. does not matter");
                        }
                ));
    }

    @TestFactory
    Stream<DynamicTest> creation_of_SingleSelectQuestion() {
        return Arrays.stream(Theme.values())
                .map(theme -> DynamicTest.dynamicTest(
                        String.format("Test creating SingleSelectQuestion using theme %s", theme),
                        () -> {
                            final Question ratingquestion = QuestionType.createFrom(theme, "singleselect", "comptine d'un autre été");
                            assertThat(ratingquestion).isInstanceOf(SingleSelectQuestion.class);
                            assertThat(ratingquestion.theme()).isEqualTo(theme);
                            assertThat(ratingquestion.sentence()).isEqualTo("comptine d'un autre été");
                        }
                ));
    }

    @Test
    void fail_on_attempt_to_create_unsupported_questionType() {
        final SurveyException exception = Assertions.assertThrows(SurveyException.class,
                () -> QuestionType.createFrom(Theme.Work, "someunknowntype", "una mattina")
        );
        assertThat(exception.getMessage()).contains("Unsupported question type [someunknowntype]");
    }

    @Test
    void question_type_cant_be_null() {
        final NullPointerException exception = Assertions.assertThrows(NullPointerException.class,
                () -> QuestionType.createFrom(Theme.Work, null, "una mattina")
        );
        assertThat(exception.getMessage()).isEqualTo("question type must be provided");
    }



}