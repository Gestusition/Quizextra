package com.quizapp.model;

import com.quizapp.interfaces.Exportable;
import com.quizapp.util.CsvUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Fill-in-the-blank question. Supports multiple acceptable answers
 * (case-insensitive).
 */
public class FillBlankQuestion extends Question implements Exportable {

    private final List<String> acceptableAnswers;

    /**
     * Creates a fill-in-the-blank question with a single correct answer.
     *
     * @param questionText  the question text (use ___ for blanks)
     * @param correctAnswer the correct answer
     */
    public FillBlankQuestion(String questionText, String correctAnswer) {
        super(questionText);
        this.acceptableAnswers = new ArrayList<>();
        if (correctAnswer != null && !correctAnswer.trim().isEmpty()) {
            this.acceptableAnswers.add(correctAnswer.trim());
        }
    }

    /**
     * Creates a fill-in-the-blank question with difficulty.
     *
     * @param questionText  the question text
     * @param correctAnswer the correct answer
     * @param difficulty    the difficulty level
     */
    public FillBlankQuestion(String questionText, String correctAnswer, Difficulty difficulty) {
        super(questionText, difficulty);
        this.acceptableAnswers = new ArrayList<>();
        if (correctAnswer != null && !correctAnswer.trim().isEmpty()) {
            this.acceptableAnswers.add(correctAnswer.trim());
        }
    }

    /**
     * Creates a fill-in-the-blank question with multiple acceptable answers.
     *
     * @param questionText      the question text
     * @param acceptableAnswers list of acceptable answers
     * @param difficulty        the difficulty level
     */
    public FillBlankQuestion(String questionText, List<String> acceptableAnswers, Difficulty difficulty) {
        super(questionText, difficulty);
        this.acceptableAnswers = new ArrayList<>();
        if (acceptableAnswers != null) {
            for (String ans : acceptableAnswers) {
                if (ans != null && !ans.trim().isEmpty()) {
                    this.acceptableAnswers.add(ans.trim());
                }
            }
        }
    }

    public List<String> getAcceptableAnswers() {
        return new ArrayList<>(acceptableAnswers);
    }

    public String getPrimaryAnswer() {
        return acceptableAnswers.isEmpty() ? "" : acceptableAnswers.get(0);
    }

    @Override
    public boolean checkAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            return false;
        }
        String trimmed = answer.trim().toLowerCase();
        for (String acceptable : acceptableAnswers) {
            if (acceptable.toLowerCase().equals(trimmed)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getCorrectAnswerDisplay() {
        if (acceptableAnswers.size() == 1) {
            return acceptableAnswers.get(0);
        }
        return String.join(" / ", acceptableAnswers);
    }

    @Override
    public String getTypeName() {
        return "Fill Blank";
    }

    @Override
    public boolean isValid() {
        if (!super.isValid())
            return false;
        return !acceptableAnswers.isEmpty();
    }

    @Override
    public String getValidationError() {
        String superError = super.getValidationError();
        if (superError != null)
            return superError;
        if (acceptableAnswers.isEmpty()) {
            return "At least one correct answer is required.";
        }
        return null;
    }

    @Override
    public String getDisplayDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Question: ").append(getQuestionText()).append("\n");
        sb.append("Type: Fill in the Blank\n");
        sb.append("Difficulty: ").append(getDifficulty().getDisplayName()).append("\n");
        if (acceptableAnswers.size() == 1) {
            sb.append("Answer: ").append(acceptableAnswers.get(0)).append("\n");
        } else {
            sb.append("Acceptable Answers:\n");
            for (String ans : acceptableAnswers) {
                sb.append("  - ").append(ans).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public String toCsvLine() {
        StringBuilder sb = new StringBuilder();
        sb.append("FB,").append(CsvUtils.escapeCsv(getQuestionText())).append(",");
        sb.append(getDifficulty().name());
        for (String ans : acceptableAnswers) {
            sb.append(",").append(CsvUtils.escapeCsv(ans));
        }
        return sb.toString();
    }

    @Override
    public String getCsvHeader() {
        return "TYPE,QUESTION,DIFFICULTY,ANSWER1,ANSWER2,...";
    }
}
