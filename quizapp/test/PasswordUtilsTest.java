package com.quizapp.test;

import com.quizapp.util.PasswordUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordUtils class.
 */
@DisplayName("PasswordUtils Tests")
class PasswordUtilsTest {

    @Test
    @DisplayName("Valid passwords should pass validation")
    void testIsValidPassword() {
        assertTrue(PasswordUtils.isValidPassword("12345"),
                "5-char password should be valid");
        assertTrue(PasswordUtils.isValidPassword("longerpassword"),
                "Long password should be valid");
    }

    @Test
    @DisplayName("Null password should be invalid")
    void testIsValidPasswordNull() {
        assertFalse(PasswordUtils.isValidPassword(null),
                "Null password should be invalid");
    }

    @Test
    @DisplayName("Empty password should be invalid")
    void testIsValidPasswordEmpty() {
        assertFalse(PasswordUtils.isValidPassword(""),
                "Empty password should be invalid");
    }

    @Test
    @DisplayName("Password shorter than 5 chars should be invalid")
    void testIsValidPasswordTooShort() {
        assertFalse(PasswordUtils.isValidPassword("1234"),
                "4-char password should be invalid");
        assertFalse(PasswordUtils.isValidPassword("abc"),
                "3-char password should be invalid");
    }

    @Test
    @DisplayName("Valid password should return null error")
    void testGetPasswordValidationErrorValid() {
        assertNull(PasswordUtils.getPasswordValidationError("12345"),
                "Valid password should return null error");
    }

    @Test
    @DisplayName("Empty or null password should return error message")
    void testGetPasswordValidationErrorEmpty() {
        assertNotNull(PasswordUtils.getPasswordValidationError(""),
                "Empty password should return error");
        assertNotNull(PasswordUtils.getPasswordValidationError(null),
                "Null password should return error");
    }

    @Test
    @DisplayName("Short password error should mention minimum length")
    void testGetPasswordValidationErrorTooShort() {
        String error = PasswordUtils.getPasswordValidationError("1234");
        assertNotNull(error, "Short password should return error");
        assertTrue(error.contains("5"), "Error should mention 5 characters");
    }
}
