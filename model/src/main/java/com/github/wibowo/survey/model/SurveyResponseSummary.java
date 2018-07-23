package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;

import java.util.HashMap;
import java.util.Map;

public final class SurveyResponseSummary {

    private final Survey survey;

    private final Map<RatingQuestion, Double> ratingAverageByQuestion;

    private double participationPercentage;

    private long numberOfParticipations;

    public SurveyResponseSummary(final Survey survey) {
        this.survey = survey;
        this.ratingAverageByQuestion = new HashMap<>();
    }

    public double getParticipationPercentage() {
        return participationPercentage;
    }

    public void setParticipationPercentage(double participationPercentage) {
        this.participationPercentage = participationPercentage;
    }

    public long getNumberOfParticipations() {
        return numberOfParticipations;
    }

    public void setNumberOfParticipations(long numberOfParticipations) {
        this.numberOfParticipations = numberOfParticipations;
    }

    public void addRatingQuestionAverage(final RatingQuestion ratingQuestion,
                                         final double average) {
        ratingAverageByQuestion.put(ratingQuestion, average);
    }

    public double averageRatingFor(final RatingQuestion ratingQuestion) {
        return ratingAverageByQuestion.get(ratingQuestion);
    }
}
