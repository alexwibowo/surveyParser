package com.github.wibowo.survey.model.questionAnswer;

public final class AnswerWithWrongQuestionType extends BaseAnswer<RatingQuestion> {
    private final String answerValue;

    public AnswerWithWrongQuestionType(final RatingQuestion question,
                                       final String answerValue) {
        super(question);
        this.answerValue = answerValue;
    }


    @Override
    public boolean isNull() {
        return false;
    }
}
