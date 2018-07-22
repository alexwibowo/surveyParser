package com.github.wibowo.survey.model.questionAnswer;

import org.jetbrains.annotations.NotNull;

public final class SingleSelectQuestion extends BaseQuestion<SingleSelectAnswer> {

    public SingleSelectQuestion(final Theme theme,
                                final String sentence) {
        super(theme, sentence);
    }

    @Override
    public Class<SingleSelectAnswer> answerType() {
        return SingleSelectAnswer.class;
    }

    @Override
    public SingleSelectAnswer createAnswerFrom(final String stringValue) {
        if (stringValue == null || stringValue.trim().isEmpty()) {
            return nullAnswer();
        } else {
            return SingleSelectAnswer.createAnswer(this, stringValue);
        }
    }

    @Override
    @NotNull
    public SingleSelectAnswer nullAnswer() {
        return SingleSelectAnswer.nullAnswer(this);
    }

}
