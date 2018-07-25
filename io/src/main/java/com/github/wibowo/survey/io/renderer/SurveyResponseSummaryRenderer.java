package com.github.wibowo.survey.io.renderer;

import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;

public interface SurveyResponseSummaryRenderer {

    void render(Survey survey, SurveySummary summary);
}
