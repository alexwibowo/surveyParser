package com.github.wibowo.survey.model.questionAnswer;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum Theme {
    Work("The Work"),
    Demographic("Demographics"),
    Place("The Place");

    final String description;

    private static final Theme[] themes = Theme.values();

    Theme(final String description) {
        this.description = description;
    }

    public static Theme from(final String themeAsString) {
        Objects.requireNonNull(themeAsString);
        final Optional<Theme> optionalTheme = Arrays.stream(themes)
                .filter(theme -> theme.description.equalsIgnoreCase(themeAsString.trim()))
                .findFirst();
        if (optionalTheme.isPresent()) {
            return optionalTheme.get();
        } else {
            throw new IllegalArgumentException(
                    String.format("Unsupported theme [%s]",themeAsString));
        }
    }
}
