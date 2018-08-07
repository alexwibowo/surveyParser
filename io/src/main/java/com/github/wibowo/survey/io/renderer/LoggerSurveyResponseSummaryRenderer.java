package com.github.wibowo.survey.io.renderer;

import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;
import com.github.wibowo.survey.model.questionAnswer.MultiSelectQuestion;
import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import com.github.wibowo.survey.model.questionAnswer.SingleSelectQuestion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

/**
 * Implementation of {@link SurveyResponseSummaryRenderer} that simply logs the result to {@link Logger}
 */
public final class LoggerSurveyResponseSummaryRenderer implements SurveyResponseSummaryRenderer {
    public static final Logger LOGGER = LogManager.getLogger(LoggerSurveyResponseSummaryRenderer.class);
    private static final ThreadLocal<DecimalFormat> ratingFormat = ThreadLocal.withInitial(() -> {
        final DecimalFormat decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        return decimalFormat;
    });
    private static final ThreadLocal<DecimalFormat> integerFormat = ThreadLocal.withInitial(() -> new DecimalFormat("###,###"));
    private static final ThreadLocal<NumberFormat> percentFormat = ThreadLocal.withInitial(DecimalFormat::getPercentInstance);

    @Override
    public void render(final Survey survey, final SurveySummary summary) {
        LOGGER.info("=======================================");
        LOGGER.info("Participation percentage : {}", percentFormat.get().format(summary.participationPercentage()));
        LOGGER.info("Total participation : {}", integerFormat.get().format(summary.totalParticipation()));
        LOGGER.info("=======================================");
        for (final Question question : survey.questions()) {
            if (question instanceof RatingQuestion) {
                reportRatingQuestion(summary, (RatingQuestion) question);
            } else if (question instanceof SingleSelectQuestion) {
                reportSingleSelectQuestion(summary, (SingleSelectQuestion) question);
            } else if (question instanceof MultiSelectQuestion) {
                reportMultiSelectQuestion(summary, (MultiSelectQuestion) question);
            }
        }
    }

    private void reportMultiSelectQuestion(final SurveySummary summary,
                                           final MultiSelectQuestion question) {
        final Map<String, Double> percentageByAnswer = summary.percentageFor(question);
        for (final Map.Entry<String, Double> stringDoubleEntry : percentageByAnswer.entrySet()) {
            LOGGER.info("{}: {}: {}", question.sentence(), stringDoubleEntry.getKey(), percentFormat.get().format(stringDoubleEntry.getValue()));
        }
    }

    private void reportRatingQuestion(final SurveySummary summary,
                                      final RatingQuestion question) {
        final double average = summary.averageRatingFor(question);
        LOGGER.info("{} : {}",
                question.sentence(),
                Double.isNaN(average) ? "N/A" : ratingFormat.get().format(average)
        );
    }

    private void reportSingleSelectQuestion(final SurveySummary summary,
                                            final SingleSelectQuestion question) {
        final Map<String, Double> percentageByAnswer = summary.percentageFor(question);
        for (final Map.Entry<String, Double> stringDoubleEntry : percentageByAnswer.entrySet()) {
            LOGGER.info("{}: {}: {}", question.sentence(), stringDoubleEntry.getKey(), percentFormat.get().format(stringDoubleEntry.getValue()));
        }
    }

}
