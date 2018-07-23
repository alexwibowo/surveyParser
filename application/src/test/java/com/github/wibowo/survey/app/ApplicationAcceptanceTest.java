package com.github.wibowo.survey.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

public class ApplicationAcceptanceTest {

    private Application application;

    private File surveyFile;
    private File surveyResponseFile;

    @BeforeEach
    void setUp() throws IOException {
        application = new Application();
        surveyFile = File.createTempFile("survey", ".csv");
        surveyFile.deleteOnExit();

        surveyResponseFile = File.createTempFile("surveyRespones", ".csv");
        surveyResponseFile.deleteOnExit();

    }

    @Test
    void test() throws IOException {
        final String[] surveyLines = new String[]{
                "theme,type,text,text",
                "The Work,ratingquestion,Question number 1",
                "The Work,ratingquestion,Question number 2"
        };

        final String[] surveyResponseLines = new String[]{
                "employee1@abc.xyz,1,2014-07-28T20:35:41+00:00,5,4",
                "employee2@abc.xyz,2,2014-07-30T23:35:41+10:00,5,5"
        };

        Files.write(surveyFile.toPath(), Arrays.asList(surveyLines), Charset.defaultCharset());
        Files.write(surveyResponseFile.toPath(), Arrays.asList(surveyResponseLines), Charset.defaultCharset());

        application.main(
                surveyFile.getPath(),
                surveyResponseFile.getPath()
        );
    }
}
