package com.github.wibowo.survey.app;

import com.github.wibowo.survey.io.SurveySummariser;
import com.github.wibowo.survey.io.logger.LoggerSurveyResponseSummaryRenderer;
import com.github.wibowo.survey.io.csv.CsvSurveyReader;
import com.github.wibowo.survey.io.csv.CsvSurveyResponseReader;
import com.github.wibowo.survey.model.EmployeeResponse;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveyResponseSummary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public final class Application {

    public static void main(final String... args) throws FileNotFoundException {
        final String surveyQuestion = args[0];
        final String surveyResponse = args[1];

        final CsvSurveyReader csvSurveyReader = new CsvSurveyReader();
        final Survey survey = csvSurveyReader.readFrom(new FileInputStream(surveyQuestion));

        final CsvSurveyResponseReader csvSurveyResponseReader = new CsvSurveyResponseReader();
        final List<EmployeeResponse> employeeResponses = csvSurveyResponseReader.readFrom(new FileInputStream(surveyResponse), survey);


        final SurveyResponseSummary summary = SurveySummariser.summarise(survey, employeeResponses);
        new LoggerSurveyResponseSummaryRenderer().render(summary);
    }
}
