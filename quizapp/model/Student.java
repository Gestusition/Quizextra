package com.quizapp.model;

import com.quizapp.interfaces.Displayable;
import com.quizapp.interfaces.Exportable;
import com.quizapp.interfaces.Identifiable;
import com.quizapp.interfaces.Searchable;
import com.quizapp.interfaces.Validatable;
import com.quizapp.util.CsvUtils;

/**
 * Represents a student in the quiz system.
 */
public class Student implements Searchable, Validatable, Displayable, Exportable, Identifiable {

    private final String name;
    private final String id;
    private double score;
    private String password;

    /**
     * Creates a new student with specified credentials.
     *
     * @param name     the student's full name
     * @param id       the student's unique numeric ID
     * @param password the student's password
     */
    public Student(String name, String id, String password) {
        this.name = name;
        this.id = id;
        this.password = password;
        this.score = 0.0;
    }

    /**
     * Creates a new student with default password.
     *
     * @param name the student's full name
     * @param id   the student's unique ID
     */
    public Student(String name, String id) {
        this(name, id, CsvUtils.DEFAULT_PASSWORD);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String getTypeName() {
        return "Student";
    }

    /**
     * Searches by name or ID (case-insensitive).
     */
    @Override
    public boolean search(String query) {
        if (query == null) {
            return false;
        }
        String q = query.toLowerCase();
        return name.toLowerCase().contains(q) || id.toLowerCase().contains(q);
    }

    /**
     * Validates: name (non-empty, no digits), id (numeric, not "0"), password (min
     * 5 chars).
     */
    @Override
    public boolean isValid() {
        if (name == null || name.trim().isEmpty())
            return false;
        if (id == null || id.trim().isEmpty())
            return false;
        if (password == null || password.trim().isEmpty())
            return false;
        if (name.matches(".*\\d.*"))
            return false;
        if (!id.matches("\\d+"))
            return false;
        if (id.equals("0"))
            return false;
        if (password.length() < CsvUtils.PASSWORD_MIN_LENGTH)
            return false;
        return true;
    }

    @Override
    public String getValidationError() {
        if (name == null || name.trim().isEmpty())
            return "Name cannot be empty.";
        if (name.matches(".*\\d.*"))
            return "Name cannot contain numbers.";
        if (id == null || id.trim().isEmpty())
            return "ID cannot be empty.";
        if (!id.matches("\\d+"))
            return "ID must contain only numbers.";
        if (id.equals("0"))
            return "ID cannot be '0' (reserved).";
        if (password == null || password.trim().isEmpty())
            return "Password cannot be empty.";
        if (password.length() < CsvUtils.PASSWORD_MIN_LENGTH)
            return "Password must be at least " + CsvUtils.PASSWORD_MIN_LENGTH + " characters.";
        return null;
    }

    @Override
    public String getDisplaySummary() {
        return id + " - " + name;
    }

    @Override
    public String getDisplayDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Student Details:\n");
        sb.append("  ID: ").append(id).append("\n");
        sb.append("  Name: ").append(name).append("\n");
        sb.append("  Last Score: ").append(String.format("%.1f%%", score)).append("\n");
        return sb.toString();
    }

    @Override
    public String toCsvLine() {
        return "STUDENT," + CsvUtils.escapeCsv(id) + "," + CsvUtils.escapeCsv(name) + ","
                + CsvUtils.escapeCsv(password);
    }

    @Override
    public String getCsvHeader() {
        return "TYPE,ID,NAME,PASSWORD";
    }
}
