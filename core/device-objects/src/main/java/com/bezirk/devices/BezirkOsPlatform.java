/*
 * @author mcataldo
 * @description the goal of this class is to have all the constants and methods required for figuring out what
 *              OS platform UhU and services are running on.
 */
package com.bezirk.devices;

/**
 * This class represents the names of the supported platforms
 * In Android we get "Dalvik Core Library"
 * In regular JVM we get "Java Platform API Specification"
 */
public final class BezirkOsPlatform {

    public final static String UPA_SERV__RUNTIME_ENV__ANDROID = "Dalvik Core Library";
    public final static String UPA_SERV__RUNTIME_ENV__JAVA = "Java Platform API Specification";

    private BezirkOsPlatform() {
        //this is a Utility class
    }

    /**
     * @return the name of the current running Platform
     */
    public static String getCurrentOSPlatform() {
        return System.getProperty("java.specification.name");
    }
}
