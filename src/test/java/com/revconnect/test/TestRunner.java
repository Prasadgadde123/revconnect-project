package com.revconnect.test;

import com.revconnect.util.AppLogger;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

public class TestRunner {

    public static void main(String[] args) {
        AppLogger.info("Starting test execution...");

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(selectPackage("com.revconnect"))
                .build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();

        // Print test results
        printTestResults(summary);

        // Exit with appropriate code
        System.exit(summary.getTestsFailedCount() > 0 ? 1 : 0);
    }

    private static void printTestResults(TestExecutionSummary summary) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST EXECUTION SUMMARY");
        System.out.println("=".repeat(60));
        System.out.println("Total Tests: " + summary.getTestsFoundCount());
        System.out.println("Tests Passed: " + summary.getTestsSucceededCount());
        System.out.println("Tests Failed: " + summary.getTestsFailedCount());
        System.out.println("Tests Skipped: " + summary.getTestsSkippedCount());

        // Fixed: Added parentheses to properly calculate execution time
        long executionTime = summary.getTimeFinished() - summary.getTimeStarted();
        System.out.println("Execution Time: " + executionTime + "ms");

        if (summary.getTestsFailedCount() > 0) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("FAILED TESTS:");
            System.out.println("=".repeat(60));
            summary.getFailures().forEach(failure -> {
                System.out.println("Test: " + failure.getTestIdentifier().getDisplayName());
                System.out.println("Exception: " + failure.getException().getMessage());
                System.out.println("-".repeat(40));
            });
        }

        AppLogger.info("Test execution completed");
        AppLogger.info("Passed: " + summary.getTestsSucceededCount() +
                ", Failed: " + summary.getTestsFailedCount());
    }
}