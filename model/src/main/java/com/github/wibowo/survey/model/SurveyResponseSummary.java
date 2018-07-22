package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;

import java.util.HashMap;
import java.util.Map;

public final class SurveyResponseSummary {

    private final Survey survey;

    private final Map<RatingQuestion, Double> ratingAverageByQuestion;

    private double participation;

    public SurveyResponseSummary(final Survey survey) {
        this.survey = survey;
        this.ratingAverageByQuestion = new HashMap<>();
    }

    public double getParticipation() {
        return participation;
    }

    public void setParticipation(double participation) {
        this.participation = participation;
    }

    public void addRatingQuestionAverage(final RatingQuestion ratingQuestion,
                                         final double average) {
        ratingAverageByQuestion.put(ratingQuestion, average);
    }

    public double averageRatingFor(final RatingQuestion ratingQuestion) {
        return ratingAverageByQuestion.get(ratingQuestion);
    }
}
