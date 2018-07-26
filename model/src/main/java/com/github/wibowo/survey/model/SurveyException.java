package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.QuestionType;
import com.github.wibowo.survey.model.questionAnswer.Theme;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SurveyException extends RuntimeException {

    public SurveyException(final String message) {
        super(message);
    }

    public SurveyException(final String message,
                           final Throwable cause) {
        super(message, cause);
    }

    public static SurveyException unsupportedQuestionType(final String question) {
        return new SurveyException(String.format("Unsupported question type [%s]. Supported values are [%s]",
                question, QuestionType.SUPPORTED_VALUES_AS_STRING));
    }

    public static SurveyException malformedFile(final String description) {
        return new SurveyException(String.format("Malformed survey file: %s", description));
    }

    public static SurveyException duplicatedQuestion(final Question question) {
        return new SurveyException(String.format("Question %s already exists in the survey", question));
    }

    public static SurveyException unsupportedMetadataKey(final String key) {
        return new SurveyException(String.format("Malformed survey file: Unsupported metadata key '%s'", key));
    }

    public static SurveyException unsupportedTheme(String themeAsString) {
        return new SurveyException(String.format("Unsupported theme [%s]. Supported values are [%s]",
                themeAsString, Theme.SUPPORTED_VALUES_AS_STRING));
    }
}
