/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Bezirk versions holds all the versions of bezirk
 * will be used by all the components to
 * that uses the version to be compatible and process accordingly.
 * the main bezirk version is read from build file
 */
public class BezirkVersion {
    private transient static final Logger logger = LoggerFactory.getLogger(BezirkVersion.class);

    private static final String DEFAULT_BEZIRK_VERSION = "1.1";

    // BEZIRK_VERSION - read from build property file
    public static final String BEZIRK_VERSION;

    // Wire Message Version. increment it where there is a change in
    // wire message format (which would lead to crash while decoding)
    private static final String WIRE_MESSAGE_VERSION = "0.2";

    // DB Version for storing database
    // sphere / sadl registry changes.
    private static final String PERSISTENCE_VERSION = "0.1";

    static {
        // Fetch the version of this Bezirk middleware instance from version.properties
        final Properties bezirkProperties = new Properties();
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();

        String middlewareVersion = "";

        try (final InputStream is = loader.getResourceAsStream("version.properties")) {
            if (is == null) {
                logger.error("Unable to find bezirk version file. using default bezirk version " +
                        DEFAULT_BEZIRK_VERSION);
            } else {
                bezirkProperties.load(is);
                middlewareVersion = (String) bezirkProperties.get("BEZIRK_VERSION");
            }
        } catch (NullPointerException e) {
            logger.error("Error fetching resource stream for version.properties", e);
        } catch (IOException e) {
            logger.error("Error reading version.properties", e);
        }

        BEZIRK_VERSION = middlewareVersion.isEmpty() ? DEFAULT_BEZIRK_VERSION : middlewareVersion;
    }

    /**
     * @return the wire message version
     */
    public static String getWireVersion() {
        return WIRE_MESSAGE_VERSION;
    }

    /**
     * @return if the version is same
     */
    public static boolean isSameWireMessageVersion(String version) {
        return WIRE_MESSAGE_VERSION.equals(version);
    }

    /**
     * this method returns list of versions. Useful to display to user
     */
    public static Map<String, String> getAllVersion() {
        Map<String, String> versions = new HashMap<>();

        versions.put("BEZIRK_VERSION", BEZIRK_VERSION);

        versions.put("WIRE_MESSAGE_VERSION", WIRE_MESSAGE_VERSION);

        //versions.put(PERSISTENCE_VERSION.toString(),PERSISTENCE_VERSION);

        return versions;
    }

}

