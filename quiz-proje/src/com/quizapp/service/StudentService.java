package com.quizapp.service;

import com.quizapp.data.DataManager;
import com.quizapp.model.Student;
import java.util.List;

/**
 * Service for student operations and authentication.
 */
public class StudentService {
    private final DataManager dataManager;

    public StudentService() {
        this.dataManager = DataManager.getInstance();
    }

    /**
     * Validates a student object.
     *
     * @param student the student to validate
     * @return true if valid
     */
    public boolean validateStudent(Student student) {
        return student != null && student.isValid();
    }

    /**
     * Gets validation error for a student.
     *
     * @param student the student to check
     * @return error message or null if valid
     */
    public String getValidationError(Student student) {
        if (student == null) {
            return "Student cannot be null.";
        }
        return student.getValidationError();
    }

    /**
     * Authenticates a student by ID and password.
     *
     * @param id       the student ID
     * @param password the password
     * @return authenticated Student or null
     */
    public Student authenticate(String id, String password) {
        if (id == null || password == null) {
            return null;
        }
        return dataManager.verifyStudent(id, password);
    }

    /**
     * Finds a student by ID.
     *
     * @param id the student ID
     * @return Student or null
     */
    public Student findById(String id) {
        return dataManager.findStudentById(id);
    }

    /**
     * Checks if a student exists.
     *
     * @param id the student ID
     * @return true if exists
     */
    public boolean exists(String id) {
        return dataManager.findStudentById(id) != null;
    }

    /**
     * Adds a new student.
     *
     * @param student the student to add
     * @return true if added
     */
    public boolean addStudent(Student student) {
        if (!validateStudent(student)) {
            return false;
        }
        return dataManager.addStudent(student);
    }

    /**
     * Deletes a student by ID.
     *
     * @param id the student ID
     * @return true if deleted
     */
    public boolean deleteStudent(String id) {
        return dataManager.deleteStudent(id);
    }

    public List<Student> getAllStudents() {
        return dataManager.getAllStudents();
    }

    /**
     * Checks if a student has weak password (less than 5 chars).
     *
     * @param student the student
     * @return true if weak
     */
    public boolean hasWeakPassword(Student student) {
        return student != null && student.getPassword().length() < 5;
    }

    /**
     * Gets student statistics.
     *
     * @return statistics record
     */
    public StudentStatistics getStatistics() {
        List<Student> students = dataManager.getAllStudents();

        int total = students.size();
        long weakPasswords = students.stream().filter(this::hasWeakPassword).count();
        double avgScore = students.stream().mapToDouble(Student::getScore).average().orElse(0.0);
        double maxScore = students.stream().mapToDouble(Student::getScore).max().orElse(0.0);
        long passing = students.stream()
                .filter(s -> dataManager.getGradeScale().isPassing(s.getScore()))
                .count();

        return new StudentStatistics(total, (int) weakPasswords, avgScore, maxScore, (int) passing);
    }

    public record StudentStatistics(int totalStudents, int weakPasswordCount,
            double averageScore, double highestScore, int passingCount) {
    }
}
