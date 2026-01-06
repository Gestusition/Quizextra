package com.quizapp.model;

import com.quizapp.interfaces.Validatable;

/**
 * Represents an administrator user in the quiz system.
 */
public class Admin implements Validatable {
    private final String username;
    private String password;
    private boolean isDefaultPassword;

    /**
     * Creates a new Admin with the given credentials.
     *
     * @param username the admin username
     * @param password the admin password
     */
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
        this.isDefaultPassword = "root".equals(password);
    }

    /**
     * Gets the admin username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the admin password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets a new password for the admin.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
        this.isDefaultPassword = false;
    }

    /**
     * Checks if the admin is using the default password.
     *
     * @return true if using default password
     */
    public boolean isDefaultPassword() {
        return isDefaultPassword;
    }

    @Override
    public boolean isValid() {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return password.length() >= 4;
    }

    @Override
    public String getValidationError() {
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty.";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty.";
        }
        if (password.length() < 4) {
            return "Password must be at least 4 characters.";
        }
        return null;
    }
}
