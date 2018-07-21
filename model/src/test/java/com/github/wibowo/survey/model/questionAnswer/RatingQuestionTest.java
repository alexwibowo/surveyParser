package com.github.wibowo.survey.model.questionAnswer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

}