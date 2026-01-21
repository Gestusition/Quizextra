package com.quizapp.interfaces;

/**
 * Interface for objects with display formatting.
 */
public interface Displayable {

    /**
     * Returns a short summary for lists.
     *
     * @return short display string
     */
    String getDisplaySummary();

    /**
     * Returns detailed information.
     *
     * @return detailed display string
     */
    String getDisplayDetails();
}
