package com.github.wibowo.survey.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Application {
    public static final Logger LOGGER = LogManager.getLogger(Application.class);

    public static void main(final String... args) {
        LOGGER.info("Processing {}", args);
    }
}
