package com.quizapp.test;

import com.quizapp.data.DataManager;
import com.quizapp.model.Question;
import com.quizapp.model.Student;
import com.quizapp.model.TrueFalseQuestion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DataManager class.
 */
@DisplayName("DataManager Tests")
class DataManagerTest {

    private DataManager dataManager;

    @BeforeEach
    void setUp() {
        dataManager = DataManager.getInstance();
    }

    @Test
    @DisplayName("DataManager should be singleton")
    void testSingleton() {
        DataManager dm1 = DataManager.getInstance();
        DataManager dm2 = DataManager.getInstance();
        assertSame(dm1, dm2, "DataManager should be singleton");
    }

    @Test
    @DisplayName("Student CRUD operations should work")
    void testStudentCRUD() {
        String testId = "test_999";

        // Cleanup
        dataManager.deleteStudent(testId);

        // Create
        assertTrue(dataManager.addStudent(new Student("Test User", testId, "pass1")),
                "Should add student");

        // Read
        Student s = dataManager.verifyStudent(testId, "pass1");
        assertNotNull(s, "Should find added student");
        assertEquals("Test User", s.getName(), "Name should match");

        // Delete
        assertTrue(dataManager.deleteStudent(testId), "Should delete student");
        assertNull(dataManager.verifyStudent(testId, "pass1"), "Should not find deleted student");
    }

    @Test
    @DisplayName("Question CRUD operations should work")
    void testQuestionCRUD() {
        int initialCount = dataManager.getAllQuestions().size();

        // Create
        Question q = new TrueFalseQuestion("Test Question", true);
        dataManager.addQuestion(q);
        assertEquals(initialCount + 1, dataManager.getAllQuestions().size(), "Question count should increase");

        // Delete
        dataManager.deleteQuestion(initialCount);
        assertEquals(initialCount, dataManager.getAllQuestions().size(), "Question count should decrease");
    }

    @Test
    @DisplayName("Find student by ID should work correctly")
    void testFindStudentById() {
        String testId = "find_test_123";

        dataManager.deleteStudent(testId);
        dataManager.addStudent(new Student("Find Test", testId, "pass1"));

        // Find existing
        Student found = dataManager.findStudentById(testId);
        assertNotNull(found, "Should find existing student by ID");

        // Find non-existing
        Student notFound = dataManager.findStudentById("nonexistent_id");
        assertNull(notFound, "Should not find non-existing student");

        dataManager.deleteStudent(testId);
    }

    @Test
    @DisplayName("Quiz settings should be configurable")
    void testQuizSettings() {
        dataManager.setQuizTimerSeconds(180);
        assertEquals(180, dataManager.getQuizTimerSeconds(), "Timer should be 180s");

        dataManager.setQuizTimerEnabled(true);
        assertTrue(dataManager.isQuizTimerEnabled(), "Timer should be enabled");

        dataManager.setQuizShuffleEnabled(true);
        assertTrue(dataManager.isQuizShuffleEnabled(), "Shuffle should be enabled");

        // Minimum timer
        dataManager.setQuizTimerSeconds(10);
        assertEquals(30, dataManager.getQuizTimerSeconds(), "Timer should be minimum 30s");
    }
}
