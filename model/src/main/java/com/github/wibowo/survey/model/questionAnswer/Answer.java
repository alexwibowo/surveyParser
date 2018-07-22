package com.github.wibowo.survey.model.questionAnswer;

/**
 * Answer to a given {@link Question}
 *
 * @param <E> a type of {@link Question}
 */
public interface Answer<E extends Question> {

    /**
     * @return {@link Question} for this answer
     */
    E question();

    /**
     * @return true if this answer is invalid and should be treated as user not responding to the question
     */
    boolean isNull();
}
