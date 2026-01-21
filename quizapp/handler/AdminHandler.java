package com.quizapp.handler;

import com.quizapp.data.DataManager;
import com.quizapp.model.Admin;
import com.quizapp.model.GradeScale;
import com.quizapp.service.QuizService;
import com.quizapp.util.CsvUtils;
import com.quizapp.util.PasswordUtils;
import java.util.Scanner;

/**
 * Handles admin login and admin menu operations.
 */
public class AdminHandler {
    private final Scanner scanner;
    private final DataManager dataManager;
    private final QuizService quizService;
    private final StudentHandler studentHandler;
    private final QuestionHandler questionHandler;
    private final QuizHandler quizHandler;
    private boolean shouldExitApp = false;

    public AdminHandler(Scanner scanner) {
        this.scanner = scanner;
        this.dataManager = DataManager.getInstance();
        this.quizService = new QuizService();
        this.studentHandler = new StudentHandler(scanner);
        this.questionHandler = new QuestionHandler(scanner);
        this.quizHandler = new QuizHandler(scanner);
    }

    public boolean shouldExitApp() {
        return shouldExitApp;
    }

    /**
     * Handles admin login process.
     */
    public void adminLogin() {
        System.out.print("Enter Admin Username (0 to cancel): ");
        String username = scanner.nextLine();
        if ("0".equals(username)) {
            return;
        }

        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine();

        Admin admin = dataManager.verifyAdmin(username, password);
        if (admin != null) {
            System.out.println("Login successful!");
            dataManager.setCurrentAdmin(admin.getUsername());

            // Show validation errors to admin
            showValidationErrorsToAdmin();

            if (admin.isDefaultPassword()) {
                System.out.println("\nWARNING: You are using the default password.");
                changeAdminPassword(admin);
            }
            adminMenu();
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    /**
     * Shows CSV validation errors to admin.
     */
    private void showValidationErrorsToAdmin() {
        if (dataManager.hasValidationErrors()) {
            System.out.println("\n*** DATA VALIDATION ERRORS ***");
            System.out.println("The following issues were found in CSV files:");
            for (String error : dataManager.getValidationErrors()) {
                System.out.println("  - " + error);
            }
            System.out.println("Please fix these issues in the CSV files.");
            System.out.println("Affected records have been skipped.\n");
        }
    }

    private void changeAdminPassword(Admin admin) {
        while (true) {
            String newPass = PasswordUtils.confirmPassword(scanner, "Enter new password: ");

            String validationError = PasswordUtils.getPasswordValidationError(newPass);
            if (validationError != null) {
                System.out.println("Error: " + validationError);
                continue;
            }

            dataManager.updateAdminPassword(admin, newPass);
            System.out.println("Password updated successfully.");
            break;
        }
    }

    public void adminMenu() {
        while (true) {
            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("[Student Operations]");
            System.out.println("  1. Add Student");
            System.out.println("  2. Delete Student");
            System.out.println("  3. Edit Student");
            System.out.println("  4. List Students");
            System.out.println("  5. Search Students");
            System.out.println("\n[Question Operations]");
            System.out.println("  6. Add Question");
            System.out.println("  7. Delete Question");
            System.out.println("  8. List Questions");
            System.out.println("  9. View Question Details");
            System.out.println(" 10. Search Questions");
            System.out.println(" 11. Reorder Questions");
            System.out.println("\n[Quiz Settings]");
            System.out.println(" 12. Configure Quiz Settings");
            System.out.println("\n[Other]");
            System.out.println(" 13. Show File Statistics");
            System.out.println(" 14. View Data Errors");
            System.out.println(" 15. Logout");
            System.out.println(" 16. Exit Application");
            System.out.println("================================");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    studentHandler.addStudent();
                    break;
                case "2":
                    studentHandler.deleteStudent();
                    break;
                case "3":
                    studentHandler.editStudent();
                    break;
                case "4":
                    studentHandler.listStudents();
                    break;
                case "5":
                    studentHandler.searchStudents();
                    break;
                case "6":
                    questionHandler.addQuestion();
                    break;
                case "7":
                    questionHandler.deleteQuestion();
                    break;
                case "8":
                    questionHandler.listQuestions();
                    break;
                case "9":
                    questionHandler.viewQuestionDetails();
                    break;
                case "10":
                    questionHandler.searchQuestions();
                    break;
                case "11":
                    questionHandler.modifyQuestionOrder();
                    break;
                case "12":
                    configureQuizSettings();
                    break;
                case "13":
                    quizHandler.showFileStats();
                    break;
                case "14":
                    showDataErrors();
                    break;
                case "15":
                    return;
                case "16":
                    shouldExitApp = true;
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Enter 1-16.");
            }

            if (studentHandler.shouldExitApp()) {
                shouldExitApp = true;
                return;
            }
        }
    }

    /**
     * Shows current data validation errors.
     */
    private void showDataErrors() {
        System.out.println("\n--- Data Validation Errors ---");
        if (dataManager.hasValidationErrors()) {
            for (String error : dataManager.getValidationErrors()) {
                System.out.println("  - " + error);
            }
        } else {
            System.out.println("No errors found.");
        }
        MenuHandler.pauseAndContinue(scanner);
    }

    private void configureQuizSettings() {
        GradeScale gradeScale = dataManager.getGradeScale();
        while (true) {
            var settings = quizService.getSettings();
            System.out.println("\n--- Quiz Settings ---");
            System.out.println("Current: Timer=" + (settings.timerEnabled() ? settings.timerSeconds() + "s" : "OFF")
                    + ", Shuffle=" + (settings.shuffleEnabled() ? "ON" : "OFF")
                    + ", Pass=" + gradeScale.getPassingGrade());
            System.out.println();
            System.out.println("1. Toggle Timer");
            System.out.println("2. Set Timer Duration");
            System.out.println("3. Toggle Shuffle");
            System.out.println("4. Set Passing Grade");
            System.out.println("5. Edit Grade Thresholds");
            System.out.println("6. View Grade Scale");
            System.out.println("0. Back");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "0":
                    return;
                case "1":
                    quizService.setTimerEnabled(!settings.timerEnabled());
                    System.out.println("Timer " + (!settings.timerEnabled() ? "enabled" : "disabled") + ".");
                    break;
                case "2":
                    setTimerDuration();
                    break;
                case "3":
                    quizService.setShuffleEnabled(!settings.shuffleEnabled());
                    System.out.println("Shuffle " + (!settings.shuffleEnabled() ? "enabled" : "disabled") + ".");
                    break;
                case "4":
                    setPassingGrade(gradeScale);
                    break;
                case "5":
                    editGradeThresholds(gradeScale);
                    break;
                case "6":
                    viewGradeScale(gradeScale);
                    break;
                default:
                    System.out.println("Enter 0-6.");
            }
        }
    }

