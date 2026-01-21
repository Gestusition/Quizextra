package com.quizapp.handler;

import com.quizapp.data.ActivityLogger;
import com.quizapp.data.DataManager;
import com.quizapp.model.Difficulty;
import com.quizapp.model.GradeScale;
import com.quizapp.model.MultipleChoiceQuestion;
import com.quizapp.model.Question;
import com.quizapp.model.Student;
import com.quizapp.model.TrueFalseQuestion;
import java.util.List;
import java.util.Scanner;

/**
 * Handles quiz-related operations and statistics display.
 */
public class QuizHandler {
    private final Scanner scanner;
    private final DataManager dataManager;

    public QuizHandler(Scanner scanner) {
        this.scanner = scanner;
        this.dataManager = DataManager.getInstance();
    }

    public void showFileStats() {
        System.out.println("\n============================================");
        System.out.println("           SYSTEM STATISTICS                ");
        System.out.println("============================================\n");

        showStudentStats();
        showQuestionStats();
        showQuizHistoryStats();
        showActivityStats();
        showQuizSettingsStats();

        System.out.println("============================================");
        MenuHandler.pauseAndContinue(scanner);
    }

    private void showStudentStats() {
        List<Student> students = dataManager.getAllStudents();
        GradeScale gradeScale = dataManager.getGradeScale();

        System.out.println("--- STUDENTS ---");
        System.out.println("Total: " + students.size());

        if (!students.isEmpty()) {
            long weakPasswords = students.stream().filter(s -> s.getPassword().length() < 5).count();
            double avgScore = students.stream().mapToDouble(Student::getScore).average().orElse(0.0);
            double maxScore = students.stream().mapToDouble(Student::getScore).max().orElse(0.0);
            long passing = students.stream().filter(s -> gradeScale.isPassing(s.getScore())).count();

            System.out.printf("  Avg Score: %.1f%% | Max: %.1f%%\n", avgScore, maxScore);
            System.out.println("  Passing (≥" + gradeScale.getPassingGrade() + "): " + passing + "/" + students.size());
            if (weakPasswords > 0) {
                System.out.println("  ⚠ Weak passwords: " + weakPasswords);
            }
        }
        System.out.println();
    }

    private void showQuestionStats() {
        List<Question> questions = dataManager.getAllQuestions();

        System.out.println("--- QUESTIONS ---");
        System.out.println("Total: " + questions.size());

        if (!questions.isEmpty()) {
            long mc = questions.stream().filter(q -> q instanceof MultipleChoiceQuestion).count();
            long tf = questions.stream().filter(q -> q instanceof TrueFalseQuestion).count();
            long easy = questions.stream().filter(q -> q.getDifficulty() == Difficulty.EASY).count();
            long medium = questions.stream().filter(q -> q.getDifficulty() == Difficulty.MEDIUM).count();
            long hard = questions.stream().filter(q -> q.getDifficulty() == Difficulty.HARD).count();

            System.out.println("  Type: MC=" + mc + ", TF=" + tf);
            System.out.println("  Difficulty: Easy=" + easy + ", Medium=" + medium + ", Hard=" + hard);
        }
        System.out.println();
    }

    private void showQuizHistoryStats() {
        ActivityLogger logger = dataManager.getActivityLogger();
        int totalQuizzes = logger.getTotalQuizCount();

        System.out.println("--- QUIZ HISTORY ---");
        System.out.println("Total Quizzes Taken: " + totalQuizzes);

        if (totalQuizzes > 0) {
            List<ActivityLogger.QuizRecord> recent = logger.getRecentQuizResults(5);
            System.out.println("Recent:");
            for (ActivityLogger.QuizRecord r : recent) {
                System.out.printf("  %s | %s | %.0f%% (%d/%d)\n",
                        r.timestamp().substring(5, 16), r.studentName(), r.score(), r.correct(), r.total());
            }
        }
        System.out.println();
    }

    private void showActivityStats() {
        ActivityLogger logger = dataManager.getActivityLogger();
        int totalActivities = logger.getTotalActivityCount();

        System.out.println("--- ACTIVITY LOG ---");
        System.out.println("Total Actions: " + totalActivities);

        if (totalActivities > 0) {
            List<ActivityLogger.ActivityRecord> recent = logger.getRecentActivities(5);
            System.out.println("Recent:");
            for (ActivityLogger.ActivityRecord r : recent) {
                System.out.printf("  %s | %s | %s | %s\n",
                        r.timestamp().substring(5, 16), r.actor(), r.action(), r.target());
            }
        }
        System.out.println();
    }

    private void showQuizSettingsStats() {
        System.out.println("--- SETTINGS ---");
        System.out.println(
                "Timer: " + (dataManager.isQuizTimerEnabled() ? dataManager.getQuizTimerSeconds() + "s" : "OFF"));
        System.out.println("Shuffle: " + (dataManager.isQuizShuffleEnabled() ? "ON" : "OFF"));
        System.out.println();
    }
}
