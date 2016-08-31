package com.bezirk.middleware.java;

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
        } catch (UnknownHostException ex) {
            System.out.println("Hostname can not be resolved");
        }
        return hostname;
    }
}
