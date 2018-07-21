package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyResponseTest {

    private RatingQuestion likeMyWorkQuestion;
    private RatingQuestion workplaceIsConvenientQuestion;
    private SingleSelectQuestion managerQuestion;
    private SingleSelectQuestion cityQuestion;
    private Survey survey;
    private Employee employee;

    @BeforeEach
    void setUp() {
        likeMyWorkQuestion = new RatingQuestion(Theme.Work, "I like the kind of work I do.");
        workplaceIsConvenientQuestion = new RatingQuestion(Theme.Place, "It is easy for me to reach my work");
        managerQuestion = new SingleSelectQuestion(Theme.Work, "Manager");
        cityQuestion = new SingleSelectQuestion(Theme.Demographic, "City");
        employee = new Employee("alex", "alex@gmail.com");
        survey = new Survey()
                .addQuestion(likeMyWorkQuestion)
                .addQuestion(managerQuestion);
    }

    @Test
    void test_create() {
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
        final SurveyResponse response = SurveyResponse.unsubmittedResponse(survey, employee);
        assertThat(response.submittedAt().isPresent()).isFalse();
    }

    @Test
    void test_survey_with_answers() {
        final LocalDateTime submittedAt = LocalDateTime.now().minusWeeks(5);
        final SingleSelectAnswer managerAnswer = new SingleSelectAnswer(managerQuestion, "Bruce Lee");
        final SingleSelectAnswer cityAnswer = new SingleSelectAnswer(cityQuestion, "Little China");
        final RatingAnswer likeMyWorkAnswer = new RatingAnswer(likeMyWorkQuestion, 5);

        final SurveyResponse response = SurveyResponse.submittedResponse(survey, employee, submittedAt)
                .addAnswer(managerAnswer)
                .addAnswer(cityAnswer)
                .addAnswer(likeMyWorkAnswer);

        assertThat(response.answerFor(managerQuestion).isPresent()).isTrue();
        assertThat(response.answerFor(managerQuestion).get().selection()).isEqualTo("Bruce Lee");
        assertThat(response.answerFor(cityQuestion).isPresent()).isTrue();
        assertThat(response.answerFor(cityQuestion).get().selection()).isEqualTo("Little China");
        assertThat(response.answerFor(likeMyWorkQuestion).isPresent()).isTrue();
        assertThat(response.answerFor(likeMyWorkQuestion).get().rating()).isEqualTo(5);
    }

    @Test
    void answer_to_a_question_is_not_mandatory() {
        final LocalDateTime submittedAt = LocalDateTime.now().minusWeeks(5);
        final SingleSelectAnswer managerAnswer = new SingleSelectAnswer(managerQuestion, "Bruce Lee");

        final SurveyResponse response = SurveyResponse.submittedResponse(survey, employee, submittedAt)
                .addAnswer(managerAnswer);

        assertThat(response.answerFor(managerQuestion).isPresent()).isTrue();
        assertThat(response.answerFor(cityQuestion).isPresent()).isFalse();
        assertThat(response.answerFor(likeMyWorkQuestion).isPresent()).isFalse();
        assertThat(response.answerFor(workplaceIsConvenientQuestion).isPresent()).isFalse();

    }
}