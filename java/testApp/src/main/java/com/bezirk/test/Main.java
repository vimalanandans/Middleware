package com.bezirk.test;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
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
                new Publisher();
                break;
            case 2:
                new Subscriber();
                break;
            case 3:
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
}
