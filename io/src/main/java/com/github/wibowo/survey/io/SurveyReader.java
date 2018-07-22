package com.github.wibowo.survey.io;

import com.github.wibowo.survey.model.Survey;

public interface SurveyReader<E> {

    Survey readFrom(E source);
}
