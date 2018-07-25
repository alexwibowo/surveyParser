package com.github.wibowo.survey.io.csv;

import com.github.wibowo.survey.io.SurveyResponseReader;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveyException;
import com.github.wibowo.survey.model.SurveySummary;
import com.github.wibowo.survey.model.questionAnswer.Question;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.util.Objects.requireNonNull;

public abstract class BaseCsvSurveyResponseReader implements SurveyResponseReader<InputStream> {
    private static final Logger LOGGER = LogManager.getLogger(BaseCsvSurveyResponseReader.class);
    private static final char DELIMITER_CHARACTER = ',';
    private static final char ESCAPE_CHARACTER = '"';

    protected final Survey survey;

    BaseCsvSurveyResponseReader(final Survey survey) {
        this.survey = requireNonNull(survey);
    }

    @Override
    public SurveyResponseReader process(final InputStream source) {
        requireNonNull(source);
        LOGGER.info("Processing survey response from input source");
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(source))) {
            bufferedReader.lines()
                    .filter(line -> !StringUtils.isBlank(line))
                    .forEach(this::processLine);
            return this;
        } catch (final SurveyException exception) {
            throw exception;
        } catch (final Exception exception) {
            throw new SurveyException("An unexpected error has occurred", exception);
        }
    }

    @Override
    public abstract SurveySummary getSummary();

    private void processLine(final String line) {
        final StringTokenizer stringTokenizer = new StringTokenizer(line, DELIMITER_CHARACTER, ESCAPE_CHARACTER)
                .setIgnoreEmptyTokens(false)
                .setEmptyTokenAsNull(false);
        final String[] values = stringTokenizer.getTokenArray();
        final String email = values[0];
        final String employeeID = values[1];
        final String submittedAt = values[2];

        if (values.length > 3) {
            onNewResponse(survey, email, employeeID, submittedAt);
            for (int questionOffset = 3; questionOffset < values.length; questionOffset++) {
                final int questionIndex = questionOffset - 3;
                final Question originalQuestion = survey.questionNumber(questionIndex);
                final String questionAnswer = values[questionOffset];
                onNewAnswerForQuestion(
                        submittedAt,
                        originalQuestion,
                        questionAnswer
                );
            }
        }
    }

    /**
     * Called once per response from an employee.
     *
     * @param survey the survey that the response was intended for
     * @param employeeEmail email of the employee
     * @param employeeID ID of the employee
     * @param submittedAt submission date
     */
    protected void onNewResponse(final Survey survey,
                                 final String employeeEmail,
                                 final String employeeID,
                                 final String submittedAt) {
    }

    /**
     * Called for every answer to a question from an employee
     *
     * @param submittedAt submission date of the survey
     * @param question original question
     * @param answer answer
     */
    protected void onNewAnswerForQuestion(final String submittedAt,
                                          final Question question,
                                          final String answer){
    }


}
