package com.github.wibowo.survey.model.questionAnswer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ThemeTest {

    @Test
    void parse_theme() {
        assertThat(Theme.from("The Place"))
                .isEqualTo(Theme.Place);
        assertThat(Theme.from("The Work"))
                .isEqualTo(Theme.Work);
        assertThat(Theme.from("Demographics"))
                .isEqualTo(Theme.Demographic);
    }

    @Test
    void parsing_is_case_insensitive() {
        assertThat(Theme.from("The Place"))
                .isEqualTo(Theme.from("the place"))
                .isEqualTo(Theme.from("tHe place"))
                .isEqualTo(Theme.from("thE place"))
                .isEqualTo(Theme.from("the Place"))
                .isEqualTo(Theme.from("the pLace"))
                // and so on
                .isEqualTo(Theme.Place);
    }

    @Test
    void trim_before_matching() {
        assertThat(Theme.from("The Place"))
                .isEqualTo(Theme.from("     the place\t\t  "))
                .isEqualTo(Theme.Place);
    }

    @Test
    void fail_when_given_null() {
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class,
                () -> Theme.from(null)
        );
    }

    @Test
    void fail_when_given_emptyString() {
        final IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> Theme.from("")
        );
        assertThat(exception.getMessage()).isEqualTo("Unsupported theme []");
    }

}