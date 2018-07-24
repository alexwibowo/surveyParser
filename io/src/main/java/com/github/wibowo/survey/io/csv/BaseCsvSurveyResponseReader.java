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
import java.util.Objects;

public abstract class BaseCsvSurveyResponseReader implements SurveyResponseReader<InputStream> {
    private static final Logger LOGGER = LogManager.getLogger(BaseCsvSurveyResponseReader.class);

    protected final Survey survey;

    protected BaseCsvSurveyResponseReader(Survey survey) {
        this.survey = survey;
    }

    @Override
    public SurveyResponseReader process(final InputStream source) {
        Objects.requireNonNull(source);
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
        final StringTokenizer stringTokenizer = new StringTokenizer(line, ',', '"')
                .setIgnoreEmptyTokens(false)
                .setEmptyTokenAsNull(false);
        final String[] values = stringTokenizer.getTokenArray();
        final String email = values[0];
        final String employeeID = values[1];
        final String submittedAtAsString = values[2];

        onNewResponse();
        if (values.length > 3) {
            for (int questionOffset = 3; questionOffset < values.length; questionOffset++) {
                final int questionIndex = questionOffset - 3;
                final Question originalQuestion = survey.questionNumber(questionIndex);
                final String questionAnswer = values[questionOffset];
                onNewParticipation(
                        survey,
                        email,
                        employeeID,
                        submittedAtAsString,
                        originalQuestion,
                        questionAnswer
                );
            }
        }
    }


    protected void onNewResponse() {
    }

    protected void onNewParticipation(final Survey survey,
                                      final String email,
                                      final String employeeID,
                                      final String submittedAtAsString,
                                      final Question originalQuestion,
                                      final String questionAnswer){
    }

}
