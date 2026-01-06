package com.quizapp.handler;

import com.quizapp.data.DataManager;
import com.quizapp.model.Difficulty;
import com.quizapp.model.Question;
import com.quizapp.model.Quiz;
import com.quizapp.model.Student;
import com.quizapp.service.QuizService;
import com.quizapp.service.StudentService;
import java.util.List;
import java.util.Scanner;

/**
 * Handles student login, CRUD operations and search.
 */
public class StudentHandler {
    private final Scanner scanner;
    private final StudentService studentService;
    private final QuizService quizService;
    private final DataManager dataManager;
    private boolean shouldExitApp = false;
    private Quiz lastQuiz = null;

    public StudentHandler(Scanner scanner) {
        this.scanner = scanner;
        this.studentService = new StudentService();
        this.quizService = new QuizService();
        this.dataManager = DataManager.getInstance();
    }

    public boolean shouldExitApp() {
        return shouldExitApp;
    }

    /**
     * Handles student login process.
     */
    public void studentLogin() {
        int failedAttempts = 0;

        while (failedAttempts < 5) {
            System.out.print("Enter Student ID (0 to cancel): ");
            String id = scanner.nextLine();
            if ("0".equals(id)) {
                return;
            }

            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            Student student = studentService.authenticate(id, password);
            if (student != null) {
                System.out.println("Welcome, " + student.getName() + "!");

                if (password.length() < 5) {
                    System.out.println(
                            "\n*** Your password is too short. Please contact an administrator to update it. ***\n");
                }

                showQuizMenu(student);
                return;
            } else {
                failedAttempts++;
                Student existingStudent = studentService.findById(id);

                if (existingStudent != null) {
                    if (failedAttempts >= 2) {
                        System.out.println("Invalid credentials. Forgot password? Contact an administrator.");
                    } else {
                        System.out.println("Invalid credentials.");
                    }
                } else {
                    // Check if there's a validation error specifically for this student ID
                    boolean hasErrorForThisId = false;
                    for (String error : dataManager.getValidationErrors()) {
                        if (error.contains("(ID: " + id + ")")) {
                            hasErrorForThisId = true;
                            break;
                        }
                    }

                    if (hasErrorForThisId) {
                        System.out
                                .println("There is a problem with your account data. Please contact an administrator.");
                    } else {
                        if (failedAttempts >= 2) {
                            System.out.println("Invalid credentials. Forgot password? Contact an administrator.");
                        } else {
                            System.out.println("Invalid credentials.");
                        }
                    }
                }
            }
        }
        System.out.println("Too many failed attempts. Returning to main menu.");
    }

