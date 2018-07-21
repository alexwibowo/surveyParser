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

}
