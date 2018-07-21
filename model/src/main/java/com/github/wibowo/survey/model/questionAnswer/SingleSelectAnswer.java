package com.github.wibowo.survey.model.questionAnswer;

import java.util.Objects;

public final class SingleSelectAnswer extends BaseAnswer<SingleSelectQuestion> {

    private final String selection;

    public SingleSelectAnswer(final SingleSelectQuestion question,
                              final String selection) {
        super(question);
        this.selection = Objects.requireNonNull(selection, "Must provide selection");
    }

    public String selection() {
        return selection;
    }

    @Override
    public String toString() {
        return "SingleSelectAnswer{" +
                "question='" + question + '\'' +
                ", selection=" + selection +
                '}';
    }
}
