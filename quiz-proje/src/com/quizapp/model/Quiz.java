package com.quizapp.model;

import com.quizapp.interfaces.Gradable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a quiz containing multiple questions.
 * Supports timer, shuffle, and difficulty-based scoring.
 */
public class Quiz implements Gradable {
    private final List<Question> questions;
    private int correctAnswersCount;
    private int totalQuestionsAttempted;

    private boolean shuffleEnabled;
    private boolean timerEnabled;
    private int timerSeconds;
    private long startTime;
    private boolean timeExpired;
    private int difficultyMultiplier = 1;

    // Store detailed results for review
    private final List<QuestionResult> sessionResults;

    public static class QuestionResult {
        private final Question question;
        private final String userAnswer;
        private final boolean isCorrect;

        public QuestionResult(Question question, String userAnswer, boolean isCorrect) {
            this.question = question;
            this.userAnswer = userAnswer;
            this.isCorrect = isCorrect;
        }

        public Question getQuestion() {
            return question;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public boolean isCorrect() {
            return isCorrect;
        }
    }

    public Quiz() {
        this.questions = new ArrayList<>();
        this.sessionResults = new ArrayList<>();
        this.correctAnswersCount = 0;
        this.totalQuestionsAttempted = 0;
        this.shuffleEnabled = false;
        this.timerEnabled = false;
        this.timerSeconds = 0;
        this.timeExpired = false;
    }

    public void addQuestion(Question q) {
        questions.add(q);
    }

    public void setShuffleEnabled(boolean enabled) {
        this.shuffleEnabled = enabled;
    }

    public void setTimerEnabled(boolean enabled) {
        this.timerEnabled = enabled;
    }

    public void setTimerSeconds(int seconds) {
        this.timerSeconds = seconds;
    }

    public void setDifficultyMultiplier(int multiplier) {
        this.difficultyMultiplier = multiplier;
    }

    public int getCorrectCount() {
        return correctAnswersCount;
    }

    public int getQuestionsAttempted() {
        return totalQuestionsAttempted;
    }

    private boolean isTimeExpired() {
        if (!timerEnabled)
            return false;
        long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
        return elapsedSeconds >= timerSeconds;
    }

    private long getRemainingSeconds() {
        if (!timerEnabled)
            return -1;
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        return Math.max(0, timerSeconds - elapsed);
    }

