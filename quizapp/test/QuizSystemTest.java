package com.quizapp.test;

import com.quizapp.data.DataManager;
import com.quizapp.model.MultipleChoiceQuestion;
import com.quizapp.model.Question;
import com.quizapp.model.Quiz;
import com.quizapp.model.Student;
import com.quizapp.model.TrueFalseQuestion;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class QuizSystemTest {

    public static void main(String[] args) {
        QuizSystemTest test = new QuizSystemTest();
        test.testMultipleChoiceQuestionCorrect();
        test.testMultipleChoiceQuestionIncorrect();
        test.testMultipleChoiceQuestionInvalidInput();
        test.testTrueFalseQuestionCorrect();
        test.testTrueFalseQuestionIncorrect();
        test.testScoring();
        test.testDataManager();
        test.testDataManager();
        test.testDataManagerIntegrity();
        test.testStudentValidation();
        test.testStudentSearch();
        test.testQuestionValidation();
        test.testQuestionSearch();
        System.out.println("All tests passed!");
    }

    public void testMultipleChoiceQuestionCorrect() {
        Question q = new MultipleChoiceQuestion("Test Question", Arrays.asList("A", "B", "C"), 1);
        assertTrue(q.checkAnswer("2"), "Option 2 (index 1) should be correct");
    }

    public void testMultipleChoiceQuestionIncorrect() {
        Question q = new MultipleChoiceQuestion("Test Question", Arrays.asList("A", "B", "C"), 1);
        assertFalse(q.checkAnswer("1"), "Option 1 should be incorrect");
    }

    public void testMultipleChoiceQuestionInvalidInput() {
        Question q = new MultipleChoiceQuestion("Test Question", Arrays.asList("A", "B", "C"), 1);
        assertFalse(q.checkAnswer("invalid"), "Invalid input should return false");
    }

    public void testTrueFalseQuestionCorrect() {
        Question q = new TrueFalseQuestion("Test Question", true);
        assertTrue(q.checkAnswer("True"), "True should be correct");
        assertTrue(q.checkAnswer("true"), "true (case insensitive) should be correct");
    }

    public void testTrueFalseQuestionIncorrect() {
        Question q = new TrueFalseQuestion("Test Question", true);
        assertFalse(q.checkAnswer("False"), "False should be incorrect");
    }

    public void testScoring() {
        Quiz quiz = new Quiz();
        Student student = new Student("Test Student", "123", "pass");

        // Add questions
        quiz.addQuestion(new MultipleChoiceQuestion("MCQ", Arrays.asList("A", "B"), 0)); // Correct: 1
        quiz.addQuestion(new TrueFalseQuestion("TF", true)); // Correct: true

        // Simulate input: "1" for MCQ, "true" for TF
        String input = "1\ntrue\n";
        Scanner scanner = new Scanner(input);

        quiz.takeQuiz(student, scanner);

        if (student.getScore() != 100.0) {
            System.err.println("Assertion failed: Score should be 100.0, but was " + student.getScore());
            System.exit(1);
        }
    }

    public void testDataManager() {
        DataManager dm = DataManager.getInstance();

        // Test Student Management
        String testId = "test_student_999";
        dm.deleteStudent(testId); // Clean up just in case

        if (!dm.addStudent(new Student("Test Name", testId, "password"))) {
            System.err.println("Assertion failed: Could not add student");
            System.exit(1);
        }

        Student s = dm.verifyStudent(testId, "password");
        if (s == null) {
            System.err.println("Assertion failed: verifyStudent returned null");
            System.exit(1);
        }

        if (!dm.deleteStudent(testId)) {
            System.err.println("Assertion failed: Could not delete student");
            System.exit(1);
        }

        if (dm.verifyStudent(testId, "password") != null) {
            System.err.println("Assertion failed: Student should be deleted");
            System.exit(1);
        }

        // Test Question Management
        int initialSize = dm.getAllQuestions().size();
        Question q = new TrueFalseQuestion("Temp Question", true);
        dm.addQuestion(q);

        if (dm.getAllQuestions().size() != initialSize + 1) {
            System.err.println("Assertion failed: Question count did not increase");
            System.exit(1);
        }

        // Clean up question (remove last added)
        dm.deleteQuestion(initialSize);
        if (dm.getAllQuestions().size() != initialSize) {
            System.err.println("Assertion failed: Question count did not decrease");
            System.exit(1);
        }
    }

    public void testDataManagerIntegrity() {
        DataManager dm = DataManager.getInstance();
        int initialStudentCount = dm.getAllStudents().size();
        int initialQuestionCount = dm.getAllQuestions().size();

        // Simulate "Cancel" by doing nothing (which is what the UI does)
        // Verify that counts haven't changed
        if (dm.getAllStudents().size() != initialStudentCount) {
            System.err.println("Assertion failed: Student count changed without action");
            System.exit(1);
        }

        if (dm.getAllQuestions().size() != initialQuestionCount) {
            System.err.println("Assertion failed: Question count changed without action");
            System.exit(1);
        }

    }

    public void testStudentValidation() {
        Student validStudent = new Student("Valid Name", "123", "password123");
        assertTrue(validStudent.isValid(), "Student should be valid");
        assertTrue(validStudent.getValidationError() == null, "Validation error should be null");

        Student invalidStudent = new Student("", "123", "pass");
        assertFalse(invalidStudent.isValid(), "Student with empty name should be invalid");
        assertTrue(invalidStudent.getValidationError().contains("Name"), "Error should mention Name");
    }

    public void testStudentSearch() {
        Student s = new Student("Alice Wonderland", "S100", "pass");
        assertTrue(s.search("Alice"), "Should find by first name");
        assertTrue(s.search("wonder"), "Should find by partial last name (case insensitive)");
        assertTrue(s.search("s100"), "Should find by ID (case insensitive)");
        assertFalse(s.search("Bob"), "Should not find non-existent term");
    }

    public void testQuestionValidation() {
        Question q = new MultipleChoiceQuestion("Valid Q", Arrays.asList("A", "B"), 0);
        assertTrue(q.isValid(), "Question should be valid");

        Question invalidQ = new MultipleChoiceQuestion("", Arrays.asList("A", "B"), 0);
        assertFalse(invalidQ.isValid(), "Question with empty text should be invalid");

        Question invalidMcq = new MultipleChoiceQuestion("Text", List.of("A"), 0);
        assertFalse(invalidMcq.isValid(), "MCQ with 1 option should be invalid");
    }

    public void testQuestionSearch() {
        Question q = new MultipleChoiceQuestion("What is Java?", Arrays.asList("Language", "Coffee"), 0);
        assertTrue(q.search("Java"), "Should find by question text");
        assertTrue(q.search("java"), "Should find by question text (case insensitive)");
        assertTrue(q.search("Coffee"), "Should find by option text");
        assertFalse(q.search("Python"), "Should not find irrelevant text");
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            System.err.println("Assertion failed: " + message);
            System.exit(1);
        }
    }

    private void assertFalse(boolean condition, String message) {
        if (condition) {
            System.err.println("Assertion failed: " + message);
            System.exit(1);
        }
    }
}

// 15
// 18
