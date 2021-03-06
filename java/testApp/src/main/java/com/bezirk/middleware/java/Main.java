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
    private static volatile String hostname = DEFAULT_HOSTNAME;

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in, "UTF-8");
        System.out.println("Select\n1. Publisher Zirk\n2. Subscriber Zirk\n3. Publisher and Subscriber together\n4. Exit");
        try {
            int n = reader.nextInt();
            startApplication(n);
        } catch (InputMismatchException e) {
            logger.error("Invalid input entered", e);
        }
        reader.close();
    }

    private static void startApplication(int option) {
        switch (option) {
            case 1:
                BezirkMiddleware.initialize();
                new Publisher();
                break;
            case 2:
                BezirkMiddleware.initialize();
                new Subscriber();
                break;
            case 3:
                BezirkMiddleware.initialize();
                new Publisher();
                new Subscriber();
                break;
            case 4:
                System.out.println("Exiting");
                return;
            default:
                System.out.println("Invalid option selected, Exiting");
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
