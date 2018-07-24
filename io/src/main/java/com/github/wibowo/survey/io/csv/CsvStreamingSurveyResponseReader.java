package com.github.wibowo.survey.io.csv;

import com.github.wibowo.survey.io.SurveyResponseReader;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveyException;
import com.github.wibowo.survey.model.SurveySummary;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class CsvStreamingSurveyResponseReader implements SurveyResponseReader<InputStream> {
    private static final Logger LOGGER = LogManager.getLogger(CsvStreamingSurveyResponseReader.class);

    @Override
    public SurveySummary readFrom(final InputStream source,
                                         final Survey survey) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(survey);
        LOGGER.info("Reading answer for survey [{}]", survey);

        final UnsafeSurveyResponse response = new UnsafeSurveyResponse();
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(source))) {
            bufferedReader.lines()
                    .filter(line -> !StringUtils.isBlank(line))
                    .forEach( line -> {
                        processLine(line, survey, response);
                    });
            return response;
        } catch (final SurveyException exception) {
            throw exception;
        } catch (final Exception exception) {
            throw new SurveyException("An unexpected error has occurred", exception);
        }
    }

    public static class UnsafeSurveyResponse implements SurveySummary {
        private long totalResponse;
        private long totalParticipation;

        private final Map<RatingQuestion, Integer> totalRatingForQuestions;
        private final Map<RatingQuestion, Integer> numberParticipationsByQuestion;

        public UnsafeSurveyResponse() {
            totalRatingForQuestions = new HashMap<>();
            numberParticipationsByQuestion = new HashMap<>();
        }

        public UnsafeSurveyResponse addResponse(){
            totalResponse++;
            return this;
        }

        public UnsafeSurveyResponse addParticipation(){
            totalParticipation++;
            return this;
        }

        public UnsafeSurveyResponse addRatingForQuestion(final RatingQuestion question,
                                                         final RatingAnswer ratingAnswer) {
            final Integer currentTotalRatingForQuestion = totalRatingForQuestions.computeIfAbsent(question, ignored -> 0);
            final Integer currentNumberParticipationsForQuestion = numberParticipationsByQuestion.computeIfAbsent(question, ignored -> 0);
            if (!ratingAnswer.isNull()) {
                totalRatingForQuestions.put(question, currentTotalRatingForQuestion + ratingAnswer.rating());
                numberParticipationsByQuestion.put(question, currentNumberParticipationsForQuestion + 1);
            }
            return this;
        }

        @Override
        public double getParticipationPercentage() {
            return ((double) totalParticipation) / totalResponse;
        }

        @Override
        public long getNumberOfParticipations(){
            return totalParticipation;
        }

        @Override
        public double averageRatingFor(final RatingQuestion ratingQuestion) {
            final Integer totalRatings = totalRatingForQuestions.getOrDefault(ratingQuestion, 0);
            final Integer numberOfParticipations = numberParticipationsByQuestion.get(ratingQuestion);
            if (numberOfParticipations == null) {
                return Double.NaN;
            }
            return ((double) totalRatings) / numberOfParticipations;
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
        final boolean isSubmitted = StringUtils.isNotBlank(submittedAtAsString);
        if (isSubmitted) {
            unsafeSurveyResponse.addParticipation();
            if (values.length > 3) {
                for (int questionOffset = 3; questionOffset < values.length; questionOffset++) {
                    final int questionIndex = questionOffset - 3;
                    final Question originalQuestion = survey.questionNumber(questionIndex);
                    if (originalQuestion instanceof RatingQuestion) {
                        final RatingQuestion ratingQuestion = (RatingQuestion) originalQuestion;
                        final String questionAnswer = values[questionOffset];
                        final RatingAnswer answer = ratingQuestion.createAnswerFrom(questionAnswer);
                        unsafeSurveyResponse.addRatingForQuestion(ratingQuestion, answer);
                    }
                }
            }
        }

    }

}
