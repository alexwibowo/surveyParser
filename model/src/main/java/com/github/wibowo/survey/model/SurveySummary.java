package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;

public interface SurveySummary {
    /**
     * @return participation percentage, where a user is considered to have participated when
     * he/she has submitted the response. i.e. when <code>submitted_at</code> is present
     * in the answer.
     */
    double participationPercentage();

    /**
     * @return total number of participation
     */
    long totalParticipation();

    /**
     * @param ratingQuestion {@link RatingQuestion} to get average for
     * @return average score for a given {@link RatingQuestion}. Note:
     * <ul>
     *     <li>Only submitted response is being considered</li>
     *     <li>Blank value is not being considered</li>
     * </ul>
     */
    double averageRatingFor(RatingQuestion ratingQuestion);
}
