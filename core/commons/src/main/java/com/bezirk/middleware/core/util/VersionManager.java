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
import java.util.Properties;

public class VersionManager {
    private transient static final Logger logger = LoggerFactory.getLogger(VersionManager.class);
    private static final String PROPERTIES = "version.properties";
    private static final String VERSION_KEY = "version";

    /**
     * Returns current bezirk middleware version
     * @return
     */
    public static final String getBezirkVersion(){
        final Properties bezirkProperties = new Properties();
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();

        String middlewareVersion = null;

        try (final InputStream is = loader.getResourceAsStream(PROPERTIES)) {
            if (is == null) {
                logger.error("Unable to find {} file ", PROPERTIES);
            } else {
                bezirkProperties.load(is);
                middlewareVersion = (String) bezirkProperties.get(VERSION_KEY);
            }
        } catch (NullPointerException e) {
            logger.error("Error reading {}", PROPERTIES, e);
        } catch (IOException e) {
            logger.error("Error reading {}", PROPERTIES, e);
        }
        return middlewareVersion;
    }
}

