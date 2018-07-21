package com.github.wibowo.survey.model.questionAnswer;

import java.util.Objects;

public abstract class BaseAnswer<E extends Question> implements Answer<E>{

    protected final E question;

    protected BaseAnswer(final E question) {
        this.question = Objects.requireNonNull(question, "Answer must be constructed with a question.");
        if (!this.getClass().isAssignableFrom(question.answerType())) {
            throw new IllegalStateException(
                    String.format("Attempt to create answer of type [%s] using question of type [%s]. Expected answer is of type [%s]. Looks like there is a programming error! Please contact the developer.",
                            this.getClass().getCanonicalName(),
                            question.getClass().getCanonicalName(),
                            question.answerType().getCanonicalName()
                            ));
        }
    }

    public E question() {
        return question;
    }
}
