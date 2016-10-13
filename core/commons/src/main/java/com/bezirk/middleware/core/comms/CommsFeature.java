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
package com.bezirk.middleware.core.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public enum CommsFeature {
    COMMS_SECURE,

    COMMS_BEZIRK,

    COMMS_ZYRE,

    // zyre-jni is platform specific (as of now). hence this needs to handled in java-build or android build
    // comms-jni will be platform specific
    COMMS_ZYRE_JNI,

    // comms jyre implementation
    COMMS_JYRE,

    WIRE_MSG_COMPRESSION,

    WIRE_MSG_ENCRYPTION,

    HTTP_BEZIRK_COMMS;

    private static final Logger logger = LoggerFactory.getLogger(CommsFeature.class);
    private static final Properties properties = new Properties();
    private String value;

    private void init() {
        try {
            //properties.load(Constants.class.getResourceAsStream(PATH));
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            properties.load(loader.getResourceAsStream("features.properties"));
        } catch (IOException e) {
            logger.error("Unable to load features.properties file from classpath.", e);
        }

        value = (String) properties.get(this.toString());
    }

    public String getValue() {
        if (value == null) {
            init();
        }
        return value;
    }

    /**
     * assuming everything boolean.
     */
    public boolean isActive() {
        if (value == null) {
            init();
        }

        return value != null && value.equalsIgnoreCase("true");
    }
}
