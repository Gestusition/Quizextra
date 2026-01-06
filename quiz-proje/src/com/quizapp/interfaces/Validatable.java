package com.quizapp.interfaces;

/**
 * Interface for objects with validation logic.
 */
public interface Validatable {

    /**
     * Checks if this object is valid.
     *
     * @return true if valid
     */
    boolean isValid();

    /**
     * Gets the validation error message.
     *
     * @return error message or null if valid
     */
    String getValidationError();
}
