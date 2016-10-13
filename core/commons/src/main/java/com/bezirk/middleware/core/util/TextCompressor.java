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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class TextCompressor {
    private static final Logger logger = LoggerFactory.getLogger(TextCompressor.class);

    /**
     * Uses a gzip compression to compress the String, a better option for a lengthier string. But the
     * smaller string will have a space overhead. and returns a compressed byte[]
     */
    public static byte[] compress(byte[] str) {
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        if (str == null || str.length == 0) {
            return str;
        }
        try {
            GZIPOutputStream gzip = new GZIPOutputStream(obj);
            gzip.write(str);
            gzip.close();
        } catch (IOException e) {
            logger.error("Exception while compressing the bytes", e);

        }
        return obj.toByteArray();
    }

    /**
     * decompress the byte[] to the String format.
     */
    public static String decompress(byte[] str) {
        if (str == null || str.length == 0) {
            return "";
        }

        final StringBuilder outStr = new StringBuilder();

        try {
            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str));
            BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
            String line;
            while ((line = bf.readLine()) != null) {
                outStr.append(line);
            }
        } catch (IOException e) {
            logger.error("Exception while decompressing the bytes", e);
        }

        return outStr.toString();
    }


    /**
     * compresses the input byte[] using deflater feature.
     */
    public static byte[] compressByteArrayUsingDeflater(byte[] bytes) {
        Deflater dfl = new Deflater();
        dfl.setLevel(Deflater.BEST_COMPRESSION);
        dfl.setInput(bytes);
        dfl.finish();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[4 * 1024];
        try {
            while (!dfl.finished()) {
                int size = dfl.deflate(tmp);
                baos.write(tmp, 0, size);
            }
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                logger.error("Exception while closing ByteArrayOutputStream", e);
            }
        }

        return baos.toByteArray();
    }
}
