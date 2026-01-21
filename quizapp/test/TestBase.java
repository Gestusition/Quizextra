package com.quizapp.test;

/**
 * Base class for all tests with assertion utilities.
 */
public abstract class TestBase {
    protected int passCount = 0;
    protected int failCount = 0;

    protected void assertTrue(boolean condition, String message) {
        if (condition) {
            passCount++;
        } else {
            failCount++;
            System.err.println("  ✗ FAIL: " + message);
        }
    }

    protected void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    protected void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) {
            passCount++;
        } else if (expected != null && expected.equals(actual)) {
            passCount++;
        } else {
            failCount++;
            System.err.println("  ✗ FAIL: " + message + " (expected: " + expected + ", got: " + actual + ")");
        }
    }

    protected void assertNull(Object obj, String message) {
        assertTrue(obj == null, message);
    }

    protected void assertNotNull(Object obj, String message) {
        assertTrue(obj != null, message);
    }

    public abstract void runTests();

    public int getPassCount() {
        return passCount;
    }

    public int getFailCount() {
        return failCount;
    }
}
