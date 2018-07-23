package com.github.wibowo.survey.io.csv;

import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveyException;
import com.github.wibowo.survey.model.SurveySummary;
import com.github.wibowo.survey.model.UnsafeSurveyResponse;
import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.RatingAnswer;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public final class CsvStreamingSurveyResponseReader {
    private static final Logger LOGGER = LogManager.getLogger(CsvStreamingSurveyResponseReader.class);

    public SurveySummary readFrom(final InputStream source,
                                  final Survey survey) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(survey);
        LOGGER.info("Reading answer for survey [{}]", survey);

        final UnsafeSurveyResponse response = new UnsafeSurveyResponse();
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(source))) {
            bufferedReader.lines()
                    .filter(line -> !StringUtils.isBlank(line))
                    .forEach(line -> processLine(line, survey, response));
            return response;
        } catch (final SurveyException exception) {
            throw exception;
        } catch (final Exception exception) {
            throw new SurveyException("An unexpected error has occurred", exception);
        }
    }


    private void processLine(final @NotNull String line,
                             final Survey survey,
                             final UnsafeSurveyResponse unsafeSurveyResponse) {
        final StringTokenizer stringTokenizer = new StringTokenizer(line, ',', '"')
                .setIgnoreEmptyTokens(false)
                .setEmptyTokenAsNull(false);
        final String[] values = stringTokenizer.getTokenArray();
        final String submittedAtAsString = values[2];

        unsafeSurveyResponse.addResponse();
        if (isNotBlank(submittedAtAsString)) {
            unsafeSurveyResponse.addParticipation();
            if (values.length > 3) {
                for (int questionOffset = 3; questionOffset < values.length; questionOffset++) {
                    final int questionIndex = questionOffset - 3;
                    final Question originalQuestion = survey.questionNumber(questionIndex);
                    if (originalQuestion instanceof RatingQuestion) {
                        processRatingQuestionAnswer(unsafeSurveyResponse, values[questionOffset], (RatingQuestion) originalQuestion);
                    }
                }
            }
        }
    }

    private void processRatingQuestionAnswer(final UnsafeSurveyResponse unsafeSurveyResponse,
                                             final String questionAnswer,
                                             final RatingQuestion ratingQuestion) {
        final RatingAnswer answer = ratingQuestion.createAnswerFrom(questionAnswer);
        unsafeSurveyResponse.addRatingForQuestion(ratingQuestion, answer);
    }

}
