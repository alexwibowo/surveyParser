package com.github.wibowo.survey.model.questionAnswer;

import org.jetbrains.annotations.NotNull;

public final class MultiSelectQuestion extends BaseQuestion<MultiSelectAnswer> {
    MultiSelectQuestion(final Theme theme,
                        final String sentence) {
        super(theme, sentence);
    }

    @Override
    public Class<MultiSelectAnswer> answerType() {
        return MultiSelectAnswer.class;
    }

    @Override
    public MultiSelectAnswer createAnswerFrom(final String stringValue) {
        return MultiSelectAnswer.createAnswer(this, stringValue);
    }

    @NotNull
    @Override
    public MultiSelectAnswer nullAnswer() {
        return MultiSelectAnswer.nullAnswer(this);
    }
}
