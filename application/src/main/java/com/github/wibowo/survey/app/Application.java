package com.github.wibowo.survey.app;

import com.github.wibowo.survey.io.csv.CsvStreamingSurveyResponseReader;
import com.github.wibowo.survey.io.csv.CsvSurveyReader;
import com.github.wibowo.survey.io.csv.CsvSurveyResponseReader;
import com.github.wibowo.survey.io.logger.LoggerSurveyResponseSummaryRenderer;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

import static org.kohsuke.args4j.OptionHandlerFilter.ALL;

public final class Application {

    private final Arguments arguments;

    public Application(final Arguments arguments) {
        this.arguments = Objects.requireNonNull(arguments);
    }

    public static void main(final String... args) throws FileNotFoundException {
        final ArgumentsBean arguments = new ArgumentsBean();
        final CmdLineParser parser = new CmdLineParser(arguments, ParserProperties.defaults().withUsageWidth(120).withShowDefaults(true));
        try {
            parser.parseArgument(args);
            new Application(arguments).doWork();
        } catch (final CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java Application [options...]");
            parser.printUsage(System.err);
            System.err.println();

            // print option sample. This is useful some time
            System.err.println("  Example: java Application"+parser.printExample(ALL));
            System.exit(1);
        }

    }

    public void doWork() throws FileNotFoundException {
        final CsvSurveyReader csvSurveyReader = new CsvSurveyReader();
        final Survey survey = csvSurveyReader.readFrom(new FileInputStream(arguments.getQuestionFile()));

        final SurveySummary surveySummary;
        if (arguments.isEnableStreamingMode()) {
            final CsvStreamingSurveyResponseReader streamingReader = new CsvStreamingSurveyResponseReader(survey);
            surveySummary = streamingReader.process(new FileInputStream(arguments.getResponseFile())).getSummary();
        } else {
            final CsvSurveyResponseReader csvreader = new CsvSurveyResponseReader(survey);
            surveySummary = csvreader.process(new FileInputStream(arguments.getResponseFile())).getSummary();
        }

        new LoggerSurveyResponseSummaryRenderer().render(survey, surveySummary);
    }
}
