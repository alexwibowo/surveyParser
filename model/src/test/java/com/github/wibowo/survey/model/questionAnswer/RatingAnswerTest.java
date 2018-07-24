package com.github.wibowo.survey.model.questionAnswer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class RatingAnswerTest {

    @Test
    void reject_rating_less_than_1() {
        final RatingQuestion ratingQuestion = new RatingQuestion(0, Theme.Place, "I feel empowered to get the work done for which I am responsible.");
        final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> RatingAnswer.createAnswer(ratingQuestion, 0)
        );
        assertThat(exception.getMessage()).isEqualTo("0 is an invalid rating value. Rating must be between 1 and 5.");
    }

    @Test
    void reject_rating_greater_than_5() {
        final RatingQuestion ratingQuestion = new RatingQuestion(0, Theme.Place, "I feel empowered to get the work done for which I am responsible.");
        final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> RatingAnswer.createAnswer(ratingQuestion, 6)
        );
        assertThat(exception.getMessage()).isEqualTo("6 is an invalid rating value. Rating must be between 1 and 5.");
    }

    @Test
    void must_be_constructed_with_ratingQuestion() {
        final NullPointerException exception = Assertions.assertThrows(NullPointerException.class,
                () -> RatingAnswer.createAnswer(null, 1)
        );
        assertThat(exception.getMessage()).isEqualTo("Answer must be constructed with a question.");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void accept_rating_between_1_and_5(int ratingValue) {
        final RatingQuestion ratingQuestion = new RatingQuestion(0, Theme.Place, "I feel empowered to get the work done for which I am responsible.");
        final RatingAnswer answer = RatingAnswer.createAnswer(ratingQuestion, ratingValue);

        assertThat(answer.question()).isSameAs(ratingQuestion);
        assertThat(answer.rating()).isEqualTo(ratingValue);
        assertThat(answer.isNull()).isFalse();
    }

    @Test
    void test_null_answer() {
        final RatingQuestion ratingQuestion = new RatingQuestion(0, Theme.Place, "I feel empowered to get the work done for which I am responsible.");
        final RatingAnswer nullAnswer = RatingAnswer.nullAnswer(ratingQuestion);
        assertThat(nullAnswer.isNull()).isTrue();
    }

}