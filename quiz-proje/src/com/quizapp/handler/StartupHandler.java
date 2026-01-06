package com.quizapp.handler;

/**
 * Handles application startup tasks.
 * Displays welcome message and initializes the application.
 * 
 * @author Quiz App Team
 * @version 1.0
 */
public class StartupHandler {

    /**
     * Constructs a new StartupHandler.
     */
    public StartupHandler() {
        // Empty constructor
    }

    /**
     * Displays the welcome message to the user.
     */
    public void displayWelcomeMessage() {
        System.out.println("Welcome to the Quiz System!");
    }

    /**
     * Performs startup initialization.
     * Currently displays the welcome message.
     */
    public void initialize() {
        displayWelcomeMessage();
    }
}
