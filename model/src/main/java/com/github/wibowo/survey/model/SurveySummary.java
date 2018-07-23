package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;

public interface SurveySummary {
    double getParticipationPercentage();

    long getNumberOfParticipations();

    double averageRatingFor(RatingQuestion ratingQuestion);
}
