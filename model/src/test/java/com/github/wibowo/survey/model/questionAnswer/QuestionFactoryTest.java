package com.github.wibowo.survey.model.questionAnswer;

import org.junit.jupiter.api.DynamicTest;
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
                            final Question ratingquestion = QuestionFactory.createFrom(theme, "ratingquestion", "any text here. does not matter");
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
                            final Question ratingquestion = QuestionFactory.createFrom(theme, "singleselect", "comptine d'un autre été");
                            assertThat(ratingquestion).isInstanceOf(SingleSelectQuestion.class);
                            assertThat(ratingquestion.theme()).isEqualTo(theme);
                            assertThat(ratingquestion.sentence()).isEqualTo("comptine d'un autre été");
                        }
                ));
    }



}