package com.github.wibowo.survey.model.questionAnswer;

import java.util.Objects;

public final class SingleSelectAnswer extends BaseAnswer<SingleSelectQuestion> {

    private final String selection;
    private final boolean isNull;

    public SingleSelectAnswer(final SingleSelectQuestion question,
                              final String selection,
                              final boolean isNull) {
        super(question);
        this.selection = Objects.requireNonNull(selection, "Must provide selection");
        this.isNull = isNull;
    }

    public static SingleSelectAnswer createAnswer(final SingleSelectQuestion question,
                                                  final String selection) {
        return new SingleSelectAnswer(question, selection, false);
    }

    public static SingleSelectAnswer nullAnswer(final SingleSelectQuestion question) {
        return new SingleSelectAnswer(question, "", true);
    }

    public String selection() {
        return selection;
    }

    @Override
    public boolean isNull() {
        return isNull;
    }

    @Override
    public String toString() {
        return "SingleSelectAnswer{" +
                "question='" + question + '\'' +
                ", selection=" + selection +
                '}';
    }
}
