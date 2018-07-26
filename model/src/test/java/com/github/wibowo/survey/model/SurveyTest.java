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
    private RatingQuestion ratingQuestion3;
    private RatingQuestion nonExistentRatingQuestion;
    private SingleSelectQuestion selectQuestion1;
    private SingleSelectQuestion selectQuestion2;
    private Survey survey;

    @BeforeEach
    void setUp() {
        ratingQuestion1 = new RatingQuestion(0, Theme.Work, "I like the kind of work I do.");
        ratingQuestion2 = new RatingQuestion(1, Theme.Place, "I feel empowered to get the work done for which I am responsible.");
        ratingQuestion3 = new RatingQuestion(4, Theme.Work, "I would definitely work for this company again");
        nonExistentRatingQuestion = new RatingQuestion(5, Theme.Work, "This question is invalid");
        selectQuestion1 = new SingleSelectQuestion(2, Theme.Work, "Manager");
        selectQuestion2 = new SingleSelectQuestion(3, Theme.Demographic, "City");

        survey = new Survey()
                .addQuestion(ratingQuestion1)
                .addQuestion(ratingQuestion2)
                .addQuestion(selectQuestion1)
                .addQuestion(selectQuestion2)
                .addQuestion(ratingQuestion3);
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
                .containsExactly(ratingQuestion1, ratingQuestion2, selectQuestion1, selectQuestion2, ratingQuestion3);

        assertThat(survey.questionNumber(0)).isSameAs(ratingQuestion1);
        assertThat(survey.questionNumber(1)).isSameAs(ratingQuestion2);
        assertThat(survey.questionNumber(2)).isSameAs(selectQuestion1);
        assertThat(survey.questionNumber(3)).isSameAs(selectQuestion2);
        assertThat(survey.questionNumber(4)).isSameAs(ratingQuestion3);
    }

    @Test
    void test_index_of_survey_question() {
        assertThat(survey.indexForQuestion(ratingQuestion1)).isEqualTo(0);
        assertThat(survey.indexForQuestion(ratingQuestion2)).isEqualTo(1);
        assertThat(survey.indexForQuestion(ratingQuestion3)).isEqualTo(4);
        assertThat(survey.indexForQuestion(selectQuestion1)).isEqualTo(2);
        assertThat(survey.indexForQuestion(selectQuestion2)).isEqualTo(3);
    }

    @Test
    void fail_on_attempt_to_find_index_of_non_existent_question() {
        final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> survey.indexForQuestion(nonExistentRatingQuestion)
        );
        assertThat(exception.getMessage()).matches("Question.*This question is invalid.*was not found in the survey.*");
    }

    @Test
    void should_fail_on_attempt_to_add_duplicated_question() {
        survey = new Survey().addQuestion(ratingQuestion1);

        final SurveyException exception = Assertions.assertThrows(SurveyException.class,
                () -> survey.addQuestion(ratingQuestion1)
        );
        assertThat(exception.getMessage()).matches("Question.*already exists in the survey");
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
                () -> survey.questionNumber(5)
        );
        assertThat(exception.getMessage()).contains("Attempt to ask for question [5]. But there are only [5] questions available");
    }

}