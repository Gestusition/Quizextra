package com.quizapp.model;

import com.quizapp.interfaces.Exportable;
import com.quizapp.util.CsvUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Multiple choice question with 2+ options.
 */
public class MultipleChoiceQuestion extends Question implements Exportable {

    private final List<String> options;
    private final int correctOptionIndex;

    /**
     * Creates a multiple choice question with default difficulty.
     *
     * @param questionText       the question text
     * @param options            list of answer options (minimum 2)
     * @param correctOptionIndex 0-based index of the correct option
     */
    public MultipleChoiceQuestion(String questionText, List<String> options, int correctOptionIndex) {
        super(questionText);
        this.options = options != null ? new ArrayList<>(options) : new ArrayList<>();
        this.correctOptionIndex = correctOptionIndex;
    }

    /**
     * Creates a multiple choice question with specified difficulty.
     *
     * @param questionText       the question text
     * @param options            list of answer options
     * @param correctOptionIndex 0-based index of the correct option
     * @param difficulty         the difficulty level
     */
    public MultipleChoiceQuestion(String questionText, List<String> options,
            int correctOptionIndex, Difficulty difficulty) {
        super(questionText, difficulty);
        this.options = options != null ? new ArrayList<>(options) : new ArrayList<>();
        this.correctOptionIndex = correctOptionIndex;
    }

    public List<String> getOptions() {
        return new ArrayList<>(options);
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    /**
     * Checks answer. Input should be 1-based option number.
     */
    @Override
    public boolean checkAnswer(String answer) {
        try {
            int selected = Integer.parseInt(answer.trim());
            return (selected - 1) == correctOptionIndex;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String getCorrectAnswerDisplay() {
        if (correctOptionIndex >= 0 && correctOptionIndex < options.size()) {
            return (correctOptionIndex + 1) + ". " + options.get(correctOptionIndex);
        }
        return "Invalid";
    }

    @Override
    public String getTypeName() {
        return "Multiple Choice";
    }

    /**
     * Searches in question text and options.
     */
    @Override
    public boolean search(String query) {
        if (super.search(query))
            return true;
        if (options != null) {
            for (String opt : options) {
                if (opt.toLowerCase().contains(query.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isValid() {
        if (!super.isValid())
            return false;
        if (options == null || options.size() < 2)
            return false;
        return correctOptionIndex >= 0 && correctOptionIndex < options.size();
    }

    @Override
    public String getValidationError() {
        String superError = super.getValidationError();
        if (superError != null)
            return superError;
        if (options == null || options.size() < 2) {
            return "Must have at least 2 options.";
        }
        if (correctOptionIndex < 0 || correctOptionIndex >= options.size()) {
            return "Invalid correct option index.";
        }
        return null;
    }

    @Override
    public String getDisplayDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Question: ").append(getQuestionText()).append("\n");
        sb.append("Type: Multiple Choice\n");
        sb.append("Difficulty: ").append(getDifficulty().getDisplayName()).append("\n");
        sb.append("Options:\n");
        for (int i = 0; i < options.size(); i++) {
            String marker = (i == correctOptionIndex) ? " ✓" : "";
            sb.append("  ").append(i + 1).append(". ").append(options.get(i)).append(marker).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toCsvLine() {
        StringBuilder sb = new StringBuilder();
        sb.append("MC,").append(CsvUtils.escapeCsv(getQuestionText())).append(",");
        sb.append(correctOptionIndex).append(",");
        sb.append(getDifficulty().name());
        for (String opt : options) {
            sb.append(",").append(CsvUtils.escapeCsv(opt));
        }
        return sb.toString();
    }

    @Override
    public String getCsvHeader() {
        return "TYPE,QUESTION,CORRECT_INDEX,DIFFICULTY,OPTION1,OPTION2,...";
    }
}
