package com.quizapp.service;

import com.quizapp.data.DataManager;
import com.quizapp.model.Difficulty;
import com.quizapp.model.MultipleChoiceQuestion;
import com.quizapp.model.Question;
import com.quizapp.model.TrueFalseQuestion;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for question CRUD operations and statistics.
 */
public class QuestionService {
    private final DataManager dataManager;

    public QuestionService() {
        this.dataManager = DataManager.getInstance();
    }

    /**
     * Validates a question object.
     *
     * @param question the question to validate
     * @return true if valid
     */
    public boolean validateQuestion(Question question) {
        return question != null && question.isValid();
    }

    /**
     * Gets validation error for a question.
     *
     * @param question the question to check
     * @return error message or null if valid
     */
    public String getValidationError(Question question) {
        if (question == null) {
            return "Question cannot be null.";
        }
        return question.getValidationError();
    }

    /**
     * Adds a new question to the system.
     *
     * @param question the question to add
     * @return true if successfully added
     */
    public boolean addQuestion(Question question) {
        if (!validateQuestion(question)) {
            return false;
        }
        dataManager.addQuestion(question);
        return true;
    }

    /**
     * Deletes a question by index.
     *
     * @param index the index of the question
     * @return true if deleted
     */
    public boolean deleteQuestion(int index) {
        return dataManager.deleteQuestion(index);
    }

    public List<Question> getAllQuestions() {
        return dataManager.getAllQuestions();
    }

    /**
     * Filters questions by difficulty level.
     *
     * @param difficulty the difficulty to filter by
     * @return filtered list
     */
    public List<Question> filterByDifficulty(Difficulty difficulty) {
        return dataManager.getAllQuestions().stream()
                .filter(q -> q.getDifficulty() == difficulty)
                .collect(Collectors.toList());
    }

    /**
     * Filters questions by type.
     *
     * @param isMultipleChoice true for MC, false for TF
     * @return filtered list
     */
    public List<Question> filterByType(boolean isMultipleChoice) {
        return dataManager.getAllQuestions().stream()
                .filter(q -> isMultipleChoice
                        ? q instanceof MultipleChoiceQuestion
                        : q instanceof TrueFalseQuestion)
                .collect(Collectors.toList());
    }

    /**
     * Searches questions by keyword.
     *
     * @param keyword the keyword to search for
     * @return matching questions
     */
    public List<Question> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllQuestions();
        }
        return dataManager.getAllQuestions().stream()
                .filter(q -> q.search(keyword))
                .collect(Collectors.toList());
    }

    /**
     * Moves a question from one position to another.
     *
     * @param fromIndex source position
     * @param toIndex   destination position
     * @return true if moved
     */
    public boolean moveQuestion(int fromIndex, int toIndex) {
        return dataManager.moveQuestion(fromIndex, toIndex);
    }

    /**
     * Gets statistics about questions.
     *
     * @return statistics record
     */
    public QuestionStatistics getStatistics() {
        List<Question> questions = dataManager.getAllQuestions();

        int total = questions.size();
        long mcCount = questions.stream().filter(q -> q instanceof MultipleChoiceQuestion).count();
        long tfCount = questions.stream().filter(q -> q instanceof TrueFalseQuestion).count();
        long easyCount = questions.stream().filter(q -> q.getDifficulty() == Difficulty.EASY).count();
        long mediumCount = questions.stream().filter(q -> q.getDifficulty() == Difficulty.MEDIUM).count();
        long hardCount = questions.stream().filter(q -> q.getDifficulty() == Difficulty.HARD).count();

        return new QuestionStatistics(total, (int) mcCount, (int) tfCount,
                (int) easyCount, (int) mediumCount, (int) hardCount);
    }

    public record QuestionStatistics(int totalQuestions, int multipleChoice, int trueFalse,
            int easyCount, int mediumCount, int hardCount) {
    }
}
