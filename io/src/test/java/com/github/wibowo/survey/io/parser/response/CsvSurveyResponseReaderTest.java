package com.github.wibowo.survey.io.parser.response;

import com.github.wibowo.survey.model.EmployeeResponse;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import com.github.wibowo.survey.model.questionAnswer.SingleSelectQuestion;
import com.github.wibowo.survey.model.questionAnswer.Theme;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvSurveyResponseReaderTest {

    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZZZ");
    private Survey survey1;
    private RatingQuestion iLikeMyWork;
    private RatingQuestion iHaveResourcesToDoMyWork;
    private RatingQuestion iFeelEmpowered;
    private SingleSelectQuestion whoIsMyManager;
    private Survey survey2;

    @BeforeEach
    void setUp() {
        iLikeMyWork = new RatingQuestion(Theme.Work, "I like the kind of work I do.");
        iHaveResourcesToDoMyWork = new RatingQuestion(Theme.Work, "In general, I have the resources (e.g., business tools, information, facilities, IT or functional support) I need to be effective.");
        iFeelEmpowered = new RatingQuestion(Theme.Place, "I feel empowered to get the work done for which I am responsible.");
        whoIsMyManager = new SingleSelectQuestion(Theme.Demographic, "Manager");

        survey1 = new Survey()
                .addQuestion(iLikeMyWork)
                .addQuestion(iHaveResourcesToDoMyWork)
                .addQuestion(iFeelEmpowered);

        survey2 = new Survey()
                .addQuestion(iLikeMyWork)
                .addQuestion(iHaveResourcesToDoMyWork)
                .addQuestion(iFeelEmpowered)
                .addQuestion(whoIsMyManager);
    }

    @Test
    void bad_answer_should_be_skipped() {
        final String[] rows = new String[]{
                "employee1@abc.xyz,1,2014-07-28T20:35:41+00:00,5,4,John",
                "employee2@abc.xyz,2,2014-07-30T23:35:41+10:00,5,5,3"
        };

        final CsvSurveyResponseReader csvSurveyResponseReader = new CsvSurveyResponseReader(survey1);
        csvSurveyResponseReader.process(inputFrom(rows));
        final List<EmployeeResponse> employeeSurveyResponse = csvSurveyResponseReader.employeeResponses();
        assertThat(employeeSurveyResponse).hasSize(2);

        final EmployeeResponse firstResponse = employeeSurveyResponse.get(0);
        verifyGenericAnswer(firstResponse, "1", "employee1@abc.xyz", "2014-07-28 20:35:41+0000");
        assertThat(firstResponse.answerFor(iLikeMyWork).rating()).isEqualTo(5);
        assertThat(firstResponse.answerFor(iHaveResourcesToDoMyWork).rating()).isEqualTo(4);
        assertTrue(firstResponse.answerFor(iFeelEmpowered).isNull());

        final EmployeeResponse secondResponse = employeeSurveyResponse.get(1);
        verifyGenericAnswer(secondResponse, "2", "employee2@abc.xyz", "2014-07-30 23:35:41+1000");
        assertThat(secondResponse.answerFor(iLikeMyWork).rating()).isEqualTo(5);
        assertThat(secondResponse.answerFor(iHaveResourcesToDoMyWork).rating()).isEqualTo(5);
        assertThat(secondResponse.answerFor(iFeelEmpowered).rating()).isEqualTo(3);

        final SurveySummary csvReaderResult = csvSurveyResponseReader.getSummary();

        // compare result to streaming version
        final CsvStreamingSurveyResponseReader streamingReader = new CsvStreamingSurveyResponseReader(survey1);
        streamingReader.process(inputFrom(rows));
        final SurveySummary streamingCsvReaderResult = streamingReader.getSummary();
        assertThat(streamingCsvReaderResult.totalParticipation())
                .isEqualTo(csvReaderResult.totalParticipation())
                .isEqualTo(2);
        assertThat(streamingCsvReaderResult.participationPercentage())
                .isEqualTo(csvReaderResult.participationPercentage())
                .isEqualTo(1.0);
        assertThat(streamingCsvReaderResult.averageRatingFor(iLikeMyWork))
                .isEqualTo(csvReaderResult.averageRatingFor(iLikeMyWork))
                .isEqualTo(5.0);
        assertThat(streamingCsvReaderResult.averageRatingFor(iHaveResourcesToDoMyWork))
                .isEqualTo(csvReaderResult.averageRatingFor(iHaveResourcesToDoMyWork))
                .isEqualTo(4.5);
        assertThat(streamingCsvReaderResult.averageRatingFor(iFeelEmpowered))
                .isEqualTo(csvReaderResult.averageRatingFor(iFeelEmpowered))
                .isEqualTo(3.0);
    }

    @Test
    void email_can_be_omitted() {
        final String[] rows = new String[]{
                ",1,2014-07-28T20:35:41+00:00,5,4,3"
        };

        final CsvSurveyResponseReader csvSurveyResponseReader = new CsvSurveyResponseReader(survey1);
        csvSurveyResponseReader.process(inputFrom(rows));
        final List<EmployeeResponse> employeeSurveyResponse = csvSurveyResponseReader.employeeResponses();
        assertThat(employeeSurveyResponse).hasSize(1);
        verifyGenericAnswer(employeeSurveyResponse.get(0), "1", "", "2014-07-28 20:35:41+0000");

        final SurveySummary csvReaderResult = csvSurveyResponseReader.getSummary();

        // compare result to streaming version
        final CsvStreamingSurveyResponseReader streamingReader = new CsvStreamingSurveyResponseReader(survey1);
        streamingReader.process(inputFrom(rows));

        final SurveySummary streamingCsvReaderResult = streamingReader.getSummary();
        assertThat(streamingCsvReaderResult.totalParticipation())
                .isEqualTo(csvReaderResult.totalParticipation())
                .isEqualTo(1);
    }

    @Test
    void employeeId_can_be_omitted() {
        final String[] rows = new String[]{
                "employee1@abc.xyz,,2014-07-28T20:35:41+00:00,5,4,3"
        };

        final CsvSurveyResponseReader csvSurveyResponseReader = new CsvSurveyResponseReader(survey1);
        csvSurveyResponseReader.process(inputFrom(rows));
        final List<EmployeeResponse> employeeSurveyResponse = csvSurveyResponseReader.employeeResponses();
        assertThat(employeeSurveyResponse).hasSize(1);

        verifyGenericAnswer(employeeSurveyResponse.get(0), "", "employee1@abc.xyz", "2014-07-28 20:35:41+0000");
    }

    @Test
    void submissionDate_can_be_omitted_which_means_survey_was_not_submitted() {
        final String[] rows = new String[]{
                "employee1@abc.xyz,1,,5,4,3"
        };

        final CsvSurveyResponseReader csvSurveyResponseReader = new CsvSurveyResponseReader(survey1);
        csvSurveyResponseReader.process(inputFrom(rows));
        final List<EmployeeResponse> employeeSurveyResponse = csvSurveyResponseReader.employeeResponses();
        assertThat(employeeSurveyResponse).hasSize(1);

        verifyGenericAnswer(employeeSurveyResponse.get(0), "1", "employee1@abc.xyz", null);

        final SurveySummary csvReaderResult = csvSurveyResponseReader.getSummary();

        // compare result to streaming version
        final CsvStreamingSurveyResponseReader streamingReader = new CsvStreamingSurveyResponseReader(survey1);
        streamingReader.process(inputFrom(rows));

        final SurveySummary streamingCsvReaderResult = streamingReader.getSummary();
        assertThat(streamingCsvReaderResult.totalParticipation())
                .isEqualTo(csvReaderResult.totalParticipation())
                .isEqualTo(0);
        assertThat(streamingCsvReaderResult.participationPercentage())
                .isEqualTo(csvReaderResult.participationPercentage())
                .isEqualTo(0);
        assertThat(streamingCsvReaderResult.averageRatingFor(iLikeMyWork))
                .isEqualTo(csvReaderResult.averageRatingFor(iLikeMyWork))
                .isEqualTo(Double.NaN);
        assertThat(streamingCsvReaderResult.averageRatingFor(iHaveResourcesToDoMyWork))
                .isEqualTo(csvReaderResult.averageRatingFor(iHaveResourcesToDoMyWork))
                .isEqualTo(Double.NaN);
        assertThat(streamingCsvReaderResult.averageRatingFor(iFeelEmpowered))
                .isEqualTo(csvReaderResult.averageRatingFor(iFeelEmpowered))
                .isEqualTo(Double.NaN);
    }

    @Test
    void answer_to_question_is_optional() {
        final String[] rows = new String[]{
                "employee1@abc.xyz,1,2014-07-28T20:35:41+00:00,5,4,"
        };

        final CsvSurveyResponseReader csvSurveyResponseReader = new CsvSurveyResponseReader(survey1);
        csvSurveyResponseReader.process(inputFrom(rows));
        final List<EmployeeResponse> employeeSurveyResponse = csvSurveyResponseReader.employeeResponses();
        assertThat(employeeSurveyResponse).hasSize(1);

        final EmployeeResponse response = employeeSurveyResponse.get(0);
        verifyGenericAnswer(response, "1", "employee1@abc.xyz", "2014-07-28 20:35:41+0000");
        assertThat(response.answerFor(iLikeMyWork).rating()).isEqualTo(5);
        assertThat(response.answerFor(iHaveResourcesToDoMyWork).rating()).isEqualTo(4);
        assertTrue(response.answerFor(iFeelEmpowered).isNull());
    }


    @Test
    void reading_response_with_one_employee() {
        final String[] rows = new String[]{
                "employee1@abc.xyz,1,2014-07-28T20:35:41+00:00,5,4,3"
        };

        final CsvSurveyResponseReader csvSurveyResponseReader = new CsvSurveyResponseReader(survey1);
        csvSurveyResponseReader.process(inputFrom(rows));
        final List<EmployeeResponse> employeeSurveyResponse = csvSurveyResponseReader.employeeResponses();
        assertThat(employeeSurveyResponse).hasSize(1);

        final EmployeeResponse response = employeeSurveyResponse.get(0);
        verifyGenericAnswer(response, "1", "employee1@abc.xyz", "2014-07-28 20:35:41+0000");
        assertThat(response.answerFor(iLikeMyWork).rating()).isEqualTo(5);
        assertThat(response.answerFor(iHaveResourcesToDoMyWork).rating()).isEqualTo(4);
        assertThat(response.answerFor(iFeelEmpowered).rating()).isEqualTo(3);
    }

    @Test
    void reading_response_with_multiple_employee() {
        final String[] rows = new String[]{
                "employee1@abc.xyz,1,2014-07-28T20:35:41+00:00,5,4,1,John",
                "employee2@abc.xyz,2,2014-07-30T23:35:41+10:00,3,1,4,Sally"
        };

        final CsvSurveyResponseReader csvSurveyResponseReader = new CsvSurveyResponseReader(survey2);
        csvSurveyResponseReader.process(inputFrom(rows));
        final List<EmployeeResponse> employeeSurveyResponse = csvSurveyResponseReader.employeeResponses();
        assertThat(employeeSurveyResponse).hasSize(2);

        final EmployeeResponse firstResponse = employeeSurveyResponse.get(0);
        verifyGenericAnswer(firstResponse, "1", "employee1@abc.xyz", "2014-07-28 20:35:41+0000");
        assertThat(firstResponse.answerFor(iLikeMyWork).rating()).isEqualTo(5);
        assertThat(firstResponse.answerFor(iHaveResourcesToDoMyWork).rating()).isEqualTo(4);
        assertThat(firstResponse.answerFor(whoIsMyManager).selection()).isEqualTo("John");

        final EmployeeResponse secondResponse = employeeSurveyResponse.get(1);
        verifyGenericAnswer(secondResponse, "2", "employee2@abc.xyz", "2014-07-30 23:35:41+1000");
        assertThat(secondResponse.answerFor(iLikeMyWork).rating()).isEqualTo(3);
        assertThat(secondResponse.answerFor(iHaveResourcesToDoMyWork).rating()).isEqualTo(1);
        assertThat(secondResponse.answerFor(whoIsMyManager).selection()).isEqualTo("Sally");

        final SurveySummary csvReaderResult = csvSurveyResponseReader.getSummary();

        // compare result to streaming version
        final CsvStreamingSurveyResponseReader streamingReader = new CsvStreamingSurveyResponseReader(survey2);
        streamingReader.process(inputFrom(rows));

        final SurveySummary streamingCsvReaderResult = streamingReader.getSummary();
        assertThat(streamingCsvReaderResult.totalParticipation())
                .isEqualTo(csvReaderResult.totalParticipation())
                .isEqualTo(2);
        assertThat(streamingCsvReaderResult.participationPercentage())
                .isEqualTo(csvReaderResult.participationPercentage())
                .isEqualTo(1.0);
        assertThat(streamingCsvReaderResult.averageRatingFor(iLikeMyWork))
                .isEqualTo(csvReaderResult.averageRatingFor(iLikeMyWork))
                .isEqualTo(4.0);
        assertThat(streamingCsvReaderResult.averageRatingFor(iHaveResourcesToDoMyWork))
                .isEqualTo(csvReaderResult.averageRatingFor(iHaveResourcesToDoMyWork))
                .isEqualTo(2.5);
        assertThat(streamingCsvReaderResult.averageRatingFor(iFeelEmpowered))
                .isEqualTo(csvReaderResult.averageRatingFor(iFeelEmpowered))
                .isEqualTo(2.5);
    }

    @Test
    void same_employee_can_submit_response_multiple_time() {
        final String[] rows = new String[]{
                "employee1@abc.xyz,1,2014-07-28T20:35:41+00:00,5,4,2,John",
                "employee1@abc.xyz,1,2014-07-30T23:35:41+10:00,5,5,1,Sally",
                "employee1@abc.xyz,1,2014-08-04T23:35:41+10:00,5,,,Sally"
        };

        final CsvSurveyResponseReader csvSurveyResponseReader = new CsvSurveyResponseReader(survey2);
        csvSurveyResponseReader.process(inputFrom(rows));
        final List<EmployeeResponse> employeeSurveyResponse = csvSurveyResponseReader.employeeResponses();
        assertThat(employeeSurveyResponse).hasSize(3);

        final EmployeeResponse firstResponse = employeeSurveyResponse.get(0);
        verifyGenericAnswer(firstResponse, "1", "employee1@abc.xyz", "2014-07-28 20:35:41+0000");
        assertThat(firstResponse.answerFor(iLikeMyWork).rating()).isEqualTo(5);
        assertThat(firstResponse.answerFor(iHaveResourcesToDoMyWork).rating()).isEqualTo(4);
        assertThat(firstResponse.answerFor(whoIsMyManager).selection()).isEqualTo("John");

        final EmployeeResponse secondResponse = employeeSurveyResponse.get(1);
        verifyGenericAnswer(secondResponse, "1", "employee1@abc.xyz", "2014-07-30 23:35:41+1000");
        assertThat(secondResponse.answerFor(iLikeMyWork).rating()).isEqualTo(5);
        assertThat(secondResponse.answerFor(iHaveResourcesToDoMyWork).rating()).isEqualTo(5);
        assertThat(secondResponse.answerFor(whoIsMyManager).selection()).isEqualTo("Sally");

        final EmployeeResponse thirdResponse = employeeSurveyResponse.get(2);
        verifyGenericAnswer(thirdResponse, "1", "employee1@abc.xyz", "2014-08-04 23:35:41+1000");
        assertThat(thirdResponse.answerFor(iLikeMyWork).rating()).isEqualTo(5);
        assertTrue(thirdResponse.answerFor(iHaveResourcesToDoMyWork).isNull());
        assertThat(thirdResponse.answerFor(whoIsMyManager).selection()).isEqualTo("Sally");

        final SurveySummary csvReaderResult = csvSurveyResponseReader.getSummary();

        // compare result to streaming version
        final CsvStreamingSurveyResponseReader streamingReader = new CsvStreamingSurveyResponseReader(survey2);
        streamingReader.process(inputFrom(rows));

        final SurveySummary streamingCsvReaderResult = streamingReader.getSummary();
        assertThat(streamingCsvReaderResult.totalParticipation())
                .isEqualTo(csvReaderResult.totalParticipation())
                .isEqualTo(3);
        assertThat(streamingCsvReaderResult.participationPercentage())
                .isEqualTo(csvReaderResult.participationPercentage())
                .isEqualTo(1.0);
        assertThat(streamingCsvReaderResult.averageRatingFor(iLikeMyWork))
                .isEqualTo(csvReaderResult.averageRatingFor(iLikeMyWork))
                .isEqualTo(5.0);
        assertThat(streamingCsvReaderResult.averageRatingFor(iHaveResourcesToDoMyWork))
                .isEqualTo(csvReaderResult.averageRatingFor(iHaveResourcesToDoMyWork))
                .isEqualTo(4.5);
        assertThat(streamingCsvReaderResult.averageRatingFor(iFeelEmpowered))
                .isEqualTo(csvReaderResult.averageRatingFor(iFeelEmpowered))
                .isEqualTo(1.5);

    }

    private void verifyGenericAnswer(final EmployeeResponse response,
                                     final String expectedEmployeeId,
                                     final String expectedEmployeeEmail,
                                     final @Nullable String expectedSubmissionDateTime) {
        assertThat(response.employee().id()).isEqualTo(expectedEmployeeId);
        assertThat(response.employee().email()).isEqualTo(expectedEmployeeEmail);

        if (expectedSubmissionDateTime != null) {
            assertTrue(response.wasSubmitted());
            assertTrue(response.submittedAt().isPresent());
            assertThat(response.submittedAt().get().format(dateFormatter)).isEqualTo(expectedSubmissionDateTime);
        } else {
            assertFalse(response.wasSubmitted());
            assertFalse(response.submittedAt().isPresent());
        }
    }


    @NotNull
    private InputStream inputFrom(String[] rows) {
        final String fileContent = Arrays.stream(rows).collect(Collectors.joining("\n"));
        return new ByteArrayInputStream(fileContent.getBytes());
    }

}