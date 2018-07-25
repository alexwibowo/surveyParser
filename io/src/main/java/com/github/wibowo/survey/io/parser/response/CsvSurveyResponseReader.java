package com.github.wibowo.survey.io.parser.response;

import com.github.wibowo.survey.io.SurveySummariser;
import com.github.wibowo.survey.model.Employee;
import com.github.wibowo.survey.model.EmployeeResponse;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;
import com.github.wibowo.survey.model.questionAnswer.Question;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Survey response parser that keeps a complete metadata of each survey response in memory.
 * The downside of using this parser is that, as expected, memory hungry.
 */
public final class CsvSurveyResponseReader extends BaseCsvSurveyResponseReader {
    private static final Logger LOGGER = LogManager.getLogger(CsvSurveyResponseReader.class);

    // Alternatively: "yyyy-MM-dd'T'HH:mm:ssXXX"
    final DateTimeFormatter submittedTimeParser = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private List<EmployeeResponse> employeeResponses;
    private EmployeeResponse employeeResponse;

    public CsvSurveyResponseReader(final Survey survey) {
        super(survey);
        this.employeeResponses = new ArrayList<>();
    }

    public SurveySummary getSummary(){
        return SurveySummariser.summarise(survey, employeeResponses);
    }

    List<EmployeeResponse> employeeResponses() {
        return employeeResponses;
    }

    protected void onNewResponse(final Survey survey,
                                 final String employeeEmail,
                                 final String employeeID,
                                 final String submittedAtAsString) {
        LOGGER.debug("Processing response from [{}:{}] submitted at [{}]", employeeID, employeeEmail, submittedAtAsString);
        ZonedDateTime submittedAt = null;
        if (!StringUtils.isBlank(submittedAtAsString)) {
            submittedAt = ZonedDateTime.parse(submittedAtAsString, submittedTimeParser);
        }
        final Employee employee = new Employee(employeeID, employeeEmail);
        if (submittedAt != null) {
            employeeResponse = EmployeeResponse.submittedResponse(survey, employee, submittedAt);
        } else {
            employeeResponse = EmployeeResponse.unsubmittedResponse(survey, employee);
        }
        employeeResponses.add(employeeResponse);
    }

    @Override
    protected void onNewAnswerForQuestion(final String submittedAt,
                                          final Question question,
                                          final String answer) {
        LOGGER.trace("Processing answer for question [{}] : [{}]", question, answer);
        employeeResponse.addAnswer(question.createAnswerFrom(answer));
    }

}
