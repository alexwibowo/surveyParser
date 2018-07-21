package com.github.wibowo.survey.model.questionAnswer;

/**
 * A single question in a survey.
 */
public interface Question {
    Theme theme();

    String sentence();
}
