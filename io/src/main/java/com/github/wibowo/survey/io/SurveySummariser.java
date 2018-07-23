package com.github.wibowo.survey.io;

import com.github.wibowo.survey.model.EmployeeResponse;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.DefaultSurveyResponseSummary;
import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.RatingAnswer;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;

import java.util.List;
import java.util.stream.Collectors;

public final class SurveySummariser {

    public static DefaultSurveyResponseSummary summarise(final Survey survey,
                                                         final List<EmployeeResponse> employeeResponses) {
        final int totalResponses = employeeResponses.size();

        final List<EmployeeResponse> submittedResponses = employeeResponses.stream()
                .filter(EmployeeResponse::wasSubmitted)
                .collect(Collectors.toList());

        final DefaultSurveyResponseSummary defaultSurveyResponseSummary = new DefaultSurveyResponseSummary(survey);
        defaultSurveyResponseSummary.setParticipationPercentage( ((double)submittedResponses.size())  / totalResponses);
        defaultSurveyResponseSummary.setNumberOfParticipations(submittedResponses.size());

        for (final Question question : survey.questions()) {
            if (question instanceof RatingQuestion) {
                final RatingQuestion ratingQuestion = (RatingQuestion) question;

                final List<RatingAnswer> answersForQuestion = submittedResponses.stream()
                        .map(response -> response.answerFor(ratingQuestion))
                        .filter(answer -> !answer.isNull())
                        .collect(Collectors.toList());
                if (answersForQuestion.isEmpty()) {
                    defaultSurveyResponseSummary.addRatingQuestionAverage(ratingQuestion, Double.NaN);
                } else {
                    final Double average = answersForQuestion.stream()
                            .collect(Collectors.averagingDouble(RatingAnswer::rating));
                    defaultSurveyResponseSummary.addRatingQuestionAverage(ratingQuestion, average);
                }
            }
        }
        return defaultSurveyResponseSummary;
    }
}
