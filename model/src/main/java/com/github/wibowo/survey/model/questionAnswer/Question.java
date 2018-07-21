package com.github.wibowo.survey.model.questionAnswer;

/**
 * A single question in a survey.
 */
public interface Question<T extends Answer> {
    Theme theme();

    String sentence();

    Class<T> answerType();
}
