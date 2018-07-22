package com.github.wibowo.survey.io.csv;

import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.EmployeeResponse;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import com.github.wibowo.survey.model.questionAnswer.SingleSelectQuestion;
import com.github.wibowo.survey.model.questionAnswer.Theme;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvSurveyResponseReaderTest {

    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssZZZ");

    @Test
    void same_employee_can_submit_response_multiple_time() {
        throw new RuntimeException("Not implemented yet");
    }

    @Test
    void email_can_be_omitted() {
        throw new RuntimeException("Not implemented yet");
    }

    @Test
    void answer_to_question_is_optional() {
        throw new RuntimeException("Not implemented yet");
    }


    @Test
    void reading_response_with_one_employee() {
        final RatingQuestion question1 = new RatingQuestion(Theme.Work, "I like the kind of work I do.");
        final RatingQuestion question2 = new RatingQuestion(Theme.Work, "In general, I have the resources (e.g., business tools, information, facilities, IT or functional support) I need to be effective.");
        final RatingQuestion question3 = new RatingQuestion(Theme.Place, "I feel empowered to get the work done for which I am responsible.");
        final Survey survey = new Survey()
                .addQuestion(question1)
                .addQuestion(question2)
                .addQuestion(question3);

        final String[] rows = new String[]{
                "employee1@abc.xyz,1,2014-07-28T20:35:41+00:00,5,4,3"
        };

        final List<EmployeeResponse> employeeSurveyRespons = new CsvSurveyResponseReader().readFrom(inputFrom(rows), survey);
        assertThat(employeeSurveyRespons).hasSize(1);

        final EmployeeResponse employeeResponse = employeeSurveyRespons.get(0);
        assertThat(employeeResponse.employee().id()).isEqualTo("1");
        assertThat(employeeResponse.employee().email()).isEqualTo("employee1@abc.xyz");
        assertThat(employeeResponse.submittedAt().isPresent());
        assertThat(employeeResponse.submittedAt().get().format(dateFormatter)).isEqualTo("2014-07-28 20:35:41+0000");

        assertTrue(employeeResponse.answerFor(question1).isPresent());
        assertThat(employeeResponse.answerFor(question1).get().rating()).isEqualTo(5);

        assertTrue(employeeResponse.answerFor(question2).isPresent());
        assertThat(employeeResponse.answerFor(question2).get().rating()).isEqualTo(4);

        assertTrue(employeeResponse.answerFor(question3).isPresent());
        assertThat(employeeResponse.answerFor(question3).get().rating()).isEqualTo(3);
    }

    @Test
    void reading_response_with_multiple_employee() {
        final RatingQuestion question1 = new RatingQuestion(Theme.Work, "I like the kind of work I do.");
        final RatingQuestion question2 = new RatingQuestion(Theme.Work, "In general, I have the resources (e.g., business tools, information, facilities, IT or functional support) I need to be effective.");
        final SingleSelectQuestion question3 = new SingleSelectQuestion(Theme.Demographic, "Manager");
        final Survey survey = new Survey()
                .addQuestion(question1)
                .addQuestion(question2)
                .addQuestion(question3);

        final String[] rows = new String[]{
                "employee1@abc.xyz,1,2014-07-28T20:35:41+00:00,5,4,John",
                "employee2@abc.xyz,2,2014-07-30T23:35:41+10:00,3,1,Sally"
        };

        final List<EmployeeResponse> employeeSurveyRespons = new CsvSurveyResponseReader().readFrom(inputFrom(rows), survey);
        assertThat(employeeSurveyRespons).hasSize(2);

        final EmployeeResponse firstEmployeeResponse = employeeSurveyRespons.get(0);
        assertThat(firstEmployeeResponse.employee().id()).isEqualTo("1");
        assertThat(firstEmployeeResponse.employee().email()).isEqualTo("employee1@abc.xyz");
        assertThat(firstEmployeeResponse.submittedAt().isPresent());
        assertThat(firstEmployeeResponse.submittedAt().get().format(dateFormatter)).isEqualTo("2014-07-28 20:35:41 +0000");

        assertTrue(firstEmployeeResponse.answerFor(question1).isPresent());
        assertThat(firstEmployeeResponse.answerFor(question1).get().rating()).isEqualTo(5);

        assertTrue(firstEmployeeResponse.answerFor(question2).isPresent());
        assertThat(firstEmployeeResponse.answerFor(question2).get().rating()).isEqualTo(4);

        assertTrue(firstEmployeeResponse.answerFor(question3).isPresent());
        assertThat(firstEmployeeResponse.answerFor(question3).get().selection()).isEqualTo("John");

        final EmployeeResponse secondEmployee = employeeSurveyRespons.get(1);
        assertThat(secondEmployee.employee().id()).isEqualTo("1");
        assertThat(secondEmployee.employee().email()).isEqualTo("employee2@abc.xyz");
        assertThat(secondEmployee.submittedAt().isPresent());
        assertThat(secondEmployee.submittedAt().get().format(dateFormatter)).isEqualTo("2014-07-30 23:35:41 +1000");

        assertTrue(secondEmployee.answerFor(question1).isPresent());
        assertThat(secondEmployee.answerFor(question1).get().rating()).isEqualTo(3);

        assertTrue(secondEmployee.answerFor(question2).isPresent());
        assertThat(secondEmployee.answerFor(question2).get().rating()).isEqualTo(1);

        assertTrue(secondEmployee.answerFor(question3).isPresent());
        assertThat(secondEmployee.answerFor(question3).get().selection()).isEqualTo("Sally");
    }


    @NotNull
    private InputStream inputFrom(String[] rows) {
        final String fileContent = Arrays.stream(rows).collect(Collectors.joining("\n"));
        return new ByteArrayInputStream(fileContent.getBytes());
    }

}