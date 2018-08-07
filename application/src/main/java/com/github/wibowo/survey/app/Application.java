package com.github.wibowo.survey.app;

import com.github.wibowo.survey.io.parser.question.CsvSurveyReader;
import com.github.wibowo.survey.io.parser.response.CsvStreamingSurveyResponseReader;
import com.github.wibowo.survey.io.parser.response.CsvSurveyResponseReader;
import com.github.wibowo.survey.io.renderer.LoggerSurveyResponseSummaryRenderer;
import com.github.wibowo.survey.model.Survey;
import com.github.wibowo.survey.model.SurveySummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

import static org.kohsuke.args4j.OptionHandlerFilter.ALL;

public final class Application {
    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    private final Arguments arguments;

    Application(final Arguments arguments) {
        this.arguments = Objects.requireNonNull(arguments);
    }

    public static void main(final String... args) throws FileNotFoundException {
        final ArgumentsBean arguments = new ArgumentsBean();
        final CmdLineParser parser = new CmdLineParser(arguments, ParserProperties.defaults().withUsageWidth(120).withShowDefaults(true));
        try {
            LOGGER.info("     _______. __    __  .______     ____    ____  ___________    ____  ______   .______      ");
            LOGGER.info("    /       ||  |  |  | |   _  \\    \\   \\  /   / |   ____\\   \\  /   / /  __  \\  |   _  \\     ");
            LOGGER.info("   |   (----`|  |  |  | |  |_)  |    \\   \\/   /  |  |__   \\   \\/   / |  |  |  | |  |_)  |    ");
            LOGGER.info("    \\   \\    |  |  |  | |      /      \\      /   |   __|   \\_    _/  |  |  |  | |      /     ");
            LOGGER.info(".----)   |   |  `--'  | |  |\\  \\----.  \\    /    |  |____    |  |    |  `--'  | |  |\\  \\----.");
            LOGGER.info("|_______/     \\______/  | _| `._____|   \\__/     |_______|   |__|     \\______/  | _| `._____|");
            LOGGER.info("                                                                                             ");


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

    void doWork() throws FileNotFoundException {
        final CsvSurveyReader csvSurveyReader = new CsvSurveyReader();
        final Survey survey = csvSurveyReader.readFrom(new FileInputStream(new File(arguments.getQuestionFile())));

        final SurveySummary surveySummary;
        if (arguments.isEnableStreamingMode()) {
            LOGGER.info("Running application in streaming mode");
            final CsvStreamingSurveyResponseReader streamingReader = new CsvStreamingSurveyResponseReader(survey);
            surveySummary = streamingReader.process(new FileInputStream(new File(arguments.getResponseFile()))).getSummary();
        } else {
            LOGGER.info("Running application in non-streaming mode");
            final CsvSurveyResponseReader csvReader = new CsvSurveyResponseReader(survey);
            surveySummary = csvReader.process(new FileInputStream(new File(arguments.getResponseFile()))).getSummary();
        }

        new LoggerSurveyResponseSummaryRenderer().render(survey, surveySummary);
    }
}
