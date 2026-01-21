package com.quizapp.test;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import java.io.PrintWriter;

/**
 * Main test runner that executes all JUnit 5 test classes.
 * Run this class to execute the full test suite.
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("       QUIZ APP TEST SUITE (JUnit 5)        ");
        System.out.println("============================================\n");

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage("com.quizapp.test"))
                .build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();

        System.out.println("\n============================================");
        System.out.println("              TEST SUMMARY                  ");
        System.out.println("============================================");
        System.out.println("Total Tests: " + summary.getTestsFoundCount());
        System.out.println("Passed: " + summary.getTestsSucceededCount());
        System.out.println("Failed: " + summary.getTestsFailedCount());
        System.out.println("Skipped: " + summary.getTestsSkippedCount());
        System.out.println("============================================");

        if (summary.getTestsFailedCount() > 0) {
            System.out.println("\n*** SOME TESTS FAILED ***");
            summary.printFailuresTo(new PrintWriter(System.err));
            System.exit(1);
        } else {
            System.out.println("\n*** ALL TESTS PASSED ***");
        }
    }
}
