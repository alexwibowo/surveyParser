package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public final class DefaultSurveyResponseSummary implements SurveySummary {

    private final Survey survey;
    private final List<EmployeeResponse> employeeResponses;

    private final Map<RatingQuestion, Double> ratingAverageByQuestion;

    private double participationPercentage;

    private long numberOfParticipations;

    public DefaultSurveyResponseSummary(final Survey survey,
                                        final List<EmployeeResponse> employeeResponses) {
        this.survey = requireNonNull(survey);
        this.employeeResponses = requireNonNull(employeeResponses);
        this.ratingAverageByQuestion = new HashMap<>();
    }

    public Survey getSurvey() {
        return survey;
    }

    public List<EmployeeResponse> getEmployeeResponses() {
        return Collections.unmodifiableList(employeeResponses);
    }

    @Override
    public double participationPercentage() {
        return participationPercentage;
    }

    public void setParticipationPercentage(double participationPercentage) {
        this.participationPercentage = participationPercentage;
    }

    @Override
    public long totalParticipation() {
        return numberOfParticipations;
    }

    public void setNumberOfParticipations(long numberOfParticipations) {
        this.numberOfParticipations = numberOfParticipations;
    }

    public void addRatingQuestionAverage(final RatingQuestion ratingQuestion,
                                         final double average) {
        ratingAverageByQuestion.put(ratingQuestion, average);
    }

    @Override
    public double averageRatingFor(final RatingQuestion ratingQuestion) {
        return ratingAverageByQuestion.get(ratingQuestion);
    }

    @Override
    public Map<String, Double> percentageFor(final SingleSelectQuestion question) {
        int size = employeeResponses.size();
        Map<String, Long> collect1 = employeeResponses.stream()
                .map(response -> response.answerFor(question))
                .collect(Collectors.groupingBy(
                        SingleSelectAnswer::selection,
                        Collectors.counting()
                ));

        Map<String, Double> averageByAnswer = new HashMap<>();
        for (Map.Entry<String, Long> stringLongEntry : collect1.entrySet()) {
            averageByAnswer.put(stringLongEntry.getKey(), ((double) stringLongEntry.getValue()) / size);
        }

        return averageByAnswer;
    }

    public Map<String, Double> percentageFor(final MultiSelectQuestion multiSelectQuestion) {
        final Map<String, Long> countBySelection = employeeResponses.stream()
                .map(response -> response.answerFor(multiSelectQuestion))
                .flatMap((Function<MultiSelectAnswer, Stream<String>>) multiSelectAnswer -> Arrays.stream(multiSelectAnswer.getSelection()))
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));

        final long size = countBySelection.values().stream().mapToLong(Long::longValue).sum();

        final Map<String, Double> averageByAnswer = new HashMap<>();
        for (final Map.Entry<String, Long> stringLongEntry : countBySelection.entrySet()) {
            averageByAnswer.put(stringLongEntry.getKey(), ((double) stringLongEntry.getValue()) / size);
        }
        return averageByAnswer;
    }
}
