package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.Question;

import java.util.ArrayList;
import java.util.List;

public final class Survey {

    private final List<Question> questions;

    public Survey() {
        this.questions = new ArrayList<>();
    }

    /**
     * @param newQuestion {@link Question} to be added to this survey
     * @return this survey
     */
    public Survey addQuestion(final Question newQuestion) {
        this.questions.add(newQuestion);
        return this;
    }

    public Iterable<Question> questions() {
        return questions;
    }

    /**
     * @param index zero based index of the question to be retrieved
     * @return the n-th {@link Question} from the survey, where n starts from 0
     * @throws IllegalArgumentException if there is not enough question available for the specified index
     */
    public Question questionNumber(final int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Question index starts from 0");
        }

        if (index > questions.size() - 1) {
            throw new IllegalArgumentException(String.format("Attempt to ask for question [%d]. But there are only [%d] questions available: %s",
                    index, questions.size(), questions)
            );
        }
        return questions.get(index);
    }

    @Override
    public String toString() {
        return "Survey{" +
                "questions=" + questions +
                '}';
    }
}
