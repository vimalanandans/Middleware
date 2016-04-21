package com.bezirk.pipe.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class StreamUtils {
    protected static Logger log = LoggerFactory.getLogger(StreamUtils.class);

    private StreamUtils() {
        // private constructor to prevent instantiation of this utility class
    }

    /**
     * @param is
     * @return
     */
    public static String getStringFromInputStream(InputStream is) {


        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.error("Exception in reading stream \n", e);

        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("Exception in closing resourses \n", e);
                }
            }
        }

        return sb.toString();
    }

}
