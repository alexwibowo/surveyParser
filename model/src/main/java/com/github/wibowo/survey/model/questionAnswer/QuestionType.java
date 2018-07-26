package com.github.wibowo.survey.model.questionAnswer;

import com.github.wibowo.survey.model.SurveyException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public enum QuestionType {
    RatingQuestion("ratingquestion"),
    SingleSelectQuestion("singleselect");

    public static final QuestionType[] values = QuestionType.values();

    public static final String SUPPORTED_VALUES_AS_STRING = Arrays.stream(values)
            .map(QuestionType::questionTypeAsString)
            .collect(Collectors.joining(","));


    private final String questionTypeAsString;

    QuestionType(final String questionTypeAsString) {
        this.questionTypeAsString = questionTypeAsString;
    }

    public static QuestionType questionTypeFor(final String stringValue) {
        Objects.requireNonNull(stringValue);
        final Optional<QuestionType> optionalType = Arrays.stream(values)
                .filter(type -> type.questionTypeAsString.equalsIgnoreCase(stringValue.trim()))
                .findFirst();
        if (optionalType.isPresent()) {
            return optionalType.get();
        } else {
            throw SurveyException.unsupportedQuestionType(stringValue);
        }
    }

    public String questionTypeAsString() {
        return questionTypeAsString;
    }

    public static Question createFrom(final Theme theme,
                                      final String type,
                                      final String text) {
        final QuestionType questionType = questionTypeFor(Objects.requireNonNull(type, "question type must be provided"));
        switch (questionType) {
            case RatingQuestion:
                return new RatingQuestion(theme, text);
            case SingleSelectQuestion:
                return new SingleSelectQuestion(theme, text);
            default:
                throw SurveyException.unsupportedQuestionType(type);
        }
    }
}
