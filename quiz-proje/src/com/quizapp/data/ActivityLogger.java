package com.quizapp.data;

import com.quizapp.util.CsvUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages activity logging and quiz history persistence.
 */
public class ActivityLogger {
    private static final String ACTIVITY_LOG_FILE = "activity_log.csv";
    private static final String QUIZ_HISTORY_FILE = "quiz_history.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String activityLogPath;
    private final String quizHistoryPath;

    public ActivityLogger() {
        String dataFolder = DataManager.getDataFolder();
        this.activityLogPath = dataFolder + ACTIVITY_LOG_FILE;
        this.quizHistoryPath = dataFolder + QUIZ_HISTORY_FILE;
        ensureFileExists(activityLogPath);
        ensureFileExists(quizHistoryPath);
    }

    private void ensureFileExists(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Could not create " + filePath);
            }
        }
    }

    // ========== Activity Logging ==========

    /**
     * Logs an activity.
     *
     * @param actor   who performed the action (admin username)
     * @param action  what action was performed
     * @param target  what was affected
     * @param details additional details
     */
    public void logActivity(String actor, String action, String target, String details) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String line = String.join(",",
                CsvUtils.escapeCsv(timestamp),
                CsvUtils.escapeCsv(actor),
                CsvUtils.escapeCsv(action),
                CsvUtils.escapeCsv(target),
                CsvUtils.escapeCsv(details));

        try (PrintWriter pw = new PrintWriter(new FileWriter(activityLogPath, true))) {
            pw.println(line);
        } catch (IOException e) {
            System.err.println("Error writing activity log: " + e.getMessage());
        }
    }

    /**
     * Gets recent activities.
     *
     * @param maxCount maximum number of activities to return
     * @return list of activity records
     */
    public List<ActivityRecord> getRecentActivities(int maxCount) {
        List<ActivityRecord> activities = new ArrayList<>();
        File file = new File(activityLogPath);
        if (!file.exists())
            return activities;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            List<String> allLines = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    allLines.add(line);
                }
            }

            // Get last maxCount lines
            int start = Math.max(0, allLines.size() - maxCount);
            for (int i = allLines.size() - 1; i >= start; i--) {
                String[] parts = parseCsvLine(allLines.get(i));
                if (parts.length >= 4) {
                    activities.add(new ActivityRecord(
                            parts[0], // timestamp
                            parts.length > 1 ? parts[1] : "",
                            parts.length > 2 ? parts[2] : "",
                            parts.length > 3 ? parts[3] : "",
                            parts.length > 4 ? parts[4] : ""));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading activity log: " + e.getMessage());
        }
        return activities;
    }

    /**
     * Gets total activity count.
     */
    public int getTotalActivityCount() {
        int count = 0;
        File file = new File(activityLogPath);
        if (!file.exists())
            return 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            // ignore
        }
        return count;
    }

    // ========== Quiz History ==========

    /**
     * Saves a quiz result.
     *
     * @param studentId   student ID
     * @param studentName student name
     * @param score       quiz score
     * @param correct     number of correct answers
     * @param total       total questions
     * @param difficulty  difficulty filter used (or "ALL")
     */
    public void saveQuizResult(String studentId, String studentName, double score,
            int correct, int total, String difficulty) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String line = String.join(",",
                CsvUtils.escapeCsv(timestamp),
                CsvUtils.escapeCsv(studentId),
                CsvUtils.escapeCsv(studentName),
                String.format("%.1f", score),
                String.valueOf(correct),
                String.valueOf(total),
                CsvUtils.escapeCsv(difficulty));

        try (PrintWriter pw = new PrintWriter(new FileWriter(quizHistoryPath, true))) {
            pw.println(line);
        } catch (IOException e) {
            System.err.println("Error writing quiz history: " + e.getMessage());
        }
    }

    /**
     * Gets recent quiz results.
     *
     * @param maxCount maximum number to return
     * @return list of quiz records
     */
    public List<QuizRecord> getRecentQuizResults(int maxCount) {
        List<QuizRecord> results = new ArrayList<>();
        File file = new File(quizHistoryPath);
        if (!file.exists())
            return results;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            List<String> allLines = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    allLines.add(line);
                }
            }

            int start = Math.max(0, allLines.size() - maxCount);
            for (int i = allLines.size() - 1; i >= start; i--) {
                String[] parts = parseCsvLine(allLines.get(i));
                if (parts.length >= 6) {
                    results.add(new QuizRecord(
                            parts[0],
                            parts[1],
                            parts[2],
                            Double.parseDouble(parts[3]),
                            Integer.parseInt(parts[4]),
                            Integer.parseInt(parts[5]),
                            parts.length > 6 ? parts[6] : "ALL"));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading quiz history: " + e.getMessage());
        }
        return results;
    }

    /**
     * Gets total quiz count.
     */
    public int getTotalQuizCount() {
        int count = 0;
        File file = new File(quizHistoryPath);
        if (!file.exists())
            return 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            // ignore
        }
        return count;
    }

    // ========== CSV Parsing ==========

    private String[] parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }

    // ========== Record Classes ==========

    public record ActivityRecord(String timestamp, String actor, String action,
            String target, String details) {
    }

    public record QuizRecord(String timestamp, String studentId, String studentName,
            double score, int correct, int total, String difficulty) {
    }
}
