package com.github.wibowo.survey.model.questionAnswer;

import java.util.Arrays;
import java.util.Objects;

public final class MultiSelectAnswer extends BaseAnswer<MultiSelectQuestion>{
    public static final String[] NO_SELECTION = {};
    private final String[] selection;
    private boolean isNull;

    protected MultiSelectAnswer(final MultiSelectQuestion question,
                                final String[] selection,
                                final boolean isNull) {
        super(question);
        this.selection = Objects.requireNonNull(selection, "Must provide selection");
        this.isNull = isNull;
    }

    public static MultiSelectAnswer createAnswer(final MultiSelectQuestion question,
                                                 final String selection) {
        final String[] parts = selection.split("\\|");
        return new MultiSelectAnswer(question, parts, false);
    }

    public static MultiSelectAnswer nullAnswer(final MultiSelectQuestion question) {
        return new MultiSelectAnswer(question, NO_SELECTION, true);
    }

    public String[] getSelection() {
        return selection;
    }

    @Override
    public boolean isNull() {
        return isNull;
    }

    @Override
    public String toString() {
        return "MultiSelectAnswer{" +
                "selection=" + Arrays.toString(selection) +
                ", isNull=" + isNull +
                '}';
    }
}
