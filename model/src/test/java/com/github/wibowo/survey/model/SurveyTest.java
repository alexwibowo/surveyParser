package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.*;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyTest {

    private RatingQuestion ratingQuestion1;
    private RatingQuestion ratingQuestion2;
    private SingleSelectQuestion selectQuestion1;
    private SingleSelectQuestion selectQuestion2;
    private Survey survey;

    @BeforeEach
    void setUp() {
        ratingQuestion1 = new RatingQuestion(Theme.Work, "I like the kind of work I do.");
        ratingQuestion2 = new RatingQuestion(Theme.Place, "I feel empowered to get the work done for which I am responsible.");
        selectQuestion1 = new SingleSelectQuestion(Theme.Work, "Manager");
        selectQuestion2 = new SingleSelectQuestion(Theme.Demographic, "City");

        survey = new Survey()
                .addQuestion(ratingQuestion1)
                .addQuestion(ratingQuestion2)
                .addQuestion(selectQuestion1)
                .addQuestion(selectQuestion2);
    }

    @Test
    void empty_survey() {
        final Survey survey = new Survey();
        final Iterable<Question> questions = survey.questions();
        AssertionsForClassTypes.assertThat(questions).asList().isEmpty();
    }

    @Test
    void test_survey_with_questions() {
        final Iterable<Question> questions = survey.questions();
        assertThat(questions)
                .containsExactly(ratingQuestion1, ratingQuestion2, selectQuestion1, selectQuestion2);

        assertThat(survey.questionNumber(0)).isSameAs(ratingQuestion1);
        assertThat(survey.questionNumber(1)).isSameAs(ratingQuestion2);
        assertThat(survey.questionNumber(2)).isSameAs(selectQuestion1);
        assertThat(survey.questionNumber(3)).isSameAs(selectQuestion2);
    }

    @Test
    void get_question_by_index_expects_positive_number() {
        final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> survey.questionNumber(-1)
        );
        assertThat(exception.getMessage()).isEqualTo("Question index starts from 0");
    }

    @Test
    void get_question_by_index_cant_be_more_than_number_of_questions_available() {
        final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> survey.questionNumber(4)
        );
        assertThat(exception.getMessage()).contains("Attempt to ask for question [4]. But there are only [4] questions available");
    }

}