    private void setTimerDuration() {
        System.out.println("\n--- Set Timer Duration ---");
        System.out.println("1. Enter in minutes");
        System.out.println("2. Enter in seconds");
        System.out.println("0. Cancel");
        System.out.print("Choose: ");

        String choice = scanner.nextLine().trim();
        int seconds = 0;

        try {
            switch (choice) {
                case "1" -> {
                    System.out.print("Enter duration in minutes (1-1440): ");
                    int minutes = Integer.parseInt(scanner.nextLine().trim());
                    if (minutes == 0)
                        return;
                    seconds = minutes * 60;
                }
                case "2" -> {
                    System.out.print("Enter duration in seconds (" + CsvUtils.TIMER_MIN_SECONDS + "-"
                            + CsvUtils.TIMER_MAX_SECONDS + "): ");
                    seconds = Integer.parseInt(scanner.nextLine().trim());
                    if (seconds == 0)
                        return;
                }
                case "0" -> {
                    return;
                }
                default -> {
                    System.out.println("Invalid option.");
                    return;
                }
            }

            if (seconds < CsvUtils.TIMER_MIN_SECONDS) {
                System.out.println("Minimum is " + CsvUtils.TIMER_MIN_SECONDS + " seconds.");
                seconds = CsvUtils.TIMER_MIN_SECONDS;
            } else if (seconds > CsvUtils.TIMER_MAX_SECONDS) {
                System.out.println("Maximum is " + CsvUtils.TIMER_MAX_SECONDS + " seconds (24 hours).");
                seconds = CsvUtils.TIMER_MAX_SECONDS;
            }

            quizService.setTimerSeconds(seconds);
            int mins = seconds / 60;
            int secs = seconds % 60;
            if (mins > 0) {
                System.out.println("Timer set to " + mins + " min " + secs + " sec.");
            } else {
                System.out.println("Timer set to " + seconds + " seconds.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private void setPassingGrade(GradeScale gradeScale) {
        System.out.println("\n--- Set Passing Grade ---");
        System.out.println("Current: " + gradeScale.getPassingGrade()
                + " (min " + gradeScale.getThreshold(gradeScale.getPassingGrade()) + ")");
        System.out.println();
        System.out.println("1. DD (" + gradeScale.getDdThreshold() + "+)");
        System.out.println("2. DC (" + gradeScale.getDcThreshold() + "+)");
        System.out.println("3. CC (" + gradeScale.getCcThreshold() + "+)");
        System.out.println("4. CB (" + gradeScale.getCbThreshold() + "+)");
        System.out.println("5. BB (" + gradeScale.getBbThreshold() + "+)");
        System.out.println("6. BA (" + gradeScale.getBaThreshold() + "+)");
        System.out.println("7. AA (" + gradeScale.getAaThreshold() + "+)");
        System.out.println("0. Cancel");
        System.out.print("Choose: ");

        String choice = scanner.nextLine().trim();
        String newGrade = switch (choice) {
            case "1" -> "DD";
            case "2" -> "DC";
            case "3" -> "CC";
            case "4" -> "CB";
            case "5" -> "BB";
            case "6" -> "BA";
            case "7" -> "AA";
            default -> null;
        };

        if (newGrade != null) {
            gradeScale.setPassingGrade(newGrade);
            dataManager.saveSettings();
            System.out.println("Passing grade set to " + newGrade + ".");
        }
    }

    private void editGradeThresholds(GradeScale gradeScale) {
        System.out.println("\n--- Edit Grade Thresholds ---");
        System.out.printf("  AA: %-3d |  BA: %-3d%n", gradeScale.getAaThreshold(), gradeScale.getBaThreshold());
        System.out.printf("  BB: %-3d |  CB: %-3d%n", gradeScale.getBbThreshold(), gradeScale.getCbThreshold());
        System.out.printf("  CC: %-3d |  DC: %-3d%n", gradeScale.getCcThreshold(), gradeScale.getDcThreshold());
        System.out.printf("  DD: %-3d |  FD: %-3d%n", gradeScale.getDdThreshold(), gradeScale.getFdThreshold());
        System.out.println();
        System.out.print("Enter grade to edit (AA/BA/BB/CB/CC/DC/DD/FD, 0=back): ");

        String grade = scanner.nextLine().trim().toUpperCase();
        if ("0".equals(grade))
            return;

        String[] validGrades = { "AA", "BA", "BB", "CB", "CC", "DC", "DD", "FD" };
        boolean valid = false;
        for (String g : validGrades) {
            if (g.equals(grade)) {
                valid = true;
                break;
            }
        }

        if (!valid) {
            System.out.println("Invalid grade.");
            return;
        }

        System.out.print("Enter new threshold for " + grade + " (1-100): ");
        try {
            int threshold = Integer.parseInt(scanner.nextLine().trim());
            
            // Validate against grade ordering constraints
            String validationError = gradeScale.validateThreshold(grade, threshold);
            if (validationError != null) {
                System.out.println("Error: " + validationError);
                return;
            }
            
            gradeScale.setThreshold(grade, threshold);
            dataManager.saveSettings();
            System.out.println(grade + " threshold set to " + threshold + ".");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private void viewGradeScale(GradeScale gradeScale) {
        System.out.println("\n--- Grade Scale ---");
        String[] grades = { "AA", "BA", "BB", "CB", "CC", "DC", "DD", "FD", "FF" };

        for (int i = 0; i < grades.length; i++) {
            String grade = grades[i];
            int min = gradeScale.getThreshold(grade);
            int max = (i == 0) ? 100 : gradeScale.getThreshold(grades[i - 1]) - 1;
            String status = gradeScale.getStatusForGrade(grade);

            String passMarker = grade.equals(gradeScale.getPassingGrade()) ? " <-- PASS" : "";
            System.out.printf("  %s : %3d-%-3d  %-11s%s%n", grade, min, max, status, passMarker);
        }

        System.out.println();
        System.out.println("Minimum Pass: " + gradeScale.getPassingGrade()
                + " (" + gradeScale.getThreshold(gradeScale.getPassingGrade()) + "+)");
        MenuHandler.pauseAndContinue(scanner);
    }
}
