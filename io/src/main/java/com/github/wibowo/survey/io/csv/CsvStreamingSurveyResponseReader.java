package com.github.wibowo.survey.io.csv;

import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;
import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.RatingAnswer;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Memory friendly survey response parser. It does not keep all responses in memory, only the accumulating response.
 */
public final class CsvStreamingSurveyResponseReader extends BaseCsvSurveyResponseReader {
    private static final Logger LOGGER = LogManager.getLogger(CsvStreamingSurveyResponseReader.class);

    private final UnsafeSurveyResponse response;

    public CsvStreamingSurveyResponseReader(final Survey survey) {
        super(survey);
        this.response = new UnsafeSurveyResponse(survey);
    }

    @Override
    public SurveySummary getSummary(){
        return response;
    }

    private static class UnsafeSurveyResponse implements SurveySummary {
        private long totalResponse;
        private long totalParticipation;

        private final int[] totalRatingForQuestions;
        private final int[] numberParticipationsByQuestion;
//        private final Map<RatingQuestion, Integer> totalRatingForQuestions;
//        private final Map<RatingQuestion, Integer> numberParticipationsByQuestion;

        private UnsafeSurveyResponse(final Survey survey) {
            final List<Question> collect = StreamSupport.stream(survey.questions().spliterator(), false).collect(Collectors.toList());
            totalRatingForQuestions = new int[collect.size()];
            numberParticipationsByQuestion = new int[collect.size()];
            Arrays.fill(totalRatingForQuestions, Integer.MIN_VALUE);
            Arrays.fill(numberParticipationsByQuestion, Integer.MIN_VALUE);
//            totalRatingForQuestions = new HashMap<>();
//            numberParticipationsByQuestion = new HashMap<>();
        }

        private void addResponse(){
            totalResponse++;
        }

        private void addParticipation(){
            totalParticipation++;
        }

        private void addRatingForQuestion(final RatingQuestion question,
                                         final RatingAnswer ratingAnswer) {
//            final Integer currentTotalRatingForQuestion = totalRatingForQuestions.computeIfAbsent(question, ignored -> 0);
//            final Integer currentNumberParticipationsForQuestion = numberParticipationsByQuestion.computeIfAbsent(question, ignored -> 0);
            int currentTotalRatingForQuestion = totalRatingForQuestions[question.questionIndex()] == Integer.MIN_VALUE ? 0 : totalRatingForQuestions[question.questionIndex()];
            int currentNumberParticipantsForQuestion = numberParticipationsByQuestion[question.questionIndex()] == Integer.MIN_VALUE ? 0 : numberParticipationsByQuestion[question.questionIndex()];
            if (!ratingAnswer.isNull()) {
                totalRatingForQuestions[question.questionIndex()] = currentTotalRatingForQuestion + ratingAnswer.rating();
                numberParticipationsByQuestion[question.questionIndex()] = currentNumberParticipantsForQuestion + 1;

//                        totalRatingForQuestions.put(question, currentTotalRatingForQuestion + ratingAnswer.rating());
//                numberParticipationsByQuestion.put(question, currentNumberParticipationsForQuestion + 1);
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
//            final Integer totalRatings = totalRatingForQuestions.getOrDefault(ratingQuestion, 0);
            final int totalRatings = totalRatingForQuestions[ratingQuestion.questionIndex()];
            final int numberOfParticipations = numberParticipationsByQuestion[ratingQuestion.questionIndex()];
            if (numberOfParticipations == Integer.MIN_VALUE) {
                return Double.NaN;
            }
//            if (numberOfParticipations == null) {
//                return Double.NaN;
//            }
            return ((double) totalRatings) / numberOfParticipations;
        }
    }

    protected void onNewResponse(final Survey survey,
                                 final String employeeEmail,
                                 final String employeeID,
                                 final String submittedAt) {
        LOGGER.debug("Processing response from [{}:{}] submitted at [{}]", employeeID, employeeEmail, submittedAt);
        response.addResponse();
        if (StringUtils.isNotBlank(submittedAt)) {
            response.addParticipation();
        }
    }

    @Override
    protected void onNewAnswerForQuestion(final String submittedAt,
                                          final Question question,
                                          final String answerAsString) {
        LOGGER.trace("Processing answer for question [{}], with answer [{}]", question, answerAsString);
        if (StringUtils.isNotBlank(submittedAt)) {
            if (question instanceof RatingQuestion) {
                final RatingQuestion ratingQuestion = (RatingQuestion) question;
                final RatingAnswer answer = ratingQuestion.createAnswerFrom(answerAsString);
                response.addRatingForQuestion(ratingQuestion, answer);
            }
        }
    }

}
