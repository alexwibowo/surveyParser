package com.github.wibowo.survey.app;

import com.github.wibowo.survey.io.csv.CsvStreamingSurveyResponseReader;
import com.github.wibowo.survey.io.logger.LoggerSurveyResponseSummaryRenderer;
import com.github.wibowo.survey.io.csv.CsvSurveyReader;
import com.github.wibowo.survey.io.csv.CsvSurveyResponseReader;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public final class Application {
    private static final boolean enableStreaming = false;

    public static void main(final String... args) throws FileNotFoundException {
        final String surveyQuestion = args[0];
        final String surveyResponse = args[1];

        final CsvSurveyReader csvSurveyReader = new CsvSurveyReader();
        final Survey survey = csvSurveyReader.readFrom(new FileInputStream(surveyQuestion));

        final SurveySummary surveySummary;
        if (enableStreaming) {
            final CsvStreamingSurveyResponseReader streamingReader = new CsvStreamingSurveyResponseReader(survey);
            surveySummary = streamingReader.process(new FileInputStream(surveyResponse)).getSummary();
        } else {
            final CsvSurveyResponseReader csvreader = new CsvSurveyResponseReader(survey);
            surveySummary = csvreader.process(new FileInputStream(surveyResponse)).getSummary();
        }

        new LoggerSurveyResponseSummaryRenderer().render(survey, surveySummary);
    }
}
