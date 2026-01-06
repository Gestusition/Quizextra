package com.quizapp.util;

import java.util.Scanner;
// import java.nio.charset.StandardCharsets;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;

/**
 * Utility class for password operations.
 * 
 * PASSWORD HASHING DEVRE DIŞI:
 * Aşağıdaki hashPassword ve verifyPassword metodlarının yorumlarını kaldırarak
 * SHA-256 hash'leme aktif edilebilir.
 */
public final class PasswordUtils {

    // Hash'leme aktif mi? (true yapılırsa hash kullanılır)
    // private static final boolean HASHING_ENABLED = false;

    private PasswordUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Hashes a password using SHA-256.
     * Yorumu kaldırarak aktif edin.
     *
     * @param password the plain text password
     * @return the hashed password
     */
    // public static String hashPassword(String password) {
    // if (!HASHING_ENABLED || password == null) {
    // return password;
    // }
    // try {
    // MessageDigest md = MessageDigest.getInstance("SHA-256");
    // byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
    // StringBuilder hexString = new StringBuilder();
    // for (byte b : hash) {
    // String hex = Integer.toHexString(0xff & b);
    // if (hex.length() == 1) hexString.append('0');
    // hexString.append(hex);
    // }
    // return hexString.toString();
    // } catch (NoSuchAlgorithmException e) {
    // throw new RuntimeException("SHA-256 not available", e);
    // }
    // }

    /**
     * Verifies a password against a stored hash.
     * Yorumu kaldırarak aktif edin.
     *
     * @param plainPassword the plain text password to verify
     * @param storedHash    the stored hash to compare against
     * @return true if passwords match
     */
    // public static boolean verifyPassword(String plainPassword, String storedHash)
    // {
    // if (plainPassword == null || storedHash == null) {
    // return false;
    // }
    // if (!HASHING_ENABLED) {
    // return plainPassword.equals(storedHash);
    // }
    // return hashPassword(plainPassword).equals(storedHash);
    // }

    /**
     * Prompts user to enter password twice for confirmation.
     * Keeps asking until both entries match.
     *
     * @param scanner the Scanner to read input from
     * @param prompt  the prompt message to display
     * @return the confirmed password
     */
    public static String confirmPassword(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String password1 = scanner.nextLine();

            System.out.print("Confirm password: ");
            String password2 = scanner.nextLine();

            if (password1.equals(password2)) {
                return password1;
            }
            System.out.println("Passwords do not match. Please try again.");
        }
    }

    /**
     * Validates password meets minimum requirements.
     *
     * @param password the password to validate
     * @return true if password is valid
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 5;
    }

    /**
     * Gets validation error message for invalid password.
     *
     * @param password the password to check
     * @return error message or null if valid
     */
    public static String getPasswordValidationError(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty.";
        }
        if (password.length() < 5) {
            return "Password must be at least 5 characters.";
        }
        return null;
    }
}
