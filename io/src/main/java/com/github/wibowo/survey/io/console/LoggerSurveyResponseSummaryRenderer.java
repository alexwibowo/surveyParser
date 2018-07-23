package com.github.wibowo.survey.io.console;

import com.github.wibowo.survey.io.SurveyResponseSummaryRenderer;
import com.github.wibowo.survey.model.SurveyResponseSummary;
import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public final class LoggerSurveyResponseSummaryRenderer implements SurveyResponseSummaryRenderer {
    public static final Logger LOGGER = LogManager.getLogger(LoggerSurveyResponseSummaryRenderer.class);

    @Override
    public void render(final SurveyResponseSummary summary) {
        final NumberFormat percentFormat = DecimalFormat.getPercentInstance();
        LOGGER.info("=======================================");
        LOGGER.info("Participation percentage : {}", percentFormat.format(summary.getParticipationPercentage()));
        LOGGER.info("Total participation : {}", summary.getNumberOfParticipations());
        LOGGER.info("=======================================");
        final NumberFormat ratingFormat = new DecimalFormat("0.00");
        for (final Question question : summary.getQuestions()) {
            if (question instanceof RatingQuestion) {
                LOGGER.info("{} : {}",
                        question.sentence(),
                        ratingFormat.format(summary.averageRatingFor((RatingQuestion)question))
                );
            }
        }
    }
}
