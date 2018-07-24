package com.github.wibowo.survey.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ApplicationPerfTest {
    private File surveyFile;
    private File surveyResponseFile;
    private int numEntries = 1_000_000;

    @BeforeEach
    void setUp() throws IOException {
        surveyFile = File.createTempFile("survey", ".csv");
        surveyFile.deleteOnExit();

        surveyResponseFile = File.createTempFile("surveyResponse", ".csv");
        surveyResponseFile.deleteOnExit();

        prepareSurveyFiles();
    }

    private void prepareSurveyFiles() throws IOException {
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
        Files.write(surveyFile.toPath(), Arrays.asList(surveyLines), Charset.defaultCharset());

        final ZonedDateTime zonedDateTime = ZonedDateTime.now().minusMonths(5);
        final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(surveyResponseFile));
        final Random randomiser = new Random();
        for (long i = 0; i < numEntries; i++) {
            final long employeeID = i % 1_000;
            final String employeeEmail = "employee" + employeeID + "@abc.xyz";
            final ZonedDateTime submissionDateTime = zonedDateTime.plusSeconds(i);
            final int randomQuestion1 = randomiser.nextInt(5) + 1;
            final int randomQuestion2 = randomiser.nextInt(5) + 1;
            final int randomQuestion3 = randomiser.nextInt(5) + 1;
            final int randomQuestion4 = randomiser.nextInt(5) + 1;
            final int randomQuestion5 = randomiser.nextInt(5) + 1;
            final String line = String.format("%s,%d,%s,%d,%d,%d,%d,%d",
                    employeeEmail, employeeID,
                    DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(submissionDateTime),
                    randomQuestion1, randomQuestion2, randomQuestion3, randomQuestion4, randomQuestion5);
            bufferedWriter.append(line);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }


    @Test
    void test_processing_using_streaming() throws IOException {
        final long start = System.nanoTime();
        executeAndGetOutput("true");
        final long end = System.nanoTime();

        final long durationNanos = end - start;
        final double averagePerQuestionNanos = ((double) durationNanos) / numEntries;
        System.out.println("Took " + TimeUnit.NANOSECONDS.toMillis(durationNanos) + "ms");
        System.out.println("Average per entry " + averagePerQuestionNanos + "ns");

        assertThat(TimeUnit.NANOSECONDS.toMillis(durationNanos)).isLessThan(2500);
    }

    private void executeAndGetOutput(final String enableStreaming) throws FileNotFoundException {
        Application.main(
                surveyFile.getPath(),
                surveyResponseFile.getPath(),
                enableStreaming
        );
    }
}
