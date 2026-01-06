package com.quizapp.test;

import com.quizapp.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Student class.
 */
@DisplayName("Student Tests")
class StudentTest {

    @Test
    @DisplayName("Student creation should set correct values")
    void testStudentCreation() {
        Student s = new Student("John Doe", "12345", "pass1");
        assertEquals("John Doe", s.getName(), "Name should match");
        assertEquals("12345", s.getId(), "ID should match");
    }

    @Test
    @DisplayName("Valid student should pass validation")
    void testStudentValidation() {
        Student valid = new Student("Valid Name", "123", "pass1");
        assertTrue(valid.isValid(), "Valid student should be valid");
        assertNull(valid.getValidationError(), "No validation error for valid student");
    }

    @Test
    @DisplayName("Invalid name should fail validation")
    void testStudentValidationInvalidName() {
        Student empty = new Student("", "123", "pass1");
        assertFalse(empty.isValid(), "Empty name should be invalid");

        Student withNumbers = new Student("John123", "123", "pass1");
        assertFalse(withNumbers.isValid(), "Name with numbers should be invalid");
    }

    @Test
    @DisplayName("Invalid ID should fail validation")
    void testStudentValidationInvalidId() {
        Student emptyId = new Student("John", "", "pass1");
        assertFalse(emptyId.isValid(), "Empty ID should be invalid");

        Student nonNumericId = new Student("John", "ABC", "pass1");
        assertFalse(nonNumericId.isValid(), "Non-numeric ID should be invalid");
    }

    @Test
    @DisplayName("Invalid password should fail validation")
    void testStudentValidationInvalidPassword() {
        Student shortPass = new Student("John", "123", "1234");
        assertFalse(shortPass.isValid(), "Short password should be invalid");

        Student emptyPass = new Student("John", "123", "");
        assertFalse(emptyPass.isValid(), "Empty password should be invalid");
    }

    @Test
    @DisplayName("Search should find by name and ID")
    void testStudentSearch() {
        Student s = new Student("Alice Wonderland", "1001", "pass1");
        assertTrue(s.search("Alice"), "Should find by first name");
        assertTrue(s.search("1001"), "Should find by ID");
        assertFalse(s.search("Bob"), "Should not find non-existent term");
    }

    @Test
    @DisplayName("Search should be case insensitive")
    void testStudentSearchCaseInsensitive() {
        Student s = new Student("Alice Wonderland", "1001", "pass1");
        assertTrue(s.search("alice"), "Search should be case insensitive");
        assertTrue(s.search("WONDERLAND"), "Search should be case insensitive");
    }

    @Test
    @DisplayName("Displayable methods should return values")
    void testStudentDisplayable() {
        Student s = new Student("John Doe", "123", "pass1");
        assertNotNull(s.getDisplaySummary(), "Display summary should not be null");
        assertNotNull(s.getDisplayDetails(), "Display details should not be null");
        assertTrue(s.getDisplaySummary().contains("123"), "Summary should contain ID");
    }

    @Test
    @DisplayName("Identifiable interface should work correctly")
    void testStudentIdentifiable() {
        Student s = new Student("John Doe", "123", "pass1");
        assertEquals("123", s.getId(), "getId should return ID");
        assertEquals("Student", s.getTypeName(), "Type name should be Student");
    }
}
