package com.github.wibowo.survey.app;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertFalse;

public class ApplicationAcceptanceTest {

    private File surveyFile;
    private File surveyResponseFile;

    private ListAppender listAppender;
    private LoggerConfig rootLoggerConfig;


    @BeforeEach
    void setUp() throws IOException {
        final org.apache.logging.log4j.core.LoggerContext loggerContext = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        final Configuration configuration = loggerContext.getConfiguration();
        rootLoggerConfig = configuration.getLoggerConfig("");

        listAppender = new ListAppender("testAppender");
        listAppender.start();
        rootLoggerConfig.addAppender(listAppender, Level.ALL, null);

        surveyFile = File.createTempFile("survey", ".csv");
        surveyFile.deleteOnExit();

        surveyResponseFile = File.createTempFile("surveyResponse", ".csv");
        surveyResponseFile.deleteOnExit();
    }

    @AfterEach
    void tearDown() {
        listAppender.stop();
        rootLoggerConfig.removeAppender("testAppender");
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void simple_test_with_all_answered(final String enableStreaming) throws IOException {
        final String question1 = "I like the kind of work I do";
        final String question2 = "In general, I have the resources (e.g., business tools, information, facilities, IT or functional support) I need to be effective.";
        final String question3 = "We are working at the right pace to meet our goals.";
        final String question4 = "I feel empowered to get the work done for which I am responsible.";
        final String question5 = "I am appropriately involved in decisions that affect my work.";
        final String[] surveyLines = new String[]{
                "theme,type,text",
                "The Work,ratingquestion," + question1,
                "The Work,ratingquestion,\"" + question2 + "\"",
                "The Work,ratingquestion," + question3,
                "The Work,ratingquestion," + question4,
                "The Work,ratingquestion," + question5
        };

        final String[] surveyResponseLines = new String[]{
                "employee1@abc.xyz,1,2014-07-28T20:35:41+00:00,5,5,5,4,4",
                "employee2@abc.xyz,2,2014-07-29T20:35:41+00:00,5,1,3,4,2",
                "employee3@abc.xyz,3,2014-07-30T20:35:41+00:00,4,3,2,1,5"
        };

        writeSurveyAndResponses(surveyLines, surveyResponseLines);


        final @NotNull String[] lines = executeAndGetOutput(enableStreaming);
        verifyLineExists(lines, "Participation percentage", "Participation percentage : 100%");
        verifyLineExists(lines, "Total participation", "Total participation : 3");
        verifyLineExists(lines, question1, question1 + " : 4.67");
        verifyLineExists(lines, question2, question2 + " : 3.00");
        verifyLineExists(lines, question3, question3 + " : 3.33");
        verifyLineExists(lines, question4, question4 + " : 3.00");
        verifyLineExists(lines, question5, question5 + " : 3.67");
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void test_multi_answer(final String enableStreaming) throws IOException {
        final String question1 = "I like the kind of work I do";
        final String question2 = "Office Location";
        final String[] surveyLines = new String[]{
                "theme,type,text",
                "The Work,ratingquestion," + question1,
                "The Work,multiselect," + question2
        };

        final String[] surveyResponseLines = new String[]{
                "employee1@abc.xyz,1,2014-07-28T20:35:41+00:00,5,Melbourne",
                "employee2@abc.xyz,2,2014-07-29T20:35:41+00:00,5,Melbourne|Sydney",
                "employee3@abc.xyz,3,2014-07-30T20:35:41+00:00,5,Jakarta|Singapore"
        };

        writeSurveyAndResponses(surveyLines, surveyResponseLines);

        final @NotNull String[] lines = executeAndGetOutput(enableStreaming);
        verifyLineExists(lines, "Participation percentage", "Participation percentage : 100%");
        verifyLineExists(lines, "Total participation", "Total participation : 3");
        verifyLineExists(lines, question1, question1 + " : 5.00");
    }

    private void writeSurveyAndResponses(String[] surveyLines, String[] surveyResponseLines) throws IOException {
        Files.write(surveyFile.toPath(), Arrays.asList(surveyLines), Charset.defaultCharset());
        Files.write(surveyResponseFile.toPath(), Arrays.asList(surveyResponseLines), Charset.defaultCharset());
    }

    @Test
    void test_single_answer_output() throws IOException {
        final String question1 = "I like the kind of work I do";
        final String question2 = "Manager";
        final String[] surveyLines = new String[]{
                "theme,type,text",
                "The Work,ratingquestion," + question1,
                "The Work,singleselect," + question2
        };

        final String[] surveyResponseLines = new String[]{
                ",1,2014-07-28T20:35:41+00:00,5,Sally",
                ",2,2014-07-29T07:05:41+00:00,4,Jane",
                ",3,2014-07-29T17:35:41+00:00,5,Sally",
                ",4,2014-07-30T04:05:41+00:00,5,Bob",
                ",5,2014-07-31T11:35:41+00:00,4,Mary"
        };

        writeSurveyAndResponses(surveyLines, surveyResponseLines);
        final @NotNull String[] lines = executeAndGetOutput("false");
        verifyLineExists(lines, "Sally", "Sally: 40%");
        verifyLineExists(lines, "Jane", "Jane: 20%");
        verifyLineExists(lines, "Bob", "Bob: 20%");
        verifyLineExists(lines, "Mary", "Mary: 20%");
        verifyLineDoesntExist(lines, "John");
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    @DisplayName("Test where some responses were not submitted by user.")
    void test_with_unsubmitted_response(final String enableStreaming) throws IOException {
        final String question1 = "I like the kind of work I do";
        final String question2 = "In general, I have the resources (e.g., business tools, information, facilities, IT or functional support) I need to be effective.";
        final String question3 = "We are working at the right pace to meet our goals.";
        final String question4 = "I feel empowered to get the work done for which I am responsible.";
        final String question5 = "I am appropriately involved in decisions that affect my work.";
        final String[] surveyLines = new String[]{
                "theme,type,text",
                "The Work,ratingquestion," + question1,
                "The Work,ratingquestion,\"" + question2 + "\"",
                "The Work,ratingquestion," + question3,
                "The Work,ratingquestion," + question4,
                "The Work,ratingquestion," + question5
        };

        final String[] surveyResponseLines = new String[]{
                "employee1@abc.xyz,1,2014-07-28T20:35:41+00:00,5,5,5,4,4",
                ",2,2014-07-29T07:05:41+00:00,4,5,5,3,3",
                ",3,2014-07-29T17:35:41+00:00,5,5,5,5,4",
                "employee4@abc.xyz,4,2014-07-30T04:05:41+00:00,5,5,5,4,4",
                ",5,2014-07-31T11:35:41+00:00,4,5,5,2,3",
                "employee5@abc.xyz,6,,,,,,"
        };

        writeSurveyAndResponses(surveyLines, surveyResponseLines);


        final @NotNull String[] lines = executeAndGetOutput(enableStreaming);
        verifyLineExists(lines, "Participation percentage", "Participation percentage : 83%"); // 5 out of 6
        verifyLineExists(lines, "Total participation", "Total participation : 5");
        verifyLineExists(lines, question1, question1 + " : " + formatRating(((double) (5 + 4 + 5 + 5 + 4)) / 5));
        verifyLineExists(lines, question2, question2 + " : " + formatRating(((double) (5 + 5 + 5 + 5 + 5)) / 5));
        verifyLineExists(lines, question3, question3 + " : " + formatRating(((double) (5 + 5 + 5 + 5 + 5)) / 5));
        verifyLineExists(lines, question4, question4 + " : " + formatRating(((double) (4 + 3 + 5 + 4 + 2)) / 5));
        verifyLineExists(lines, question5, question5 + " : " + formatRating(((double) (4 + 3 + 4 + 4 + 3)) / 5));
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void test_with_singleSelect_question(final String enableStreaming) throws IOException {
        final String question1 = "I like the kind of work I do";
        final String question2 = "In general, I have the resources (e.g., business tools, information, facilities, IT or functional support) I need to be effective.";
        final String question3 = "We are working at the right pace to meet our goals.";
        final String question4 = "I feel empowered to get the work done for which I am responsible.";
        final String question5 = "Manager";
        final String[] surveyLines = new String[]{
                "theme,type,text",
                "The Work,ratingquestion," + question1,
                "The Work,ratingquestion,\"" + question2 + "\"",
                "The Work,ratingquestion," + question3,
                "The Work,ratingquestion," + question4,
                "The Work,singleselect," + question5
        };

        final String[] surveyResponseLines = new String[]{
                ",1,2014-07-28T20:35:41+00:00,5,5,5,4,Sally",
                ",2,2014-07-29T07:05:41+00:00,4,5,5,3,Jane",
                ",3,2014-07-29T17:35:41+00:00,5,5,5,5,John",
                ",4,2014-07-30T04:05:41+00:00,5,5,5,4,Bob",
                ",5,2014-07-31T11:35:41+00:00,4,5,5,2,Mary"
        };

        writeSurveyAndResponses(surveyLines, surveyResponseLines);

        final @NotNull String[] lines = executeAndGetOutput(enableStreaming);
        verifyLineExists(lines, "Participation percentage", "Participation percentage : 100%");
        verifyLineExists(lines, "Total participation", "Total participation : 5");
        verifyLineExists(lines, question1, question1 + " : " + formatRating(((double) (5 + 4 + 5 + 5 + 4)) / 5));
        verifyLineExists(lines, question2, question2 + " : " + formatRating(((double) (5 + 5 + 5 + 5 + 5)) / 5));
        verifyLineExists(lines, question3, question3 + " : " + formatRating(((double) (5 + 5 + 5 + 5 + 5)) / 5));
        verifyLineExists(lines, question4, question4 + " : " + formatRating(((double) (4 + 3 + 5 + 4 + 2)) / 5));
        assertFalse(Arrays.stream(lines)
                .filter(line -> line.startsWith("Manager"))
                .findAny()
                .isPresent());
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void test_with_multiple_response_from_same_employee(final String enableStreaming) throws IOException {
        final String question1 = "I like the kind of work I do";
        final String question2 = "I have the resource I need to be effective.";
        final String question3 = "City";
        final String question4 = "I feel empowered to get the work done for which I am responsible.";
        final String question5 = "Manager";
        final String[] surveyLines = new String[]{
                "type,theme,text",
                "ratingquestion,The Work," + question1,
                "ratingquestion,The Work," + question2,
                "singleselect,Demographics," + question3,
                "ratingquestion,The Work," + question4,
                "singleselect,Demographics," + question5
        };

        final String[] surveyResponseLines = new String[]{
                "employee1@abc.xyz,,,5,5,Perth,4,Sally",
                "employee2@abc.xyz,,,,5,Sydney,3,Jane",
                "employee2@abc.xyz,,,,5,,5,John",
                "employee2@abc.xyz,,,5,5,Melbourne,4,Bob",
                "employee2@abc.xyz,,,4,5,Darwin,2,Mary",
        };

        writeSurveyAndResponses(surveyLines, surveyResponseLines);

        final @NotNull String[] lines = executeAndGetOutput(enableStreaming);
        verifyLineExists(lines, "Participation percentage", "Participation percentage : 0%");
        verifyLineExists(lines, "Total participation", "Total participation : 0");
        verifyLineExists(lines, question1, question1 + " : N/A");
        verifyLineExists(lines, question2, question2 + " : N/A");
        verifyLineExists(lines, question4, question4 + " : N/A");
        assertFalse(Arrays.stream(lines)
                .filter(line -> line.startsWith("Manager"))
                .findAny()
                .isPresent());
        assertFalse(Arrays.stream(lines)
                .filter(line -> line.startsWith("City"))
                .findAny()
                .isPresent());
    }

    private String formatRating(double value) {
        return new DecimalFormat("0.00").format(value);
    }

    private void verifyLineExists(@NotNull final String[] lines,
                                  final String lineFinder,
                                  final String lineMatcher) {
        assertThat(Arrays.stream(lines)
                .filter(line -> line.startsWith(lineFinder))
                .findFirst().get()).isEqualTo(lineMatcher);
    }

    private void verifyLineDoesntExist(@NotNull final String[] lines,
                                  final String lineFinder) {
        assertThat(Arrays.stream(lines)
                .filter(line -> line.startsWith(lineFinder))
                .findFirst())
                .isNotPresent();
    }

    private String[] executeAndGetOutput(final String enableStreaming) throws FileNotFoundException {
        new Application(new ArgumentsBean()
                .setQuestionFile(surveyFile.getPath())
                .setResponseFile(surveyResponseFile.getPath())
                .setEnableStreamingMode(Boolean.parseBoolean(enableStreaming))
        ).doWork();
        final List<LogEvent> events = listAppender.getEvents();
        return events.stream().map(LogEvent::getMessage).map(Message::getFormattedMessage)
                .toArray(String[]::new);

    }
}
