package com.github.wibowo.survey.model;

public class SurveyException extends RuntimeException {

    public SurveyException(final String message) {
        super(message);
    }

    public SurveyException(final String message,
                           final Throwable cause) {
        super(message, cause);
    }

    public static SurveyException unsupportedQuestionType(final String question) {
        return new SurveyException(String.format("Unsupported question [%s]", question));
    }

    public static SurveyException malformedFile(final String description) {
        return new SurveyException(String.format("Malformed survey file: %s", description));
    }

}