package com.quizapp.handler;

import java.util.Scanner;

/**
 * Handles menu display and navigation utilities.
 */
public class MenuHandler {
    private final Scanner scanner;
    private final AdminHandler adminHandler;
    private final StudentHandler studentHandler;

    /**
     * Creates a new MenuHandler with shared Scanner.
     */
    public MenuHandler() {
        this.scanner = new Scanner(System.in);
        this.adminHandler = new AdminHandler(scanner);
        this.studentHandler = new StudentHandler(scanner);
    }

    /**
     * Pauses execution and waits for user to press Enter.
     *
     * @param scanner the scanner to read input from
     */
    public static void pauseAndContinue(Scanner scanner) {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Displays the main menu options.
     */
    public void printMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Admin Login");
        System.out.println("2. Student Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
    }

    /**
     * Runs the main application menu loop.
     */
    public void runMainMenu() {
        while (true) {
            printMainMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    adminHandler.adminLogin();
                    if (adminHandler.shouldExitApp()) {
                        return;
                    }
                    break;
                case "2":
                    studentHandler.studentLogin();
                    if (studentHandler.shouldExitApp()) {
                        return;
                    }
                    break;
                case "3":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Please enter a number in range (1-3).");
            }
        }
    }

    /**
     * Gets the scanner instance.
     *
     * @return the scanner
     */
    public Scanner getScanner() {
        return scanner;
    }
}
