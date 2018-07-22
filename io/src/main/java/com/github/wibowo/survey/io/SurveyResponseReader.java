package com.github.wibowo.survey.io;

import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.EmployeeResponse;

import java.util.List;

public interface SurveyResponseReader<E> {

    List<EmployeeResponse> readFrom(E source,
                                    Survey survey);
}
