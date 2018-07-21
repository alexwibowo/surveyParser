package com.github.wibowo.survey.model.questionAnswer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class RatingAnswerTest {

    @Test
    void reject_rating_less_than_1() {
        final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new RatingAnswer(null, 0)
        );
        assertThat(exception.getMessage()).isEqualTo("Rating must be between 1 and 5.");
    }

    @Test
    void reject_rating_greater_than_5() {
        final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new RatingAnswer(null, 6)
        );
        assertThat(exception.getMessage()).isEqualTo("Rating must be between 1 and 5.");
    }

    @Test
    void must_be_constructed_with_ratingQuestion() {
        final NullPointerException exception = Assertions.assertThrows(NullPointerException.class,
                () -> new RatingAnswer(null, 1)
        );
        assertThat(exception.getMessage()).isEqualTo("Answer must be constructed with a question.");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void accept_rating_between_1_and_5(int ratingValue) {
        final RatingQuestion ratingQuestion = new RatingQuestion(Theme.Place, "I feel empowered to get the work done for which I am responsible.");
        final RatingAnswer answer = new RatingAnswer(ratingQuestion, ratingValue);

        assertThat(answer.question()).isSameAs(ratingQuestion);
        assertThat(answer.rating()).isEqualTo(ratingValue);
    }

}