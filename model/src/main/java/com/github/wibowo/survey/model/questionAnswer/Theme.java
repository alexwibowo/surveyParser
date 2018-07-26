package com.github.wibowo.survey.model.questionAnswer;

import com.github.wibowo.survey.model.SurveyException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public enum Theme {
    Work("The Work"),
    Demographic("Demographics"),
    Place("The Place");

    final String description;

    private static final Theme[] themes = Theme.values();
    public static final String SUPPORTED_VALUES_AS_STRING = Arrays.stream(themes)
            .map(Theme::description)
            .collect(Collectors.joining(","));

    Theme(final String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    public static Theme from(final String themeAsString) {
        Objects.requireNonNull(themeAsString);
        final Optional<Theme> optionalTheme = Arrays.stream(themes)
                .filter(theme -> theme.description.equalsIgnoreCase(themeAsString.trim()))
                .findFirst();
        if (optionalTheme.isPresent()) {
            return optionalTheme.get();
        } else {
            throw SurveyException.unsupportedTheme(themeAsString);
        }
    }
}
