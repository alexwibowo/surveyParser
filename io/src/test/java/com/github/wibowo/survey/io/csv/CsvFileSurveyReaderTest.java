package com.github.wibowo.survey.io.csv;

import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveyException;
import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.RatingQuestion;
import com.github.wibowo.survey.model.questionAnswer.SingleSelectQuestion;
import com.github.wibowo.survey.model.questionAnswer.Theme;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CsvFileSurveyReaderTest {

    @Test
    void reject_file_with_duplicated_column() {
        final InputStream inputStream = inputFrom(new String[]{
                "theme,type,text,text",
                "The Work,ratingquestion,I like the kind of work I do,Another column",
                "The Work,ratingquestion,Some question regarding to unknown type,Another duplicated question"
        });
        final SurveyException exception = Assertions.assertThrows(
                SurveyException.class,
                () -> new CsvSurveyReader().readFrom(inputStream)
        );
        assertThat(exception.getMessage()).isEqualTo("Malformed survey file: Header [theme, type, text, text] contain one or more duplicated columns.");
    }

    @Test
    void reject_file_with_missing_column() {
        final SurveyException missingTheme = Assertions.assertThrows(
                SurveyException.class,
                () -> new CsvSurveyReader().readFrom(inputFrom(new String[]{
                        "type,text",
                        "ratingquestion,I like the kind of work I do",
                        "ratingquestion,Some question regarding to unknown type"
                }))
        );

        final SurveyException missingType = Assertions.assertThrows(
                SurveyException.class,
                () -> new CsvSurveyReader().readFrom(inputFrom(new String[]{
                        "theme,text",
                        "The Work,I like the kind of work I do",
                        "The Work,Some question regarding to unknown type"
                }))
        );

        final SurveyException missingText = Assertions.assertThrows(
                SurveyException.class,
                () -> new CsvSurveyReader().readFrom(inputFrom(new String[]{
                        "theme,type",
                        "The Work,ratingquestion",
                        "The Work,ratingquestion"
                }))
        );

        assertThat(missingTheme.getMessage())
                .isEqualTo(missingType.getMessage())
                .isEqualTo(missingText.getMessage())
                .isEqualTo("Malformed survey file: One or more required column is missing. Survey must contain 'theme','type' and 'text' columns");
    }

    @Test
    void reject_file_with_mismatch_between_header_and_rows() {
        final String[] rows = new String[]{
                "theme,type,text,anotherExtraColumn",
                "The Work,ratingquestion,I like the kind of work I do",
                "The Work,ratingquestion,Some question regarding to unknown type"
        };
        final InputStream inputStream = inputFrom(rows);
        final SurveyException exception = Assertions.assertThrows(
                SurveyException.class,
                () -> new CsvSurveyReader().readFrom(inputStream)
        );
        assertThat(exception.getMessage()).isEqualTo("Malformed survey file: Row [The Work, ratingquestion, I like the kind of work I do] cant be processed against header [theme, type, text, anotherExtraColumn]");
    }

    @Test
    void reject_unknown_question_type() {
        final String[] rows = new String[]{
                "theme,type,text",
                "The Work,ratingquestion,I like the kind of work I do",
                "The Work,someunknowntype,Some question regarding to unknown type"
        };
        final InputStream inputStream = inputFrom(rows);

        final SurveyException exception = Assertions.assertThrows(
                SurveyException.class,
                () -> new CsvSurveyReader().readFrom(inputStream)
        );
        assertThat(exception.getMessage()).isEqualTo("Unsupported question [someunknowntype]");
    }

    @Test
    void reading_one_question() {
        final String[] rows = new String[]{
                "theme,type,text",
                "The Work,ratingquestion,I like the kind of work I do"
        };
        final Survey survey = new CsvSurveyReader().readFrom(inputFrom(rows));
        assertNotNull(survey);

        final List<Question> questions = StreamSupport.stream(survey.questions().spliterator(), false)
                .collect(Collectors.toList());
        assertThat(questions).hasSize(1);

        verifyQuestion(questions.get(0), Theme.Work, RatingQuestion.class, "I like the kind of work I do");
    }

    @Test
    void reading_multiple_questions_of_same_type() {
        final String[] rows = new String[]{
                "theme,type,text",
                "The Work,ratingquestion,I like the kind of work I do",
                "The Place,ratingquestion,We are working at the right pace to meet our goals."
        };
        final Survey survey = new CsvSurveyReader().readFrom(inputFrom(rows));
        assertNotNull(survey);

        final List<Question> questions = StreamSupport.stream(survey.questions().spliterator(), false)
                .collect(Collectors.toList());
        assertThat(questions).hasSize(2);

        verifyQuestion(questions.get(0), Theme.Work, RatingQuestion.class, "I like the kind of work I do");
        verifyQuestion(questions.get(1),Theme.Place, RatingQuestion.class, "We are working at the right pace to meet our goals.");
    }

    @Test
    void reading_multiple_questions_of_different_type() {
        final String[] rows = new String[]{
                "theme,type,text",
                "The Work,ratingquestion,I like the kind of work I do",
                "The Place,ratingquestion,We are working at the right pace to meet our goals.",
                "Demographics,singleselect,Manager"
        };
        final Survey survey = new CsvSurveyReader().readFrom(inputFrom(rows));
        assertNotNull(survey);

        final List<Question> questions = StreamSupport.stream(survey.questions().spliterator(), false)
                .collect(Collectors.toList());
        assertThat(questions).hasSize(3);

        verifyQuestion(questions.get(0), Theme.Work, RatingQuestion.class, "I like the kind of work I do");
        verifyQuestion(questions.get(1),Theme.Place, RatingQuestion.class, "We are working at the right pace to meet our goals.");
        verifyQuestion(questions.get(2),Theme.Demographic, SingleSelectQuestion.class, "Manager");
    }

    private void verifyQuestion(final Question question,
                                final Theme expectedTheme,
                                final Class expectedQuestionType,
                                final String expectedQuestionText) {
        assertThat(question.theme()).isEqualTo(expectedTheme);
        assertThat(question).isInstanceOf(expectedQuestionType);
        assertThat(question.sentence()).isEqualTo(expectedQuestionText);
    }

    @NotNull
    private InputStream inputFrom(String[] rows) {
        final String fileContent = Arrays.stream(rows).collect(Collectors.joining("\n"));
        return new ByteArrayInputStream(fileContent.getBytes());
    }

}