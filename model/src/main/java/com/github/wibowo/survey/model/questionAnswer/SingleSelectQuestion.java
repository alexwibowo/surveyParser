package com.github.wibowo.survey.model.questionAnswer;

public final class SingleSelectQuestion extends BaseQuestion<SingleSelectAnswer> {

    public SingleSelectQuestion(final Theme theme,
                                final String sentence) {
        super(theme, sentence);
    }

    @Override
    public Class<SingleSelectAnswer> answerType() {
        return SingleSelectAnswer.class;
    }
}
