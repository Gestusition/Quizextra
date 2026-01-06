package com.quizapp.model;

/**
 * Represents difficulty levels for quiz questions.
 */
public enum Difficulty {
    EASY("Easy", 1),
    MEDIUM("Medium", 2),
    HARD("Hard", 3);

    private final String displayName;
    private final int value;

    Difficulty(String displayName, int value) {
        this.displayName = displayName;
        this.value = value;
    }

    /**
     * Gets the display name for the difficulty level.
     *
     * @return human-readable difficulty name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the numeric value of the difficulty.
     *
     * @return difficulty value (1=easy, 2=medium, 3=hard)
     */
    public int getValue() {
        return value;
    }

    /**
     * Parses a difficulty from string input.
     *
     * @param input the input string (name or number)
     * @return the matching Difficulty or MEDIUM as default
     */
    public static Difficulty fromString(String input) {
        if (input == null) {
            return MEDIUM;
        }
        String trimmed = input.trim().toUpperCase();

        // Try by name
        for (Difficulty d : values()) {
            if (d.name().equals(trimmed) || d.displayName.equalsIgnoreCase(trimmed)) {
                return d;
            }
        }

        // Try by number
        try {
            int num = Integer.parseInt(trimmed);
            for (Difficulty d : values()) {
                if (d.value == num) {
                    return d;
                }
            }
        } catch (NumberFormatException ignored) {
        }

        return MEDIUM; // Default
    }

    @Override
    public String toString() {
        return displayName;
    }
}
