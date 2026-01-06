package com.quizapp.main;

import com.quizapp.handler.MenuHandler;
import com.quizapp.handler.StartupHandler;

/**
 * Main entry point for the Quiz Application.
 * 
 */
public class QuizApp {

        public static void main(String[] args) {

                StartupHandler startupHandler = new StartupHandler();
                startupHandler.initialize();

                MenuHandler menuHandler = new MenuHandler();
                menuHandler.runMainMenu();
        }
}
