package com.github.wibowo.survey.io.renderer;

import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;
import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import com.github.wibowo.survey.model.questionAnswer.SingleSelectQuestion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

/**
 * Implementation of {@link SurveyResponseSummaryRenderer} that simply logs the result to {@link Logger}
 */
public final class LoggerSurveyResponseSummaryRenderer implements SurveyResponseSummaryRenderer {
    public static final Logger LOGGER = LogManager.getLogger(LoggerSurveyResponseSummaryRenderer.class);

    @Override
    public void render(final Survey survey, final SurveySummary summary) {
        final NumberFormat percentFormat = DecimalFormat.getPercentInstance();
        LOGGER.info("=======================================");
        LOGGER.info("Participation percentage : {}", percentFormat.format(summary.participationPercentage()));
        LOGGER.info("Total participation : {}", summary.totalParticipation());
        LOGGER.info("=======================================");
        final NumberFormat ratingFormat = new DecimalFormat("0.00");
        for (final Question question : survey.questions()) {
            if (question instanceof RatingQuestion) {
                final double average = summary.averageRatingFor((RatingQuestion) question);
                LOGGER.info("{} : {}",
                        question.sentence(),
                        Double.isNaN(average) ? "N/A" : ratingFormat.format(average)
                );
            } else if (question instanceof SingleSelectQuestion) {
                Map<String, Double> percentageByAnswer = summary.percentageFor((SingleSelectQuestion) question);
                for (Map.Entry<String, Double> stringDoubleEntry : percentageByAnswer.entrySet()) {
                    LOGGER.info("{}: {}", stringDoubleEntry.getKey(), percentFormat.format(stringDoubleEntry.getValue()));
                }
            }
        }


    }
}
