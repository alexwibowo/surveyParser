package com.github.wibowo.survey.model.questionAnswer;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;


public final class RatingQuestion extends BaseQuestion<RatingAnswer> {
    private static final Logger LOGGER = LogManager.getLogger(RatingQuestion.class);

    public RatingQuestion(final Theme theme,
                          final String sentence) {
        super(theme, sentence);
    }

    @Override
    public Class<RatingAnswer> answerType() {
        return RatingAnswer.class;
    }

    @Override
    public RatingAnswer createAnswerFrom(final String stringValue) {
        if (StringUtils.isBlank(stringValue)) {
            return nullAnswer();
        }

        try {
            return RatingAnswer.createAnswer(this, Integer.parseInt(stringValue));
        } catch (final Exception e) {
            LOGGER.warn("Unable to parse rating [{}] for question [{}]. This answer will be treated as not present.",
                    stringValue, this, e);
            return nullAnswer();
        }
    }

    @NotNull
    public RatingAnswer nullAnswer() {
        return RatingAnswer.nullAnswer(this);
    }



}
