package com.github.wibowo.survey.model.questionAnswer;

import static java.util.Objects.requireNonNull;

public abstract class BaseQuestion implements Question {
    protected final Theme theme;
    protected final String sentence;

    protected BaseQuestion(final Theme theme,
                           final String sentence) {
        this.theme = requireNonNull(theme);
        this.sentence = requireNonNull(sentence);
    }

    @Override
    public Theme theme() {
        return theme;
    }

    @Override
    public String sentence() {
        return sentence;
    }
}
