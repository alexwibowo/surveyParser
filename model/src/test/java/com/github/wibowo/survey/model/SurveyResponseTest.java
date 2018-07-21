package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import com.github.wibowo.survey.model.questionAnswer.SingleSelectQuestion;
import com.github.wibowo.survey.model.questionAnswer.Theme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyResponseTest {

    private RatingQuestion ratingQuestion1;
    private SingleSelectQuestion selectQuestion1;
    private Survey survey;

    @BeforeEach
    void setUp() {
        ratingQuestion1 = new RatingQuestion(Theme.Work, "I like the kind of work I do.");
        selectQuestion1 = new SingleSelectQuestion(Theme.Work, "Manager");

        survey = new Survey()
                .addQuestion(ratingQuestion1)
                .addQuestion(selectQuestion1);
    }

    @Test
    void test_create() {
        final Employee employee = new Employee("alex", "alex@gmail.com");
        final LocalDateTime submittedAt = LocalDateTime.now().minusWeeks(5);
        final SurveyResponse response = SurveyResponse.submittedResponse(survey, employee, submittedAt);

        assertThat(response.survey()).isSameAs(survey);
        assertThat(response.employee().id()).isEqualTo("alex");
        assertThat(response.employee().email()).isEqualTo("alex@gmail.com");
        assertThat(response.submittedAt().isPresent()).isTrue();
        assertThat(response.submittedAt().get()).isEqualTo(submittedAt);
    }

    @Test
    void survey_submission_is_optional() {
        final Employee employee = new Employee("alex", "alex@gmail.com");
        final SurveyResponse response = SurveyResponse.unsubmittedResponse(survey, employee);
        assertThat(response.submittedAt().isPresent()).isFalse();
    }
}