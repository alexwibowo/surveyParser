package com.github.wibowo.survey.model.questionAnswer;

import com.github.wibowo.survey.model.SurveyException;

import java.util.Objects;

public final class QuestionFactory {

    private QuestionFactory() {
    }

    public static Question createFrom(int questionIndex,
                                      final Theme theme,
                                      final String type,
                                      final String text) {
        switch (Objects.requireNonNull(type, "question type must be provided")) {
            case "ratingquestion":
                return new RatingQuestion(questionIndex, theme, text);
            case "singleselect":
                return new SingleSelectQuestion(questionIndex, theme, text);
            default:
                throw SurveyException.unsupportedQuestionType(type);
        }
    }
}
