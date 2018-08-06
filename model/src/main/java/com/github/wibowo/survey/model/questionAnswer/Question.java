package com.github.wibowo.survey.model.questionAnswer;

import org.jetbrains.annotations.NotNull;

/**
 * A single question in a survey.
 */
public interface Question<T extends Answer> {
    Theme theme();

    String sentence();

    Class<T> answerType();

    T createAnswerFrom(@NotNull String stringValue);

    @NotNull T nullAnswer();
}
