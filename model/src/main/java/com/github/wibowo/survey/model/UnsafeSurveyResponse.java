package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.RatingAnswer;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;

import java.util.HashMap;
import java.util.Map;

public class UnsafeSurveyResponse implements SurveySummary {
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
