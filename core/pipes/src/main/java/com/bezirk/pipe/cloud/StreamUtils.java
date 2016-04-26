package com.bezirk.pipe.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class StreamUtils {
    protected static final Logger logger = LoggerFactory.getLogger(StreamUtils.class);

    private StreamUtils() {
        // private constructor to prevent instantiation of this utility class
    }

    /**
     * @param is
     * @return
     */
    public static String getStringFromInputStream(InputStream is) {
        final StringBuilder sb = new StringBuilder();

        String line;
        try  (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            logger.error("Exception in reading stream", e);
        }

        return sb.toString();
    }
}
