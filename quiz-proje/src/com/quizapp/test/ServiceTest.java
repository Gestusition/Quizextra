package com.quizapp.test;

import com.quizapp.model.Difficulty;
import com.quizapp.model.MultipleChoiceQuestion;
import com.quizapp.model.Student;
import com.quizapp.service.QuestionService;
import com.quizapp.service.QuizService;
import com.quizapp.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Service classes.
 */
@DisplayName("Service Layer Tests")
class ServiceTest {

    @Test
    @DisplayName("StudentService validation should work correctly")
    void testStudentServiceValidation() {
        StudentService service = new StudentService();

        Student valid = new Student("Valid Name", "123", "pass1");
        assertTrue(service.validateStudent(valid), "Valid student should pass validation");

        Student invalid = new Student("", "123", "pass");
        assertFalse(service.validateStudent(invalid), "Invalid student should fail validation");

        assertNotNull(service.getValidationError(invalid), "Should return error message");
    }

    @Test
    @DisplayName("StudentService statistics should return data")
    void testStudentServiceStatistics() {
        StudentService service = new StudentService();

        StudentService.StudentStatistics stats = service.getStatistics();
        assertNotNull(stats, "Statistics should not be null");
        assertTrue(stats.totalStudents() >= 0, "Total students should be >= 0");
    }

    @Test
    @DisplayName("QuestionService validation should work correctly")
    void testQuestionServiceValidation() {
        QuestionService service = new QuestionService();

        var valid = new MultipleChoiceQuestion("Question?", Arrays.asList("A", "B"), 0);
        assertTrue(service.validateQuestion(valid), "Valid question should pass");

        var invalid = new MultipleChoiceQuestion("", Arrays.asList("A", "B"), 0);
        assertFalse(service.validateQuestion(invalid), "Invalid question should fail");
    }

    @Test
    @DisplayName("QuestionService filtering should work correctly")
    void testQuestionServiceFiltering() {
        QuestionService service = new QuestionService();

        var allQuestions = service.getAllQuestions();
        var easyQuestions = service.filterByDifficulty(Difficulty.EASY);

        assertTrue(easyQuestions.size() <= allQuestions.size(),
                "Filtered should be <= total");
    }

    @Test
    @DisplayName("QuizService should calculate correct letter grades")
    void testQuizServiceGrading() {
        QuizService service = new QuizService();

        assertEquals("AA", service.calculateLetterGrade(95), "95 should be AA");
        assertEquals("BA", service.calculateLetterGrade(85), "85 should be BA");
        assertEquals("CB", service.calculateLetterGrade(75), "75 should be CB");
        assertEquals("DC", service.calculateLetterGrade(65), "65 should be DC");
        assertEquals("FD", service.calculateLetterGrade(55), "55 should be FD");

        assertTrue(service.isPassing(70), "70 (CC) should be passing by default");
        assertFalse(service.isPassing(60), "60 (DD) should NOT be passing by default (CC required)");
    }

    @Test
    @DisplayName("QuizService settings should be accessible")
    void testQuizServiceSettings() {
        QuizService service = new QuizService();

        QuizService.QuizSettings settings = service.getSettings();
        assertNotNull(settings, "Settings should not be null");
    }
}
