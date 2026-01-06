package com.quizapp.util;

/**
 * Utility class for CSV operations and application constants.
 */
public final class CsvUtils {

    /** Minimum timer duration in seconds. */
    public static final int TIMER_MIN_SECONDS = 30;

    /** Maximum timer duration in seconds (24 hours). */
    public static final int TIMER_MAX_SECONDS = 86400;

    /** Minimum password length for validation. */
    public static final int PASSWORD_MIN_LENGTH = 5;

    /** Default password for new students. */
    public static final String DEFAULT_PASSWORD = "12345";

    private CsvUtils() {
    }

    /**
     * Escapes text for CSV format. Wraps in quotes if contains comma or quote.
     *
     * @param text the text to escape
     * @return CSV-safe string
     */
    public static String escapeCsv(String text) {
        if (text == null) {
            return "";
        }
        if (text.contains(",") || text.contains("\"")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }

    /**
     * Removes CSV quotes and unescapes internal quotes.
     *
     * @param value the CSV value to parse
     * @return unescaped string value
     */
    public static String unescapeCsv(String value) {
        if (value == null) {
            return "";
        }
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }
}
