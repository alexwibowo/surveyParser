package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeResponseTest {

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
        final ZonedDateTime submittedAt = ZonedDateTime.now().minusWeeks(5);
        final EmployeeResponse response = EmployeeResponse.submittedResponse(survey, employee, submittedAt);

        assertThat(response.survey()).isSameAs(survey);
        assertThat(response.employee().id()).isEqualTo("alex");
        assertThat(response.employee().email()).isEqualTo("alex@gmail.com");
        assertTrue(response.wasSubmitted());
        assertThat(response.submittedAt().get()).isEqualTo(submittedAt);
    }

    @Test
    void survey_submission_is_optional() {
        final EmployeeResponse response = EmployeeResponse.unsubmittedResponse(survey, employee);
        assertFalse(response.wasSubmitted());
    }

    @Test
    void test_survey_with_answers() {
        final ZonedDateTime submittedAt = ZonedDateTime.now().minusWeeks(5);
        final SingleSelectAnswer managerAnswer = SingleSelectAnswer.createAnswer(managerQuestion, "Bruce Lee");
        final SingleSelectAnswer cityAnswer = SingleSelectAnswer.createAnswer(cityQuestion, "Little China");
        final RatingAnswer likeMyWorkAnswer = RatingAnswer.createAnswer(likeMyWorkQuestion, 5);

        final EmployeeResponse response = EmployeeResponse.submittedResponse(survey, employee, submittedAt)
                .addAnswer(managerAnswer)
                .addAnswer(cityAnswer)
                .addAnswer(likeMyWorkAnswer);

        assertThat(response.answerFor(managerQuestion).selection()).isEqualTo("Bruce Lee");
        assertThat(response.answerFor(cityQuestion).selection()).isEqualTo("Little China");
        assertThat(response.answerFor(likeMyWorkQuestion).rating()).isEqualTo(5);
    }

    @Test
    void answer_to_a_question_is_not_mandatory() {
        final ZonedDateTime submittedAt = ZonedDateTime.now().minusWeeks(5);
        final SingleSelectAnswer managerAnswer = SingleSelectAnswer.createAnswer(managerQuestion, "Bruce Lee");

        final EmployeeResponse response = EmployeeResponse.submittedResponse(survey, employee, submittedAt)
                .addAnswer(managerAnswer);

        assertThat(response.answerFor(managerQuestion).isNull()).isFalse();
        assertThat(response.answerFor(cityQuestion).isNull()).isTrue();
        assertThat(response.answerFor(likeMyWorkQuestion).isNull()).isTrue();
        assertTrue(response.answerFor(workplaceIsConvenientQuestion).isNull());
    }
}