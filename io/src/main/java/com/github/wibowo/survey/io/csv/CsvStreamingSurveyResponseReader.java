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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class CsvStreamingSurveyResponseReader implements SurveyResponseReader<InputStream> {
    private static final Logger LOGGER = LogManager.getLogger(CsvStreamingSurveyResponseReader.class);

    private final Survey survey;
    private final UnsafeSurveyResponse response;

    public CsvStreamingSurveyResponseReader(final Survey survey) {
        this.survey = survey;
        this.response = new UnsafeSurveyResponse();
    }

    @Override
    public CsvStreamingSurveyResponseReader process(final InputStream source) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(survey);
        LOGGER.info("Reading answer for survey [{}]", survey);

        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(source))) {
            bufferedReader.lines()
                    .filter(line -> !StringUtils.isBlank(line))
                    .forEach( line -> processLine(line, survey, response));
            return this;
        } catch (final SurveyException exception) {
            throw exception;
        } catch (final Exception exception) {
            throw new SurveyException("An unexpected error has occurred", exception);
        }
    }

    @Override
    public SurveySummary getSummary(){
        return response;
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

        public void addResponse(){
            totalResponse++;
        }

        public void addParticipation(){
            totalParticipation++;
        }

        public void addRatingForQuestion(final RatingQuestion question,
                                         final RatingAnswer ratingAnswer) {
            final Integer currentTotalRatingForQuestion = totalRatingForQuestions.computeIfAbsent(question, ignored -> 0);
            final Integer currentNumberParticipationsForQuestion = numberParticipationsByQuestion.computeIfAbsent(question, ignored -> 0);
            if (!ratingAnswer.isNull()) {
                totalRatingForQuestions.put(question, currentTotalRatingForQuestion + ratingAnswer.rating());
                numberParticipationsByQuestion.put(question, currentNumberParticipationsForQuestion + 1);
            }
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


    private void processLine(final String line,
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
                    final String questionAnswer = values[questionOffset];

                    if (originalQuestion instanceof RatingQuestion) {
                        final RatingQuestion ratingQuestion = (RatingQuestion) originalQuestion;
                        final RatingAnswer answer = ratingQuestion.createAnswerFrom(questionAnswer);
                        unsafeSurveyResponse.addRatingForQuestion(ratingQuestion, answer);
                    }
                }
            }
        }

    }

}
