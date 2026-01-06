package com.quizapp.model;

import com.quizapp.interfaces.Displayable;
import com.quizapp.interfaces.Identifiable;
import com.quizapp.interfaces.Searchable;
import com.quizapp.interfaces.Validatable;

/**
 * Abstract base class for quiz questions.
 * Subclasses: MultipleChoiceQuestion, TrueFalseQuestion, FillBlankQuestion
 */
public abstract class Question implements Searchable, Validatable, Displayable, Identifiable {

    private static int idCounter = 1;
    private final String questionId;
    private final String questionText;
    private Difficulty difficulty;

    /**
     * Creates a question with default MEDIUM difficulty.
     *
     * @param questionText the question text
     */
    public Question(String questionText) {
        this.questionId = "Q" + (idCounter++);
        this.questionText = questionText;
        this.difficulty = Difficulty.MEDIUM;
    }

    /**
     * Creates a question with specified difficulty.
     *
     * @param questionText the question text
     * @param difficulty   the difficulty level
     */
    public Question(String questionText, Difficulty difficulty) {
        this.questionId = "Q" + (idCounter++);
        this.questionText = questionText;
        this.difficulty = difficulty != null ? difficulty : Difficulty.MEDIUM;
    }

    public String getQuestionText() {
        return questionText;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty != null ? difficulty : Difficulty.MEDIUM;
    }

    /**
     * Checks if the provided answer is correct.
     *
     * @param answer the user's answer
     * @return true if correct
     */
    public abstract boolean checkAnswer(String answer);

    /**
     * Returns a display string for the correct answer.
     *
     * @return the correct answer formatted for display
     */
    public abstract String getCorrectAnswerDisplay();

    @Override
    public String getId() {
        return questionId;
    }

    @Override
    public String getTypeName() {
        return "Question";
    }

    @Override
    public boolean search(String query) {
        if (query == null)
            return false;
        return questionText.toLowerCase().contains(query.toLowerCase());
    }

    @Override
    public boolean isValid() {
        return questionText != null && !questionText.trim().isEmpty();
    }

    @Override
    public String getValidationError() {
        if (questionText == null || questionText.trim().isEmpty()) {
            return "Question text cannot be empty.";
        }
        return null;
    }

    @Override
    public String getDisplaySummary() {
        String type;
        if (this instanceof MultipleChoiceQuestion) {
            type = "MC";
        } else if (this instanceof TrueFalseQuestion) {
            type = "TF";
        } else if (this instanceof FillBlankQuestion) {
            type = "FB";
        } else {
            type = "??";
        }
        return "[" + difficulty + "/" + type + "] " + truncate(questionText, 50);
    }

    @Override
    public String getDisplayDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Question: ").append(questionText).append("\n");
        sb.append("Difficulty: ").append(difficulty.getDisplayName()).append("\n");
        sb.append("Correct Answer: ").append(getCorrectAnswerDisplay()).append("\n");
        return sb.toString();
    }

    private String truncate(String text, int maxLen) {
        if (text == null)
            return "";
        if (text.length() <= maxLen)
            return text;
        return text.substring(0, maxLen - 3) + "...";
    }
}
