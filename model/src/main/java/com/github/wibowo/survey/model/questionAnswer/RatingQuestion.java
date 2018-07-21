package com.github.wibowo.survey.model.questionAnswer;

public final class RatingQuestion extends BaseQuestion<RatingAnswer> {

    public RatingQuestion(final Theme theme,
                          final String sentence) {
        super(theme, sentence);
    }

    @Override
    public Class<RatingAnswer> answerType() {
        return RatingAnswer.class;
    }
}
