package com.github.wibowo.survey.io.csv;

import com.github.wibowo.survey.io.SurveyResponseReader;
import com.github.wibowo.survey.io.SurveySummariser;
import com.github.wibowo.survey.model.*;
import com.github.wibowo.survey.model.questionAnswer.Question;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CsvSurveyResponseReader implements SurveyResponseReader<InputStream> {
    private static final Logger LOGGER = LogManager.getLogger(CsvSurveyResponseReader.class);

    // Alternatively: "yyyy-MM-dd'T'HH:mm:ssXXX"
    final DateTimeFormatter submittedTimeParser = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public SurveySummary readFrom(final InputStream source,
                                  final Survey survey) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(survey);
        LOGGER.info("Reading answer for survey [{}]", survey);

        final List<EmployeeResponse> employeeResponses = parseCSVFile(source, survey);
        return SurveySummariser.summarise(survey, employeeResponses);
    }

    List<EmployeeResponse> parseCSVFile(final InputStream source,
                                        final Survey survey) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(survey);

        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(source))) {
            return bufferedReader.lines()
                    .filter(line -> !StringUtils.isBlank(line))
                    .map(line -> processLine(line, survey))
                    .collect(Collectors.toList());
        } catch (final SurveyException exception) {
            throw exception;
        } catch (final Exception exception) {
            throw new SurveyException("An unexpected error has occurred", exception);
        }
    }


    private EmployeeResponse processLine(final @NotNull String line,
                                         final Survey survey) {
        final StringTokenizer stringTokenizer = new StringTokenizer(line, ',', '"')
                .setIgnoreEmptyTokens(false)
                .setEmptyTokenAsNull(false);
        final String[] values = stringTokenizer.getTokenArray();
        final String email = values[0];
        final String employeeID = values[1];

        final Employee employee = new Employee(employeeID, email);
        final String submittedAtAsString = values[2];

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

        if (values.length > 3) {
            for (int questionOffset = 3; questionOffset < values.length; questionOffset++) {
                final int questionIndex = questionOffset - 3;
                final Question originalQuestion = survey.questionNumber(questionIndex);
                final String questionAnswer = values[questionOffset];

                employeeResponse.addAnswer(originalQuestion.createAnswerFrom(questionAnswer));
            }
        }


        return employeeResponse;
    }

}
