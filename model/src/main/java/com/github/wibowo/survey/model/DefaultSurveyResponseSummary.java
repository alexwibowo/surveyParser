package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DefaultSurveyResponseSummary implements SurveySummary{

    private final Survey survey;
    private final List<EmployeeResponse> submittedResponses;

    private final Map<RatingQuestion, Double> ratingAverageByQuestion;

    private double participationPercentage;

    private long numberOfParticipations;

    public DefaultSurveyResponseSummary(final Survey survey,
                                        final List<EmployeeResponse> submittedResponses) {
        this.survey = survey;
        this.submittedResponses = submittedResponses;
        this.ratingAverageByQuestion = new HashMap<>();
    }

    public Survey getSurvey() {
        return survey;
    }

    public List<EmployeeResponse> getSubmittedResponses() {
        return Collections.unmodifiableList(submittedResponses);
    }

    @Override
    public double participationPercentage() {
        return participationPercentage;
    }

    public void setParticipationPercentage(double participationPercentage) {
        this.participationPercentage = participationPercentage;
    }

    @Override
    public long totalParticipation() {
        return numberOfParticipations;
    }

    public void setNumberOfParticipations(long numberOfParticipations) {
        this.numberOfParticipations = numberOfParticipations;
    }

    public void addRatingQuestionAverage(final RatingQuestion ratingQuestion,
                                         final double average) {
        ratingAverageByQuestion.put(ratingQuestion, average);
    }

    @Override
    public double averageRatingFor(final RatingQuestion ratingQuestion) {
        return ratingAverageByQuestion.get(ratingQuestion);
    }
}
