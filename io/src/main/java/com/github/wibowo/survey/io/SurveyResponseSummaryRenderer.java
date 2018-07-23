package com.github.wibowo.survey.io;

import com.github.wibowo.survey.model.DefaultSurveyResponseSummary;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;

public interface SurveyResponseSummaryRenderer {

    void render(Survey survey, SurveySummary summary);
}
