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
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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

    @Test
    void test() throws IOException {
        final String question1 = "I like the kind of work I do";
        final String question2 = "In general I have the resources I need to be effective.";
        final String question3 = "We are working at the right pace to meet our goals.";
        final String question4 = "I feel empowered to get the work done for which I am responsible.";
        final String question5 = "I am appropriately involved in decisions that affect my work.";
        final String[] surveyLines = new String[]{
                "theme,type,text",
                "The Work,ratingquestion," + question1,
                "The Work,ratingquestion," + question2,
                "The Work,ratingquestion," + question3,
                "The Work,ratingquestion," + question4,
                "The Work,ratingquestion," + question5
        };

        final String[] surveyResponseLines = new String[]{
                "employee1@abc.xyz,1,2014-07-28T20:35:41+00:00,5,5,5,4,4",
                "employee2@abc.xyz,2,2014-07-29T20:35:41+00:00,5,1,3,4,2",
                "employee3@abc.xyz,3,2014-07-30T20:35:41+00:00,4,3,2,1,5"
        };

        Files.write(surveyFile.toPath(), Arrays.asList(surveyLines), Charset.defaultCharset());
        Files.write(surveyResponseFile.toPath(), Arrays.asList(surveyResponseLines), Charset.defaultCharset());


        final @NotNull String[] lines = executeAndGetOutput();
        verifyLineExists(lines, "Participation percentage", "Participation percentage\\s*:\\s*100%");
        verifyLineExists(lines, "Total participation", "Total participation\\s*:\\s*3");
        verifyLineExists(lines, question1, question1 + "\\s*:\\s*4.67");
        verifyLineExists(lines, question2, question2 + "\\s*:\\s*3.00");
        verifyLineExists(lines, question3, question3 + "\\s*:\\s*3.33");
        verifyLineExists(lines, question4, question4 + "\\s*:\\s*3.00");
        verifyLineExists(lines, question5, question5 + "\\s*:\\s*3.67");
    }

    private void verifyLineExists(@NotNull final String[] lines,
                                  final String lineFinder,
                                  final String lineMatcher) {
        assertThat(Arrays.stream(lines)
                .filter(line -> line.startsWith(lineFinder))
                .findFirst().get()).matches(lineMatcher);
    }

    @NotNull
    private String[] executeAndGetOutput() throws FileNotFoundException {
        Application.main(
                surveyFile.getPath(),
                surveyResponseFile.getPath()
        );

        final List<LogEvent> events = listAppender.getEvents();
        return events.stream().map(LogEvent::getMessage).map(Message::getFormattedMessage)
                .toArray(String[]::new);

    }
}
