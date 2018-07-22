package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import com.github.wibowo.survey.model.questionAnswer.SingleSelectQuestion;
import com.github.wibowo.survey.model.questionAnswer.Theme;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyTest {

    @Test
    void empty_survey() {
        final Survey survey = new Survey();
        final Iterable<Question> questions = survey.questions();
        AssertionsForClassTypes.assertThat(questions).asList().isEmpty();
    }

    @Test
    void test_survey_with_questions() {
        final RatingQuestion ratingQuestion1 = new RatingQuestion(Theme.Work, "I like the kind of work I do.");
        final RatingQuestion ratingQuestion2 = new RatingQuestion(Theme.Place, "I feel empowered to get the work done for which I am responsible.");
        final SingleSelectQuestion selectQuestion1 = new SingleSelectQuestion(Theme.Work, "Manager");
        final SingleSelectQuestion selectQuestion2 = new SingleSelectQuestion(Theme.Demographic, "City");

        final Survey survey = new Survey()
                .addQuestion(ratingQuestion1)
                .addQuestion(ratingQuestion2)
                .addQuestion(selectQuestion1)
                .addQuestion(selectQuestion2);

        final Iterable<Question> questions = survey.questions();
        assertThat(questions)
                .containsExactly(ratingQuestion1, ratingQuestion2, selectQuestion1, selectQuestion2);

    }

    @Test
    void test_get_question_by_index() {
        throw new RuntimeException("test me");
    }

}