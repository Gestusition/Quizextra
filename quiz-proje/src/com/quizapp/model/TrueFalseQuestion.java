package com.quizapp.model;

import com.quizapp.interfaces.Exportable;
import com.quizapp.util.CsvUtils;

/**
 * True/False question. Accepts T, F, true, false (case-insensitive).
 */
public class TrueFalseQuestion extends Question implements Exportable {

    private final boolean correctAnswer;

    /**
     * Creates a true/false question with default difficulty.
     *
     * @param questionText  the question text
     * @param correctAnswer true if the correct answer is "True"
     */
    public TrueFalseQuestion(String questionText, boolean correctAnswer) {
        super(questionText);
        this.correctAnswer = correctAnswer;
    }

    /**
     * Creates a true/false question with specified difficulty.
     *
     * @param questionText  the question text
     * @param correctAnswer true if the correct answer is "True"
     * @param difficulty    the difficulty level
     */
    public TrueFalseQuestion(String questionText, boolean correctAnswer, Difficulty difficulty) {
        super(questionText, difficulty);
        this.correctAnswer = correctAnswer;
    }

    public boolean getCorrectAnswer() {
        return correctAnswer;
    }

    @Override
    public boolean checkAnswer(String answer) {
        if (answer == null)
            return false;
        String trimmed = answer.trim().toLowerCase();
        if (trimmed.equals("true") || trimmed.equals("t")) {
            return correctAnswer;
        } else if (trimmed.equals("false") || trimmed.equals("f")) {
            return !correctAnswer;
        }
        return false;
    }

    @Override
    public String getCorrectAnswerDisplay() {
        return correctAnswer ? "True" : "False";
    }

    @Override
    public String getTypeName() {
        return "True/False";
    }

    /**
     * Checks if the answer format is valid (T/F/true/false).
     *
     * @param answer the answer to validate
     * @return true if valid format
     */
    public boolean isValidAnswer(String answer) {
        if (answer == null)
            return false;
        String trimmed = answer.trim().toLowerCase();
        return trimmed.equals("true") || trimmed.equals("false") ||
                trimmed.equals("t") || trimmed.equals("f");
    }

    @Override
    public String getDisplayDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Question: ").append(getQuestionText()).append("\n");
        sb.append("Type: True/False\n");
        sb.append("Difficulty: ").append(getDifficulty().getDisplayName()).append("\n");
        sb.append("Answer: ").append(getCorrectAnswerDisplay()).append("\n");
        return sb.toString();
    }

    @Override
    public String toCsvLine() {
        return "TF," + CsvUtils.escapeCsv(getQuestionText()) + "," + correctAnswer + "," + getDifficulty().name();
    }

    @Override
    public String getCsvHeader() {
        return "TYPE,QUESTION,ANSWER,DIFFICULTY";
    }
}
