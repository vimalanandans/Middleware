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
package com.bezirk.middleware.java;

import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.java.proxy.BezirkMiddleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String DEFAULT_HOSTNAME = "UNKNOWN-PC";
    private static final String BOLD_ESCAPE_SEQUENCE = "\033[0;1m";
    private static final String NORMAL_ESCAPE_SEQUENCE = "\033[0;8m";
    private static volatile String hostname = DEFAULT_HOSTNAME;
    private static String channelId;

    public static void main(String[] args) {
        if (args.length == 0 || args[0] == null) {
            System.out.println(BOLD_ESCAPE_SEQUENCE + "\nChannel Id is mandatory.\n" + NORMAL_ESCAPE_SEQUENCE);
            System.out.println("To run the test, run " + BOLD_ESCAPE_SEQUENCE + " java -jar test.jar <channelId>\n" + NORMAL_ESCAPE_SEQUENCE);
            //System.out.println("To change the log level, run " + BOLD_ESCAPE_SEQUENCE + " java -Dloglevel=<loglevel> -jar path/to/test.jar <channelId>" + NORMAL_ESCAPE_SEQUENCE);
            //System.out.println("Ex. To run with debug loglevel, run " + BOLD_ESCAPE_SEQUENCE + " java -Dloglevel=debug -jar path/to/test.jar <channelId>\n" + NORMAL_ESCAPE_SEQUENCE);
            return;
        }
        channelId = args[0];
        Scanner reader = new Scanner(System.in, "UTF-8");
        System.out.println("Select\n1. Test Sending\n2. Test Receiving\n3. Exit");
        try {
            int n = reader.nextInt();
            startApplication(n);
        } catch (InputMismatchException e) {
            logger.error("Invalid input entered", e);
        }
        reader.close();
    }

    private static void startApplication(final int option) {
        switch (option) {
            case 1:
                initializeBezirk();
                new Publisher();
                break;
            case 2:
                initializeBezirk();
                new Subscriber();
                break;
            case 3:
                System.out.println("Exiting");
                return;
            default:
                System.out.println("Invalid option selected, Exiting");
        }
    }

    private static void initializeBezirk() {
        if (channelId != null) {
            System.out.println("Initializing Bezirk with channelId '" + channelId + "'\n");
            Config config = new Config.ConfigBuilder().setPackageLogLevel("com.j256.ormlite", Config.Level.ERROR).setGroupName(channelId).create();
            BezirkMiddleware.initialize(config);

        } else {
            //for the hackathon this line would never reach as channelId is mandatory
            BezirkMiddleware.initialize();
        }
    }

    public static String getHostName() {
        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        } catch (UnknownHostException e) {
            logger.error("Hostname can not be resolved", e);
        }
        return hostname;
    }
}
