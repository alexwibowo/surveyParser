package com.github.wibowo.survey.io.csv;

import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;
import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.RatingAnswer;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import org.apache.commons.lang3.StringUtils;

public final class NewCsvStreamingSurveyResponseReader extends BaseCsvSurveyResponseReader {

    private final CsvStreamingSurveyResponseReader.UnsafeSurveyResponse response;

    public NewCsvStreamingSurveyResponseReader(final Survey survey) {
        super(survey);
        response = new CsvStreamingSurveyResponseReader.UnsafeSurveyResponse();
    }

    @Override
    public SurveySummary getSummary() {
        return response;
    }

    @Override
    protected void onNewResponse() {
        response.addResponse();
    }

    @Override
    protected void onNewParticipation(final Survey survey,
                                      final String email,
                                      final String employeeID,
                                      final String submittedAtAsString,
                                      final Question question,
                                      final String questionAnswer) {
        if (StringUtils.isNotBlank(submittedAtAsString)) {
            response.addParticipation();

            if (question instanceof RatingQuestion) {
                final RatingQuestion ratingQuestion = (RatingQuestion) question;
                final RatingAnswer answer = ratingQuestion.createAnswerFrom(questionAnswer);
                response.addRatingForQuestion(ratingQuestion, answer);
            }
        }
    }
}
