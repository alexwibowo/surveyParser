package com.github.wibowo.survey.io.csv;

import com.github.wibowo.survey.io.SurveyResponseReader;
import com.github.wibowo.survey.model.Employee;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveyException;
import com.github.wibowo.survey.model.EmployeeResponse;
import com.github.wibowo.survey.model.questionAnswer.Question;
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

    private final String COMMA_SEPARATED_SPLITTER = "\\s*,\\s*";

    // Alternatively: "yyyy-MM-dd'T'HH:mm:ssXXX"
    final DateTimeFormatter submittedTimeParser = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public List<EmployeeResponse> readFrom(final InputStream source,
                                           final Survey survey) {
        Objects.requireNonNull(survey);

        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(source))) {
            return bufferedReader.lines()
                    .filter(line -> line != null && !line.trim().isEmpty())
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
        final String[] values = line.split(COMMA_SEPARATED_SPLITTER);
        final String email = values[0];
        final String employeeID = values[1];

        final Employee employee = new Employee(employeeID, email);
        final String submittedAtAsString = values[2];

        ZonedDateTime submittedAt = null;
        if (submittedAtAsString != null && submittedAtAsString.trim().length() > 0) {
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
                final String questionAnswer = values[questionOffset];
                final int questionIndex = questionOffset - 3;

                final Question originalQuestion = survey.questionNumber(questionIndex);
                final Class aClass = originalQuestion.answerType();

            }
        }


        return employeeResponse;
    }

}
