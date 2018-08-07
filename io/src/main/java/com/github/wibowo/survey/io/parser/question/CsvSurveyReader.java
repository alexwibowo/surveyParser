package com.github.wibowo.survey.io.parser.question;

import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveyException;
import com.github.wibowo.survey.model.questionAnswer.Question;
import com.github.wibowo.survey.model.questionAnswer.QuestionType;
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
        private Metadata metadata;
        private final Survey survey;

        ParsingContext() {
            survey = new Survey();
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

        private void readMetadata(final String[] columnTypes) {
            LOGGER.debug("Reading metadata line {}", Arrays.toString(columnTypes));
            final String[] uniqueMetadataKeys = Arrays.stream(columnTypes).distinct().toArray(String[]::new);
            if (uniqueMetadataKeys.length != columnTypes.length) {
                throw SurveyException.malformedFile(String.format("Header %s contain one or more duplicated columns.",
                        Arrays.toString(columnTypes)
                ));
            }
            this.metadata = new Metadata(columnTypes);
        }

        private void readQuestion(final String[] columnValues) {
            survey.addQuestion(this.metadata.parseQuestionString(columnValues));
        }
    }

    /**
     *  Type of column in the CSV file
     */
    enum ColumnType {
        /**
         * {@link Theme} for the data
         */
        Theme("theme"),
        /**
         * Question type
         */
        Type("type"),
        /**
         * Actual question sentence
         */
        Text("text");

        public static final ColumnType[] SUPPORTED_VALUES = ColumnType.values();

        private String key;

        ColumnType(final String text) {
            this.key=text;
        }

        static ColumnType find(final String keyName) {
            for (final ColumnType metadataKey : SUPPORTED_VALUES) {
                if (Objects.equals(metadataKey.key, keyName)) {
                    return metadataKey;
                }
            }
            throw SurveyException.unsupportedMetadataKey(keyName);
        }
    }

    /**
     * Keeps information about what each column in the CSV corresponds to.
     * In particular, what is the {@link ColumnType} value of a column
     */
    static class Metadata {
        /**
         * Defines what each column means.
         * E.g.: if the content is [{@link ColumnType#Theme},{@link ColumnType#Type},{@link ColumnType#Text}]
         * then the first column is {@link ColumnType#Theme}, the second column is {@link ColumnType#Type}, and so on.
         */
        private final ColumnType[] columnTypes;

        Metadata(final String[] columnTypesAsString) {
            this.columnTypes = Arrays.stream(columnTypesAsString)
                    .map(ColumnType::find)
                    .toArray(ColumnType[]::new);
        }

        /**
         * Create a single {@link Question} from the given column values.
         * The values are parsed in the order of {@link #columnTypes}
         *
         * @param columnValues values to be parsed
         * @return instance of {@link Question} constructed from the values
         */
        Question parseQuestionString(final String[] columnValues) {
            if (columnValues.length != columnTypes.length) {
                throw SurveyException.malformedFile(
                        String.format(
                                "Row %s cant be processed against header %s",
                                Arrays.toString(columnValues), Arrays.toString(columnTypes)
                        )
                );
            }

            Theme theme = null;
            String questionType = null;
            String text = null;
            for (int i = 0; i < columnTypes.length; i++) {
                final ColumnType columnName = columnTypes[i];
                switch (columnName) {
                    case Theme:
                        theme = Theme.from(columnValues[i]);
                        break;
                    case Type:
                        questionType = columnValues[i];
                        break;
                    case Text:
                        text = columnValues[i];
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
            return QuestionType.createFrom(theme, questionType, text);
        }
    }


}
