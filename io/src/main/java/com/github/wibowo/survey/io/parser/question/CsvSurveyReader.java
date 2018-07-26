package com.github.wibowo.survey.io.parser.question;

import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveyException;
import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.QuestionFactory;
import com.github.wibowo.survey.model.questionAnswer.Theme;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringTokenizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Parser for the survey question.
 */
public final class CsvSurveyReader implements SurveyReader<InputStream> {
    private static final Logger LOGGER = LogManager.getLogger(CsvSurveyReader.class);
    private static final char DELIMITER_CHARACTER = ',';
    private static final char ESCAPE_CHARACTER = '"';

    @Override
    public Survey readFrom(final InputStream source) {
        requireNonNull(source);
        LOGGER.info("Reading Survey from input source");
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(source))) {
            final ParsingContext context = new ParsingContext();

            bufferedReader.lines()
                    .filter(line -> !StringUtils.isBlank(line))
                    .forEach(line -> processLine(context, line));

            return context.survey();
        } catch (final SurveyException exception) {
            throw exception;
        } catch (final Exception exception) {
            throw new SurveyException("An unexpected error has occurred", exception);
        }

    }

    private void processLine(final ParsingContext context,
                             final @NotNull String line) {
        final StringTokenizer stringTokenizer = new StringTokenizer(line, DELIMITER_CHARACTER, ESCAPE_CHARACTER);
        context.processLine(stringTokenizer.getTokenArray());
    }

    /**
     * Keeps track of the parsing process.
     * i.e. when the metadata hasn't been read, it expects that the first one to be processed next would be the metadata.
     * Subsequent read would be the data themselves.
     */
    static class ParsingContext {
        Metadata metadata;
        final Survey survey;
        int questionIndex;

        ParsingContext() {
            survey = new Survey();
            questionIndex = 0;
        }

        void processLine(final String[] columnValues) {
            if (needToReadMetadata()) {
                readMetadata(columnValues);
            } else {
                readQuestion(columnValues);
            }
        }

        Survey survey() {
            return survey;
        }

        private boolean needToReadMetadata() {
            return metadata == null;
        }

        private void readMetadata(final String[] metadataKeys) {
            LOGGER.debug("Reading metadata line {}", Arrays.toString(metadataKeys));
            final String[] uniqueMetadataKeys = Arrays.stream(metadataKeys).distinct().toArray(String[]::new);
            if (uniqueMetadataKeys.length != metadataKeys.length) {
                throw SurveyException.malformedFile(String.format("Header %s contain one or more duplicated columns.",
                        Arrays.toString(metadataKeys)
                ));
            }
            verifyMetadata(metadataKeys);
            this.metadata = new Metadata(metadataKeys);
        }

        private void verifyMetadata(final String[] metadataKeys) {
            for (final String metadataKey : metadataKeys) {
                if (MetadataKey.isValidKey(metadataKey)) {

                }
            }

        }

        private void readQuestion(final String[] columnValues) {
            final Metadata metadata = this.metadata;
            this.addQuestion(metadata.parseQuestionString(columnValues));
        }

        private void addQuestion(final Question question) {
            survey.addQuestion(question);
        }
    }

    enum MetadataKey {
        Theme("theme"),
        Type("type"),
        Text("text");

        private String key;

        MetadataKey(final String text) {
            this.key=text;
        }

        static boolean isValidKey(final String keyName) {
            for (MetadataKey metadataKey : values()) {
                if (Objects.equals(metadataKey.key, keyName)) {
                    return true;
                }
            }
            return false;
        }

        static MetadataKey find(final String keyName) {
            for (MetadataKey metadataKey : values()) {
                if (Objects.equals(metadataKey.key, keyName)) {
                    return metadataKey;
                }
            }
            throw SurveyException.unsupportedMetadataKey(keyName);
        }
    }


    static class Metadata {
        private final MetadataKey[] keys;

        Metadata(final String[] keyValues) {
            this.keys = new MetadataKey[keyValues.length];
            for (int i = 0; i < keyValues.length; i++) {
                this.keys[i] = MetadataKey.find(keyValues[i]);
            }
        }

        Question parseQuestionString(final String[] stringValues) {
            if (stringValues.length != keys.length) {
                throw SurveyException.malformedFile(
                        String.format(
                                "Row %s cant be processed against header %s",
                                Arrays.toString(stringValues), Arrays.toString(keys)
                        )
                );
            }

            Theme theme = null;
            String questionType = null;
            String text = null;
            for (int i = 0; i < keys.length; i++) {
                final MetadataKey columnName = keys[i];
                switch (columnName) {
                    case Theme:
                        theme = Theme.from(stringValues[i]);
                        break;
                    case Type:
                        questionType = stringValues[i];
                        break;
                    case Text:
                        text = stringValues[i];
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported column " + columnName);
                }
            }

            if (theme == null || questionType == null || text == null) {
                throw SurveyException.malformedFile(
                        "One or more required column is missing. Survey must contain 'theme','type' and 'text' columns"
                );

            }
            return QuestionFactory.createFrom(theme, questionType, text);
        }
    }


}
