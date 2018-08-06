package com.github.wibowo.survey.model;

import com.github.wibowo.survey.model.questionAnswer.Answer;
import com.github.wibowo.survey.model.questionAnswer.Question;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Encapsulates response of single employee to all the questions in a survey.
 */
public final class EmployeeResponse {

    private final Survey survey;

    private final Employee employee;

    private final @Nullable ZonedDateTime submittedAt;

    /**
     * Mapping from question to the answer.
     * The invariant here is that the key (the question) is the same as {@link Answer#question()}
     * @see #addAnswer(Answer)
     */
    private final Map<Question, Answer> answers;

    private EmployeeResponse(final Survey survey,
                             final Employee employee,
                             final @Nullable ZonedDateTime submittedAt) {
        this.survey = requireNonNull(survey);
        this.employee = requireNonNull(employee);
        this.submittedAt = submittedAt;
        this.answers = new HashMap<>();
    }

    public static EmployeeResponse submittedResponse(final Survey survey,
                                                     final Employee employee,
                                                     final ZonedDateTime submittedAt) {
        return new EmployeeResponse(survey, employee, submittedAt);
    }

    public static EmployeeResponse unsubmittedResponse(final Survey survey,
                                                       final Employee employee) {
        return new EmployeeResponse(survey, employee, null);
    }

    /**
     * Add employee's answer for the survey
     */
    public EmployeeResponse addAnswer(final @NotNull Answer answer) {
        Objects.requireNonNull(answer);
        answers.put(answer.question(), answer);
        return this;
    }

    /**
     * Find {@link Answer} for a given {@link Question}
     *
     * @return {@link Question#nullAnswer()} when we cant find answer for the requested question
     */
    public @NotNull <E extends Answer> E answerFor(final @NotNull Question<E> question) {
        Objects.requireNonNull(question);

        // we expect the question and the answer are synced. So it is ok to cast here.
        //noinspection unchecked
        final E answer = (E) answers.get(question);
        if (answer != null) {
            return answer;
        } else {
            return question.nullAnswer();
        }
    }

    public Survey survey() {
        return survey;
    }

    public Employee employee() {
        return employee;
    }

    public Optional<ZonedDateTime> submittedAt() {
        return Optional.ofNullable(submittedAt);
    }

    public boolean wasSubmitted() {
        return submittedAt != null;
    }
}
