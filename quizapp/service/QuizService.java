package com.quizapp.service;

import com.quizapp.data.ActivityLogger;
import com.quizapp.data.DataManager;
import com.quizapp.model.Difficulty;
import com.quizapp.model.Question;
import com.quizapp.model.Quiz;
import com.quizapp.model.Student;
import java.util.List;

/**
 * Service for quiz operations and settings.
 */
public class QuizService {
    private final DataManager dataManager;
    private final QuestionService questionService;

    public QuizService() {
        this.dataManager = DataManager.getInstance();
        this.questionService = new QuestionService();
    }

    /**
     * Creates a new quiz with current system settings.
     *
     * @return configured Quiz object
     */
    public Quiz createQuiz() {
        Quiz quiz = new Quiz();
        quiz.setTimerEnabled(dataManager.isQuizTimerEnabled());
        quiz.setTimerSeconds(dataManager.getQuizTimerSeconds());
        quiz.setShuffleEnabled(dataManager.isQuizShuffleEnabled());
        return quiz;
    }

    /**
     * Creates a quiz filtered by difficulty.
     *
     * @param difficulty the difficulty level
     * @return Quiz with filtered questions, or null if empty
     */
    public Quiz createQuizByDifficulty(Difficulty difficulty) {
        List<Question> questions = questionService.filterByDifficulty(difficulty);
        if (questions.isEmpty()) {
            return null;
        }

        Quiz quiz = createQuiz();
        for (Question q : questions) {
            quiz.addQuestion(q);
        }
        quiz.setDifficultyMultiplier(difficulty.getValue());
        return quiz;
    }

    /**
     * Loads all questions into a quiz.
     *
     * @param quiz the quiz to load into
     * @return true if questions loaded
     */
    public boolean loadQuestions(Quiz quiz) {
        List<Question> questions = questionService.getAllQuestions();
        if (questions.isEmpty()) {
            return false;
        }
        for (Question q : questions) {
            quiz.addQuestion(q);
        }
        return true;
    }

    public String calculateLetterGrade(double score) {
        return dataManager.getGradeScale().getLetterGrade(score);
    }

    public String getGradeStatus(double score) {
        return dataManager.getGradeScale().getStatus(score);
    }

    public boolean isPassing(double score) {
        return dataManager.getGradeScale().isPassing(score);
    }

    /**
     * Saves the result of a completed quiz.
     *
     * @param student    the student who took the quiz
     * @param quiz       the completed quiz
     * @param difficulty the difficulty filter (null for all)
     */
    public void saveQuizResult(Student student, Quiz quiz, Difficulty difficulty) {
        String difficultyLabel = difficulty != null ? difficulty.name() : "ALL";
        dataManager.saveQuizResult(
                student,
                quiz.calculateScore(),
                quiz.getCorrectCount(),
                quiz.getQuestionsAttempted(),
                difficultyLabel);
    }

    /**
     * Gets current quiz settings.
     *
     * @return settings record
     */
    public QuizSettings getSettings() {
        return new QuizSettings(
                dataManager.isQuizTimerEnabled(),
                dataManager.getQuizTimerSeconds(),
                dataManager.isQuizShuffleEnabled());
    }

    public void setTimerEnabled(boolean enabled) {
        dataManager.setQuizTimerEnabled(enabled);
    }

    public void setTimerSeconds(int seconds) {
        dataManager.setQuizTimerSeconds(seconds);
    }

    public void setShuffleEnabled(boolean enabled) {
        dataManager.setQuizShuffleEnabled(enabled);
    }

    /**
     * Gets quiz history statistics.
     *
     * @return history statistics record
     */
    public QuizHistoryStatistics getHistoryStatistics() {
        ActivityLogger logger = dataManager.getActivityLogger();
        return new QuizHistoryStatistics(
                logger.getTotalQuizCount(),
                logger.getRecentQuizResults(5));
    }

    public record QuizSettings(boolean timerEnabled, int timerSeconds, boolean shuffleEnabled) {
    }

    public record QuizHistoryStatistics(int totalQuizzesTaken, List<ActivityLogger.QuizRecord> recentResults) {
    }
}
