package com.github.wibowo.survey.io.parser.question;

import com.github.wibowo.survey.model.Survey;

/**
 * Read {@link Survey} from the given source of type {@link E}
 *
 * @param <E> input source
 */
public interface SurveyReader<E> {

    Survey readFrom(E source);
}
