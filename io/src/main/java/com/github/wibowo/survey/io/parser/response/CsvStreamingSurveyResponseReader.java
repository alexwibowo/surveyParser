package com.github.wibowo.survey.io.parser.response;

import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;
import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.RatingAnswer;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
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

    /**
     * Mutable implementation of {@link SurveySummary}. It is used to accumulate the statistics at the same time
     * the survey response is being processed.
     */
    private static class UnsafeSurveyResponse implements SurveySummary {
        /**
         * Value to indicate that there is no response / no response required for a given question
         * <ul>
         *     <li>for rating question, it means no response was provided</li>
         *     <li>for singleselect question, it means no response is required</li>
         * </ul>
         */
        public static final int NULL_RATING_RESPONSE = Integer.MIN_VALUE;
        private long totalResponse;
        private long totalParticipation;

        private final int[] totalRatingForQuestions;
        private final int[] numberParticipationsByQuestion;
        private final Survey survey;

        private UnsafeSurveyResponse(final Survey survey) {
            final List<Question> collect = StreamSupport.stream(survey.questions().spliterator(), false).collect(Collectors.toList());
            this.survey = survey;
            totalRatingForQuestions = new int[collect.size()];
            numberParticipationsByQuestion = new int[collect.size()];
            Arrays.fill(totalRatingForQuestions, NULL_RATING_RESPONSE);
            Arrays.fill(numberParticipationsByQuestion, NULL_RATING_RESPONSE);
        }

        private void addResponse(){
            totalResponse++;
        }

        private void addParticipation(){
            totalParticipation++;
        }

        private void addRatingForQuestion(final RatingQuestion question,
                                         final RatingAnswer ratingAnswer) {
            final int questionIndex = survey.indexForQuestion(question);
            final int fastQuestionIndex = question.questionIndex();
            int currentTotalRatingForQuestion = totalRatingForQuestions[questionIndex] == NULL_RATING_RESPONSE ? 0 : totalRatingForQuestions[questionIndex];
            int currentNumberParticipantsForQuestion = numberParticipationsByQuestion[questionIndex] == NULL_RATING_RESPONSE ? 0 : numberParticipationsByQuestion[questionIndex];
            if (!ratingAnswer.isNull()) {
                totalRatingForQuestions[questionIndex] = currentTotalRatingForQuestion + ratingAnswer.rating();
                numberParticipationsByQuestion[questionIndex] = currentNumberParticipantsForQuestion + 1;
            }
        }

        @Override
        public double participationPercentage() {
            return ((double) totalParticipation) / totalResponse;
        }

        @Override
        public long totalParticipation(){
            return totalParticipation;
        }

        @Override
        public double averageRatingFor(final RatingQuestion ratingQuestion) {
            final int fastQuestionIndex = ratingQuestion.questionIndex();
            final int questionIndex = survey.indexForQuestion(ratingQuestion);
            final int totalRatings = totalRatingForQuestions[questionIndex];
            final int numberOfParticipations = numberParticipationsByQuestion[questionIndex];
            if (numberOfParticipations == NULL_RATING_RESPONSE) {
                return Double.NaN;
            }
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
