package com.bezirk.middleware.java;

import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.java.logging.LoggingManager;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class LoggingManagerTest {

    @Test
    public void test() {
        Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        String currentPackageName = getClass().getPackage().getName();
        Logger currentPackageLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(currentPackageName);

        System.out.println("Root logger level '" + rootLogger.getLevel() + "', current Package existing log level '" + currentPackageLogger.getLevel() + "'");
        currentPackageLogger.trace("This line should print only if the root logger level is '" + Level.TRACE + "'");
        currentPackageLogger.setLevel(Level.TRACE);
        System.out.println("Current Package level set to '" + currentPackageLogger.getLevel() + "'");
        currentPackageLogger.trace("This line should definitely print");
    }

    @Test
    public void test1() {
        LoggingManager loggingManager = new LoggingManager(new Config.ConfigBuilder().setLogLevel(Config.Level.INFO).create());
        loggingManager.configure();
    }
}
