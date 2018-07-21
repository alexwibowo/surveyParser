package com.github.wibowo.survey.model.questionAnswer;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public abstract class BaseQuestion<E extends Answer> implements Question<E> {
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

    @Override
    public String toString() {
        return "Question{" +
                "theme=" + theme +
                ", sentence='" + sentence + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final BaseQuestion that = (BaseQuestion) o;
        return theme == that.theme &&
                Objects.equals(sentence, that.sentence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theme, sentence);
    }
}
