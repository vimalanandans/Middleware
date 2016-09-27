package com.bezirk.middleware.java.logging;

import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.java.componentManager.Configuration;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.util.Map;

import ch.qos.logback.classic.Level;

public class LoggingManager {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoggingManager.class);
    private final static String LOG_LEVEL_ENV_VARIABLE = "loglevel";
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
        Config.Level level = getLevel(Configuration.getPropertyValue(LOG_LEVEL_ENV_VARIABLE));
        Level logBackLevel;
        if (level != null) {
            logBackLevel = getLogbackLevel(level);
        } else {
            logBackLevel = getLogbackLevel(config.getLogLevel());
        }
        rootLogger.setLevel(logBackLevel);
        logger.debug("BezirkRootLogging level set to '" + logBackLevel + "'");
        //logger.info("BezirkRootLogging level set to '" + logBackLevel + "'"); //kept to test functionality, LoggingManagerTest
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

    private static Config.Level getLevel(String s) {
        if (s == null) {
            return null;
        }
        for (Config.Level level : Config.Level.values()) {
            if (level.name().equalsIgnoreCase(s)) {
                return level;
            }
        }
        return null;
    }
}