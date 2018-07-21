package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.Answer;
import com.github.wibowo.survey.model.questionAnswer.Question;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public final class SurveyResponse {

    private final Survey survey;

    private final Employee employee;

    private final @Nullable LocalDateTime submittedAt;

    private final Map<Question, Answer> answers;

    private SurveyResponse(final Survey survey,
                          final Employee employee,
                          final @Nullable LocalDateTime submittedAt) {
        this.survey = requireNonNull(survey);
        this.employee = requireNonNull(employee);
        this.submittedAt = submittedAt;
        this.answers = new HashMap<>();
    }

    public static SurveyResponse submittedResponse(final Survey survey,
                                                   final Employee employee,
                                                   final LocalDateTime submittedAt) {
        return new SurveyResponse(survey, employee, submittedAt);
    }

    public static SurveyResponse unsubmittedResponse(final Survey survey,
                                                     final Employee employee) {
        return new SurveyResponse(survey, employee, null);
    }

    public SurveyResponse addAnswer(final @NotNull Answer answer) {
        Objects.requireNonNull(answer);
        answers.put(answer.question(), answer);
        return this;
    }

    public <E extends Answer> Optional<E> answerFor(final @NotNull Question<E> question) {
        Objects.requireNonNull(question);

        // we expect the question and the answer are synced. So it is ok to cast here.
        //noinspection unchecked
        return Optional.ofNullable((E)answers.get(question));
    }

    public Survey survey() {
        return survey;
    }

    public Employee employee() {
        return employee;
    }

    public Optional<LocalDateTime> submittedAt() {
        return Optional.ofNullable(submittedAt);
    }
}
