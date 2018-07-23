package com.github.wibowo.survey.app;

import com.github.wibowo.survey.io.SurveySummariser;
import com.github.wibowo.survey.io.csv.CsvSurveyReader;
import com.github.wibowo.survey.io.csv.CsvSurveyResponseReader;
import com.github.wibowo.survey.model.EmployeeResponse;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveyResponseSummary;
import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public final class Application {
    public static final Logger LOGGER = LogManager.getLogger(Application.class);

    public static void main(final String... args) throws FileNotFoundException {
        LOGGER.debug("Processing {}", args);
        final String surveyQuestion = args[0];
        final String surveyResponse = args[1];

        final CsvSurveyReader csvSurveyReader = new CsvSurveyReader();
        final Survey survey = csvSurveyReader.readFrom(new FileInputStream(surveyQuestion));

        final CsvSurveyResponseReader csvSurveyResponseReader = new CsvSurveyResponseReader();
        final List<EmployeeResponse> employeeResponses = csvSurveyResponseReader.readFrom(new FileInputStream(surveyResponse), survey);


        final SurveyResponseSummary summary = SurveySummariser.summarise(survey, employeeResponses);
        final NumberFormat percentFormat = DecimalFormat.getPercentInstance();
        LOGGER.info("=======================================");
        LOGGER.info("Participation percentage : {}", percentFormat.format(summary.getParticipationPercentage()));
        LOGGER.info("Total participation      : {}", summary.getNumberOfParticipations());
        LOGGER.info("=======================================");
        final NumberFormat ratingFormat = new DecimalFormat("###.00");
        for (final Question question : survey.questions()) {
            if (question instanceof RatingQuestion) {
                LOGGER.info("{} : {}",
                        question.sentence(),
                        ratingFormat.format(summary.averageRatingFor((RatingQuestion)question))
                );
            }
        }


    }
}