    private void showQuizMenu(Student student) {
        while (true) {
            var settings = quizService.getSettings();
            boolean timerEnabled = settings.timerEnabled();
            int timerSeconds = settings.timerSeconds();
            boolean shuffleEnabled = settings.shuffleEnabled();

            System.out.println("\n========== STUDENT MENU ==========");
            String timerDisplay = timerEnabled ? formatTime(timerSeconds) : "OFF";
            System.out.println("Quiz Settings: Timer=" + timerDisplay + ", Shuffle=" + (shuffleEnabled ? "ON" : "OFF"));
            System.out.println("----------------------------------");
            System.out.println("1. Start Quiz");
            System.out.println("2. Start Quiz by Difficulty");

            if (lastQuiz != null) {
                System.out.println("3. Review Last Quiz");
                System.out.println("4. View My Quiz History");
                System.out.println("5. Logout");
                System.out.println("6. Exit Application");
            } else {
                System.out.println("3. View My Quiz History");
                System.out.println("4. Logout");
                System.out.println("5. Exit Application");
            }

            System.out.println("==================================");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();

            if (lastQuiz != null) {
                switch (choice) {
                    case "1" -> takeQuiz(student, timerEnabled, timerSeconds, shuffleEnabled, null);
                    case "2" -> showDifficultyMenu(student, timerEnabled, timerSeconds, shuffleEnabled);
                    case "3" -> reviewLastQuiz();
                    case "4" -> viewMyQuizHistory(student);
                    case "5" -> {
                        return;
                    }
                    case "6" -> {
                        shouldExitApp = true;
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Enter 1-6.");
                }
            } else {
                switch (choice) {
                    case "1" -> takeQuiz(student, timerEnabled, timerSeconds, shuffleEnabled, null);
                    case "2" -> showDifficultyMenu(student, timerEnabled, timerSeconds, shuffleEnabled);
                    case "3" -> viewMyQuizHistory(student);
                    case "4" -> {
                        return;
                    }
                    case "5" -> {
                        shouldExitApp = true;
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Enter 1-5.");
                }
            }
        }
    }

    private void showDifficultyMenu(Student student, boolean timerEnabled,
            int timerSeconds, boolean shuffleEnabled) {
        System.out.println("\n--- Select Difficulty ---");
        System.out.println("1. Easy (Score x1)");
        System.out.println("2. Medium (Score x2)");
        System.out.println("3. Hard (Score x3)");
        System.out.println("0. Back");
        System.out.print("Choose: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                takeQuiz(student, timerEnabled, timerSeconds, shuffleEnabled, Difficulty.EASY);
                break;
            case "2":
                takeQuiz(student, timerEnabled, timerSeconds, shuffleEnabled, Difficulty.MEDIUM);
                break;
            case "3":
                takeQuiz(student, timerEnabled, timerSeconds, shuffleEnabled, Difficulty.HARD);
                break;
            case "0":
                break;
            default:
                System.out.println("Enter 0-3.");
        }
    }

    private void takeQuiz(Student student, boolean enableTimer, int timerSeconds,
            boolean shuffleQuestions, Difficulty filterDifficulty) {

        // Check for critical settings errors
        if (dataManager.hasCriticalSettingsErrors()) {
            System.out.println("\nQuiz is temporarily unavailable. Please contact an administrator.");
            MenuHandler.pauseAndContinue(scanner);
            return;
        }

        Quiz quiz = new Quiz();
        List<Question> questions = dataManager.getAllQuestions();

        if (filterDifficulty != null) {
            questions = questions.stream()
                    .filter(q -> q.getDifficulty() == filterDifficulty)
                    .toList();

            if (questions.isEmpty()) {
                System.out.println("No " + filterDifficulty.getDisplayName() + " questions available.");
                MenuHandler.pauseAndContinue(scanner);
                return;
            }
            System.out.println("\n*** " + filterDifficulty.getDisplayName().toUpperCase() + " MODE ***");
        }

        if (questions.isEmpty()) {
            System.out.println("No questions available. Please contact an administrator.");
            return;
        }

        for (Question q : questions) {
            quiz.addQuestion(q);
        }

        quiz.setShuffleEnabled(shuffleQuestions);
        quiz.setTimerEnabled(enableTimer);
        quiz.setTimerSeconds(timerSeconds);

        if (filterDifficulty != null) {
            quiz.setDifficultyMultiplier(filterDifficulty.getValue());
        }

        quiz.takeQuiz(student, scanner);
        quiz.showResults();

        // Save quiz result to history using service
        quizService.saveQuizResult(student, quiz, filterDifficulty);

        // Update session last quiz
        this.lastQuiz = quiz;

        postQuizMenu(quiz);
    }

    private void postQuizMenu(Quiz quiz) {
        while (true) {
            System.out.println("\n1. View Score Details");
            System.out.println("2. Return to Menu");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    quiz.showResults();
                    break;
                case "2":
                    return;
                default:
                    System.out.println("Enter 1-2.");
            }
        }
    }

    public void addStudent() {
        System.out.println("\n--- Add New Student ---");

        // Get and validate name
        System.out.print("Enter Student Name (0 to cancel): ");
        String name = scanner.nextLine();
        if ("0".equals(name)) {
            return;
        }
        if (name.trim().isEmpty()) {
            System.out.println("Error: Name cannot be empty.");
            MenuHandler.pauseAndContinue(scanner);
            return;
        }
        if (name.matches(".*\\d.*")) {
            System.out.println("Error: Name cannot contain numbers.");
            MenuHandler.pauseAndContinue(scanner);
            return;
        }

        // Get and validate ID
        System.out.print("Enter Student ID (0 to cancel): ");
        String id = scanner.nextLine();
        if ("0".equals(id)) {
            return;
        }
        if (id.trim().isEmpty()) {
            System.out.println("Error: ID cannot be empty.");
            MenuHandler.pauseAndContinue(scanner);
            return;
        }
        if (!id.matches("\\d+")) {
            System.out.println("Error: ID must contain only numbers.");
            MenuHandler.pauseAndContinue(scanner);
            return;
        }
        // Check if ID already exists
        if (studentService.findById(id) != null) {
            System.out.println("Error: Student ID already exists.");
            MenuHandler.pauseAndContinue(scanner);
            return;
        }

        // Get and validate password
        String password = confirmPasswordWithCancel("Enter Password (0 to cancel): ");
        if (password == null) {
            return;
        }

        Student newStudent = new Student(name, id, password);
        if (studentService.addStudent(newStudent)) {
            System.out.println("Student added successfully.");
        } else {
            System.out.println("Error: Could not add student.");
        }
        MenuHandler.pauseAndContinue(scanner);
    }

    private String confirmPasswordWithCancel(String prompt) {
        while (true) {
            System.out.print(prompt);
            String password1 = scanner.nextLine();
            if ("0".equals(password1)) {
                return null;
            }

            if (password1.length() < 5) {
                System.out.println("Password must be at least 5 characters.");
                continue;
            }

            System.out.print("Confirm password: ");
            String password2 = scanner.nextLine();

            if (password1.equals(password2)) {
                return password1;
            }
            System.out.println("Passwords do not match. Try again.");
        }
    }

    public void deleteStudent() {
        System.out.println("\n--- Delete Student ---");
        System.out.print("Enter Student ID (0 to cancel): ");
        String id = scanner.nextLine();
        if ("0".equals(id)) {
            return;
        }

        if (studentService.deleteStudent(id)) {
            System.out.println("Student deleted.");
        } else {
            System.out.println("Student not found.");
        }
        MenuHandler.pauseAndContinue(scanner);
    }

    public void listStudents() {
        List<Student> students = studentService.getAllStudents();
        System.out.println("\n--- Student List (" + students.size() + ") ---");
        if (students.isEmpty()) {
            System.out.println("No students.");
        } else {
            for (Student s : students) {
                String warning = s.getPassword().length() < 5 ? " [!PWD]" : "";
                System.out.println("  " + s.getId() + " - " + s.getName() + warning);
            }
        }
        MenuHandler.pauseAndContinue(scanner);
    }

    public void searchStudents() {
        System.out.println("\n--- Search Students ---");
        System.out.print("Query: ");
        String query = scanner.nextLine();
        List<Student> students = studentService.getAllStudents();
        boolean found = false;
        for (Student s : students) {
            if (s.search(query)) {
                System.out.println("  " + s.getId() + " - " + s.getName());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No results.");
        }
        MenuHandler.pauseAndContinue(scanner);
    }

    /**
     * Allows admin to edit student information (password reset).
     */
    public void editStudent() {
        System.out.println("\n--- Edit Student ---");
        System.out.print("Enter Student ID (0 to cancel): ");
        String id = scanner.nextLine();
        if ("0".equals(id)) {
            return;
        }

        Student student = studentService.findById(id);
        if (student == null) {
            System.out.println("Student not found.");
            MenuHandler.pauseAndContinue(scanner);
            return;
        }

        System.out.println("Student: " + student.getName() + " (ID: " + student.getId() + ")");
        System.out.println("\n1. Reset Password");
        System.out.println("0. Back");
        System.out.print("Choose: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                resetStudentPassword(student);
                break;
            case "0":
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private void resetStudentPassword(Student student) {
        String newPassword = confirmPasswordWithCancel("Enter new password (0 to cancel): ");
        if (newPassword == null) {
            return;
        }

        // Create updated student with new password
        Student updated = new Student(student.getName(), student.getId(), newPassword);
        dataManager.deleteStudent(student.getId());
        dataManager.addStudent(updated);
        System.out.println("Password updated for " + student.getName() + ".");
        MenuHandler.pauseAndContinue(scanner);
    }

    /**
     * Shows quiz history for the current student.
     */
    private void viewMyQuizHistory(Student student) {
        System.out.println("\n--- My Quiz History ---");
        var logger = dataManager.getActivityLogger();
        var allResults = logger.getRecentQuizResults(100);

        // Filter for this student
        var myResults = allResults.stream()
                .filter(r -> r.studentId().equals(student.getId()))
                .toList();

        if (myResults.isEmpty()) {
            System.out.println("No quiz history found.");
        } else {
            System.out.println("Your recent quizzes:\n");
            for (var r : myResults) {
                System.out.printf("  %s | Score: %.0f%% (%d/%d) | %s\n",
                        r.timestamp().substring(0, 16),
                        r.score(),
                        r.correct(),
                        r.total(),
                        r.difficulty());
            }
        }
        MenuHandler.pauseAndContinue(scanner);
    }

    private void reviewLastQuiz() {
        if (this.lastQuiz == null) {
            System.out.println("No quiz to review.");
        } else {
            this.lastQuiz.showDetailedResults();
        }
        MenuHandler.pauseAndContinue(scanner);
    }

    private String formatTime(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        if (mins > 0 && secs > 0) {
            return mins + "m " + secs + "s";
        } else if (mins > 0) {
            return mins + "m";
        } else {
            return secs + "s";
        }
    }
}
