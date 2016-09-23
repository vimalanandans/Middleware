package com.bezirk.middleware.java;

import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.java.proxy.BezirkMiddleware;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    private final static String DEFAULT_HOSTNAME = "UNKNOWN-PC";
    private static String hostname = DEFAULT_HOSTNAME;

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.println("Select\n1. Publisher Zirk\n2. Subscriber Zirk\n3. Publisher and Subscriber together\n4. Exit");
        try {
            int n = reader.nextInt();
            startAppication(n);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input entered");
        }
    }

    private static void startAppication(int option) {
        switch (option) {
            case 1:
                init();
                new Publisher();
                break;
            case 2:
                init();
                new Subscriber();
                break;
            case 3:
                init();
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

    private static void init() {
        Config config = new Config.ConfigBuilder().setLogLevel(Config.Level.DEBUG).setPackageLogLevel("com.bezirk.middleware.core.comms", Config.Level.INFO).create();
        BezirkMiddleware.initialize(config);
    }

    public static String getHostName() {
        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        } catch (UnknownHostException ex) {
            System.out.println("Hostname can not be resolved");
        }
        return hostname;
    }
}
