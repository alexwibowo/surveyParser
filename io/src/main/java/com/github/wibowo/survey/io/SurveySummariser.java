package com.github.wibowo.survey.io;

import com.github.wibowo.survey.model.EmployeeResponse;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveyResponseSummary;
import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.RatingAnswer;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;

import java.util.List;
import java.util.stream.Collectors;

public final class SurveySummariser {

    public static SurveyResponseSummary summarise(final Survey survey,
                                                  final List<EmployeeResponse> employeeResponses) {
        final int totalResponses = employeeResponses.size();

        final List<EmployeeResponse> submittedResponses = employeeResponses.stream()
                .filter(EmployeeResponse::wasSubmitted)
                .collect(Collectors.toList());

        final SurveyResponseSummary surveyResponseSummary = new SurveyResponseSummary(survey);
        surveyResponseSummary.setParticipation(submittedResponses.size() * 100 / totalResponses);

        for (final Question question : survey.questions()) {
            if (question instanceof RatingQuestion) {
                final RatingQuestion ratingQuestion = (RatingQuestion) question;
                final Double average = submittedResponses.stream()
                        .map(response -> response.answerFor(ratingQuestion))
                        .collect(Collectors.averagingDouble(RatingAnswer::rating));
                surveyResponseSummary.addRatingQuestionAverage(ratingQuestion, average);
            }
        }
        return surveyResponseSummary;
    }
}
