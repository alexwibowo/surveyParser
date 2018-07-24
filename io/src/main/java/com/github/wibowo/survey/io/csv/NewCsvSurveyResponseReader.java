package com.github.wibowo.survey.io.csv;

import com.github.wibowo.survey.io.SurveySummariser;
import com.github.wibowo.survey.model.Employee;
import com.github.wibowo.survey.model.EmployeeResponse;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;
import com.github.wibowo.survey.model.questionAnswer.Question;
import org.apache.commons.lang3.StringUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class NewCsvSurveyResponseReader extends BaseCsvSurveyResponseReader{
    // Alternatively: "yyyy-MM-dd'T'HH:mm:ssXXX"
    final DateTimeFormatter submittedTimeParser = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final List<EmployeeResponse> employeeResponses;

    public NewCsvSurveyResponseReader(final Survey survey) {
        super(survey);
        employeeResponses = new ArrayList<>();
    }

    @Override
    public SurveySummary getSummary() {
        return SurveySummariser.summarise(survey, employeeResponses);
    }

    @Override
    protected void onNewParticipation(final Survey survey,
                                      final String email,
                                      final String employeeID,
                                      final String submittedAtAsString,
                                      final Question originalQuestion,
                                      final String questionAnswer) {
        final Employee employee = new Employee(employeeID, email);
        ZonedDateTime submittedAt = null;
        if (!StringUtils.isBlank(submittedAtAsString)) {
            submittedAt = ZonedDateTime.parse(submittedAtAsString, submittedTimeParser);
        }

        final EmployeeResponse employeeResponse;
        if (submittedAt != null) {
            employeeResponse = EmployeeResponse.submittedResponse(survey, employee, submittedAt);
        } else {
            employeeResponse = EmployeeResponse.unsubmittedResponse(survey, employee);
        }
        employeeResponse.addAnswer(originalQuestion.createAnswerFrom(questionAnswer));
        employeeResponses.add(employeeResponse);
    }
}
