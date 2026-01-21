package com.quizapp.interfaces;

/**
 * Interface for objects with unique ID.
 */
public interface Identifiable {

    /**
     * Gets the unique identifier.
     *
     * @return the unique ID
     */
    String getId();

    /**
     * Gets the type name for display.
     *
     * @return type name (e.g., "Student", "Question")
     */
    String getTypeName();
}
