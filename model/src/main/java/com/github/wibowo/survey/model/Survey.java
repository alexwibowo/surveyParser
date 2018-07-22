package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.Question;

import java.util.ArrayList;
import java.util.List;

public final class Survey {

    private final List<Question> questions;

    public Survey() {
        this.questions = new ArrayList<>();
    }

    public Survey addQuestion(final Question newQuestion) {
        this.questions.add(newQuestion);
        return this;
    }

    public Iterable<Question> questions() {
        return questions;
    }

    public Question questionNumber(final int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Question index must be >= 0");
        }

        if (index > questions.size()) {
            throw new IllegalArgumentException(String.format("Attempt to ask for question [%d]. But there are only [%d] questions available: %s",
                    index, questions.size(), questions)
            );
        }
        return questions.get(index);
    }

}
