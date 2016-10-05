package com.bezirk.middleware.core.comms;

import org.slf4j.LoggerFactory;

import java.util.Set;

import ch.qos.logback.classic.Level;

public class Utils {
    public static int getNumberOfThreadsInSystem(boolean print) {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        if (print) {
            for (Thread t : threadSet) {
                System.out.println(t.getName() + t.getState() + t.getThreadGroup() + t.isAlive());
            }
        }
        return threadSet.size();
    }

    public static void setLogLevel(Level level) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(level);
    }
}
