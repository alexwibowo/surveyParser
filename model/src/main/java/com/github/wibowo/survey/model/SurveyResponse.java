package com.github.wibowo.survey.model;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public final class SurveyResponse {

    private final Survey survey;

    private final Employee employee;

    private final @Nullable LocalDateTime submittedAt;

    public SurveyResponse(final Survey survey,
                          final Employee employee,
                          final @Nullable LocalDateTime submittedAt) {
        this.survey = requireNonNull(survey);
        this.employee = requireNonNull(employee);
        this.submittedAt = submittedAt;
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
