package com.github.wibowo.survey.io;

import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;

/**
 * Read response from the given input source {@link E}, and return the summarised survey {@link SurveySummary}
 * @param <E> survey response source
 */
public interface SurveyResponseReader<E> {

    SurveySummary readFrom(E source,
                           Survey survey);
}
