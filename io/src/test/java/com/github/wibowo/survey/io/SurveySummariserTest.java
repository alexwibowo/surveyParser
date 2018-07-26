package com.github.wibowo.survey.io;

import com.github.wibowo.survey.model.*;
import com.github.wibowo.survey.model.questionAnswer.RatingAnswer;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import com.github.wibowo.survey.model.questionAnswer.SingleSelectQuestion;
import com.github.wibowo.survey.model.questionAnswer.Theme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SurveySummariserTest {

    private Survey survey1;
    private RatingQuestion iLikeMyWork;
    private RatingQuestion iHaveResourcesToDoMyWork;
    private RatingQuestion iFeelEmpowered;
    private SingleSelectQuestion whoIsMyManager;

    @BeforeEach
    void setUp() {
        iLikeMyWork = new RatingQuestion(Theme.Work, "I like the kind of work I do.");
        iHaveResourcesToDoMyWork = new RatingQuestion(Theme.Work, "In general, I have the resources (e.g., business tools, information, facilities, IT or functional support) I need to be effective.");
        iFeelEmpowered = new RatingQuestion(Theme.Place, "I feel empowered to get the work done for which I am responsible.");
        whoIsMyManager = new SingleSelectQuestion(Theme.Demographic, "Manager");

        survey1 = new Survey()
                .addQuestion(iLikeMyWork)
                .addQuestion(iHaveResourcesToDoMyWork)
                .addQuestion(iFeelEmpowered)
                .addQuestion(whoIsMyManager);
    }

    @Test
    void simple_test_with_all_values_are_present() {
        final ArrayList<EmployeeResponse> employeeResponses = new ArrayList<>();
        employeeResponses.add(EmployeeResponse.submittedResponse(survey1, new Employee("1", "alex@gmail.com"), ZonedDateTime.now().minusDays(5))
                .addAnswer(RatingAnswer.createAnswer(iLikeMyWork, 5))
                .addAnswer(RatingAnswer.createAnswer(iHaveResourcesToDoMyWork, 5))
                .addAnswer(RatingAnswer.createAnswer(iFeelEmpowered, 5)));
        employeeResponses.add(EmployeeResponse.submittedResponse(survey1, new Employee("2", "bob@gmail.com"), ZonedDateTime.now().minusDays(6))
                .addAnswer(RatingAnswer.createAnswer(iLikeMyWork, 4))
                .addAnswer(RatingAnswer.createAnswer(iHaveResourcesToDoMyWork, 3))
                .addAnswer(RatingAnswer.createAnswer(iFeelEmpowered, 3)));
        employeeResponses.add(EmployeeResponse.submittedResponse(survey1, new Employee("3", "john@gmail.com"), ZonedDateTime.now().minusDays(2))
                .addAnswer(RatingAnswer.createAnswer(iLikeMyWork, 1))
                .addAnswer(RatingAnswer.createAnswer(iHaveResourcesToDoMyWork, 1))
                .addAnswer(RatingAnswer.createAnswer(iFeelEmpowered, 2)));
        final SurveySummary summary = SurveySummariser.summarise(survey1, employeeResponses);
        assertThat(summary.participationPercentage()).isEqualTo(1.0);
        assertThat(summary.averageRatingFor(iLikeMyWork)).isEqualTo( (double) (5 + 4 + 1) / 3 );
        assertThat(summary.averageRatingFor(iHaveResourcesToDoMyWork)).isEqualTo( (double) (5 + 3 + 1) / 3 );
        assertThat(summary.averageRatingFor(iFeelEmpowered)).isEqualTo( (double) (5 + 3 + 2) / 3 );
    }

    @Test
    void all_datapoints_from_unsubmitted_survey_should_be_excluded() {
        final ArrayList<EmployeeResponse> employeeResponses = new ArrayList<>();
        employeeResponses.add(EmployeeResponse.submittedResponse(survey1, new Employee("1", "alex@gmail.com"), ZonedDateTime.now().minusDays(5))
                .addAnswer(RatingAnswer.createAnswer(iLikeMyWork, 5))
                .addAnswer(RatingAnswer.createAnswer(iHaveResourcesToDoMyWork, 5))
                .addAnswer(RatingAnswer.createAnswer(iFeelEmpowered, 5)));
        employeeResponses.add(EmployeeResponse.unsubmittedResponse(survey1, new Employee("2", "bob@gmail.com"))
                .addAnswer(RatingAnswer.createAnswer(iLikeMyWork, 4))
                .addAnswer(RatingAnswer.createAnswer(iHaveResourcesToDoMyWork, 3))
                .addAnswer(RatingAnswer.createAnswer(iFeelEmpowered, 3)));
        employeeResponses.add(EmployeeResponse.submittedResponse(survey1, new Employee("3", "john@gmail.com"), ZonedDateTime.now().minusDays(2))
                .addAnswer(RatingAnswer.createAnswer(iLikeMyWork, 1))
                .addAnswer(RatingAnswer.createAnswer(iHaveResourcesToDoMyWork, 1))
                .addAnswer(RatingAnswer.createAnswer(iFeelEmpowered, 2)));

        final SurveySummary summary = SurveySummariser.summarise(survey1, employeeResponses);
        assertThat(summary.participationPercentage()).isEqualTo( 2.0d /3);
        assertThat(summary.averageRatingFor(iLikeMyWork)).isEqualTo( 3.0d );
        assertThat(summary.averageRatingFor(iHaveResourcesToDoMyWork)).isEqualTo( 3.0d );
        assertThat(summary.averageRatingFor(iFeelEmpowered)).isEqualTo( (double) (5  + 2) / 2 );
    }

    @Test
    void unanswered_question_should_be_excluded() {
        final ArrayList<EmployeeResponse> employeeResponses = new ArrayList<>();
        employeeResponses.add(EmployeeResponse.submittedResponse(survey1, new Employee("1", "alex@gmail.com"), ZonedDateTime.now().minusDays(5))
                .addAnswer(RatingAnswer.createAnswer(iLikeMyWork, 5))
                .addAnswer(RatingAnswer.createAnswer(iHaveResourcesToDoMyWork, 5))
                .addAnswer(RatingAnswer.createAnswer(iFeelEmpowered, 5)));
        employeeResponses.add(EmployeeResponse.submittedResponse(survey1, new Employee("2", "bob@gmail.com"), ZonedDateTime.now().minusDays(6))
                .addAnswer(RatingAnswer.nullAnswer(iLikeMyWork))
                .addAnswer(RatingAnswer.createAnswer(iHaveResourcesToDoMyWork, 3))
                .addAnswer(RatingAnswer.nullAnswer(iFeelEmpowered)));
        employeeResponses.add(EmployeeResponse.submittedResponse(survey1, new Employee("3", "john@gmail.com"), ZonedDateTime.now().minusDays(2))
                .addAnswer(RatingAnswer.createAnswer(iLikeMyWork, 1))
                .addAnswer(RatingAnswer.createAnswer(iHaveResourcesToDoMyWork, 1))
                .addAnswer(RatingAnswer.createAnswer(iFeelEmpowered, 2)));
        final SurveySummary summary = SurveySummariser.summarise(survey1, employeeResponses);
        assertThat(summary.participationPercentage()).isEqualTo(1.0);
        assertThat(summary.averageRatingFor(iLikeMyWork)).isEqualTo( (double) (5 + 1) / 2);
        assertThat(summary.averageRatingFor(iHaveResourcesToDoMyWork)).isEqualTo( (double) (5  + 3 + 1) / 3 );
        assertThat(summary.averageRatingFor(iFeelEmpowered)).isEqualTo( (double) (5 + 2) / 2 );
    }



}