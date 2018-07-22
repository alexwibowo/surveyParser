package com.github.wibowo.survey.model.questionAnswer;

import org.jetbrains.annotations.NotNull;

public final class QuestionFactory {

    private QuestionFactory() {
    }

    public static Question createFrom(final @NotNull Theme theme,
                                      final @NotNull String type,
                                      final @NotNull String text) {
        switch (type) {
            case "ratingquestion":
                return new RatingQuestion(theme, text);
            case "singleselect":
                return new SingleSelectQuestion(theme, text);
            default:
                throw new IllegalArgumentException(String.format("Unknown question type [%s].", type));
        }
    }
}
