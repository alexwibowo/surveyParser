package com.github.wibowo.survey.model.questionAnswer;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public abstract class BaseQuestion<E extends Answer> implements Question<E> {
    protected final Theme theme;
    protected final String sentence;
    protected final int questionIndex;

    protected BaseQuestion(final int questionIndex,
                           final Theme theme,
                           final String sentence) {
        this.questionIndex = questionIndex;
        this.theme = requireNonNull(theme);
        this.sentence = requireNonNull(sentence);
    }

    @Override
    public final Theme theme() {
        return theme;
    }

    @Override
    public final String sentence() {
        return sentence;
    }

    @Override
    public String toString() {
        return "BaseQuestion{" +
                "theme=" + theme +
                ", sentence='" + sentence + '\'' +
                ", questionIndex=" + questionIndex +
                '}';
    }

    public final int questionIndex() {
        return questionIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseQuestion<?> that = (BaseQuestion<?>) o;
        return questionIndex == that.questionIndex &&
                theme == that.theme &&
                Objects.equals(sentence, that.sentence);
    }

    @Override
    public int hashCode() {

        return Objects.hash(theme, sentence, questionIndex);
    }
}
