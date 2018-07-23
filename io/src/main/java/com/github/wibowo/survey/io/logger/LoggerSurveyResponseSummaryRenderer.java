package com.github.wibowo.survey.io.logger;

import com.github.wibowo.survey.io.SurveyResponseSummaryRenderer;
import com.github.wibowo.survey.model.DefaultSurveyResponseSummary;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;
import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public final class LoggerSurveyResponseSummaryRenderer implements SurveyResponseSummaryRenderer {
    public static final Logger LOGGER = LogManager.getLogger(LoggerSurveyResponseSummaryRenderer.class);

    public void render(Survey survey, final SurveySummary summary) {
        final NumberFormat percentFormat = DecimalFormat.getPercentInstance();
        LOGGER.info("=======================================");
        LOGGER.info("Participation percentage : {}", percentFormat.format(summary.getParticipationPercentage()));
        LOGGER.info("Total participation : {}", summary.getNumberOfParticipations());
        LOGGER.info("=======================================");
        final NumberFormat ratingFormat = new DecimalFormat("0.00");
        for (final Question question : survey.questions()) {
            if (question instanceof RatingQuestion) {
                final double average = summary.averageRatingFor((RatingQuestion) question);
                LOGGER.info("{} : {}",
                        question.sentence(),
                        Double.isNaN(average) ? "N/A" : ratingFormat.format(average)
                );
            }
        }
    }
}