    public void takeQuiz(Student s, Scanner scanner) {
        correctAnswersCount = 0;
        totalQuestionsAttempted = 0;
        timeExpired = false;
        sessionResults.clear();

        List<Question> quizQuestions = new ArrayList<>(questions);
        if (shuffleEnabled) {
            Collections.shuffle(quizQuestions);
        }

        System.out.println("\n==================================================");
        System.out.println("Starting quiz for: " + s.getName());
        System.out.println("Total questions: " + quizQuestions.size());
        if (timerEnabled) {
            System.out.println("Time limit: " + formatTime(timerSeconds));
            System.out.println("Note: Timer checks between questions.");
        }
        if (difficultyMultiplier > 1) {
            System.out.println("Score multiplier: x" + difficultyMultiplier);
        }
        System.out.println("==================================================\n");

        startTime = System.currentTimeMillis();

        for (int i = 0; i < quizQuestions.size(); i++) {
            if (timerEnabled && isTimeExpired()) {
                System.out.println("\n*** TIME'S UP! ***");
                timeExpired = true;
                break;
            }

            Question q = quizQuestions.get(i);

            if (timerEnabled) {
                System.out.println("[Time remaining: " + formatTime((int) getRemainingSeconds()) + "]");
            }

            System.out.println("Q" + (i + 1) + ": " + q.getDisplaySummary());
            System.out.println("   " + q.getQuestionText());

            String answer = "";
            boolean validInput = false;
            boolean isCorrect = false;

            if (q instanceof MultipleChoiceQuestion mcq) {
                List<String> options = mcq.getOptions();
                for (int j = 0; j < options.size(); j++) {
                    System.out.println("   " + (j + 1) + ". " + options.get(j));
                }

                while (!validInput) {
                    if (timerEnabled && isTimeExpired()) {
                        System.out.println("\n*** TIME'S UP! ***");
                        timeExpired = true;
                        break;
                    }

                    System.out.print("Answer (1-" + options.size() + "): ");
                    answer = scanner.nextLine().trim();
                    try {
                        int choice = Integer.parseInt(answer);
                        if (choice >= 1 && choice <= options.size()) {
                            validInput = true;
                            isCorrect = q.checkAnswer(answer);
                        } else {
                            System.out.println("Enter 1-" + options.size());
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Enter a number.");
                    }
                }
            } else if (q instanceof TrueFalseQuestion tfq) {
                while (!validInput) {
                    if (timerEnabled && isTimeExpired()) {
                        System.out.println("\n*** TIME'S UP! ***");
                        timeExpired = true;
                        break;
                    }

                    System.out.print("Answer (T/F): ");
                    answer = scanner.nextLine().trim();
                    if (tfq.isValidAnswer(answer)) {
                        validInput = true;
                        isCorrect = q.checkAnswer(answer);
                    } else {
                        System.out.println("Enter T/F");
                    }
                }
            } else if (q instanceof FillBlankQuestion) {
                while (!validInput) {
                    if (timerEnabled && isTimeExpired()) {
                        System.out.println("\n*** TIME'S UP! ***");
                        timeExpired = true;
                        break;
                    }

                    System.out.print("Answer: ");
                    answer = scanner.nextLine().trim();
                    if (!answer.isEmpty()) {
                        validInput = true;
                        isCorrect = q.checkAnswer(answer);
                    } else {
                        System.out.println("*** You must enter an answer to continue! ***");
                    }
                }
            }

            if (timeExpired)
                break;

            // Record result
            if (validInput) {
                sessionResults.add(new QuestionResult(q, answer, isCorrect));
                if (isCorrect) {
                    correctAnswersCount++;
                }
                // No immediate feedback
                System.out.println("Answer saved.");
            }

            totalQuestionsAttempted++;
            System.out.println();
        }

        double score = calculateScore();
        s.setScore(score);
    }

    public void showResults() {
        System.out.println("\n==================================================");
        System.out.println("                  QUIZ RESULTS                    ");
        System.out.println("==================================================");
        if (timeExpired) {
            System.out.println("  ** Time expired **");
        }
        System.out.println("  Questions: " + totalQuestionsAttempted + "/" + questions.size());
        System.out.println("  Correct:   " + correctAnswersCount);

        double baseScore = calculateBaseScore();
        double finalScore = calculateScore();

        if (difficultyMultiplier > 1) {
            System.out.printf("  Base Score: %.1f%%\n", baseScore);
            System.out.printf("  Multiplier: x%d\n", difficultyMultiplier);
        }
        System.out.printf("  FINAL SCORE: %.1f%%\n", finalScore);
        System.out.println("  Grade: " + getLetterGrade() + (isPassing() ? " (PASS)" : " (FAIL)"));
        System.out.println("==================================================");
    }

    public void showDetailedResults() {
        if (sessionResults.isEmpty()) {
            System.out.println("No details available.");
            return;
        }

        System.out.println("\n==================================================");
        System.out.println("             DETAILED ANSWER REVIEW               ");
        System.out.println("==================================================");

        for (int i = 0; i < sessionResults.size(); i++) {
            QuestionResult res = sessionResults.get(i);
            Question q = res.getQuestion();
            boolean correct = res.isCorrect();

            System.out.println("Q" + (i + 1) + ": " + q.getQuestionText() + " [" + q.getTypeName() + "]");
            System.out.println("   Your Answer: " + res.getUserAnswer());
            if (correct) {
                System.out.println("   Result:      ✓ CORRECT");
            } else {
                System.out.println("   Result:      ✗ WRONG");
                System.out.println("   Correct:     " + q.getCorrectAnswerDisplay());
            }
            System.out.println("--------------------------------------------------");
        }
    }

    private double calculateBaseScore() {
        if (totalQuestionsAttempted == 0)
            return 0.0;
        return ((double) correctAnswersCount / totalQuestionsAttempted) * 100.0;
    }

    @Override
    public double calculateScore() {
        double base = calculateBaseScore();
        return Math.min(100.0, base * difficultyMultiplier);
    }

    private String formatTime(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        if (mins > 0 && secs > 0) {
            return mins + "m " + secs + "s";
        } else if (mins > 0) {
            return mins + "m";
        } else {
            return secs + "s";
        }
    }
}
