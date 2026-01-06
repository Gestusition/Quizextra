package com.quizapp.interfaces;

import com.quizapp.data.DataManager;
import com.quizapp.model.GradeScale;

/**
 * Interface for objects that can calculate and grade a score.
 */
public interface Gradable {

    /**
     * Calculates and returns the score.
     *
     * @return the calculated score as a percentage (0-100)
     */
    double calculateScore();

    /**
     * Gets the maximum possible score.
     *
     * @return maximum score value
     */
    default double getMaxScore() {
        return 100.0;
    }

    /**
     * Gets a letter grade based on the score using configured scale.
     *
     * @return letter grade (AA, BA, BB, CB, CC, DC, DD, FD, FF)
     */
    default String getLetterGrade() {
        GradeScale scale = DataManager.getInstance().getGradeScale();
        return scale.getLetterGrade(calculateScore());
    }

    /**
     * Gets the status (PASS/CONDITIONAL/FAIL) based on score.
     *
     * @return status string
     */
    default String getGradeStatus() {
        GradeScale scale = DataManager.getInstance().getGradeScale();
        return scale.getStatus(calculateScore());
    }

    /**
     * Checks if the score is passing based on configured passing grade.
     *
     * @return true if score meets or exceeds passing threshold
     */
    default boolean isPassing() {
        GradeScale scale = DataManager.getInstance().getGradeScale();
        return scale.isPassing(calculateScore());
    }
}
