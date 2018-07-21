package com.github.wibowo.survey.model.questionAnswer;

import java.util.Objects;

public abstract class BaseAnswer<E extends Question> implements Answer<E>{

    protected final E question;

    protected BaseAnswer(final E question) {
        this.question = Objects.requireNonNull(question, "Answer must be constructed with a question.");
    }

    public E question() {
        return question;
    }
}
