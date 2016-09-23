package com.bezirk.middleware.android.logging;

import com.bezirk.middleware.core.proxy.Config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.Map;

import ch.qos.logback.classic.Level;

public class LoggingManager {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoggingManager.class);
    private final Config config;

    public LoggingManager(@NotNull final Config config) {
        this.config = config;
    }

    public void configure() {
        configureBezirkRootLogging();
        configurePackageLevelLogging();
    }

    private void configureBezirkRootLogging() {
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        Level logBackLevel = getLogbackLevel(config.getLogLevel());

        rootLogger.setLevel(logBackLevel);
        logger.debug("BezirkRootLogging level set to '" + logBackLevel + "'");
    }

    private void configurePackageLevelLogging() {
        if (config.getPackageLogLevelMap() != null) {
            for (Map.Entry<String, Config.Level> entry : config.getPackageLogLevelMap().entrySet()) {
                ch.qos.logback.classic.Logger currentPackageLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(entry.getKey());
                currentPackageLogger.setLevel(getLogbackLevel(entry.getValue()));
            }
        }
    }

    private Level getLogbackLevel(Config.Level level) {
        switch (level) {
            case TRACE:
                return Level.TRACE;
            case DEBUG:
                return Level.DEBUG;
            case INFO:
                return Level.INFO;
            case WARN:
                return Level.WARN;
            case ERROR:
                return Level.ERROR;
            case OFF:
                return Level.OFF;
        }
        return null;
    }

}
