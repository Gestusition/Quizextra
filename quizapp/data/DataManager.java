package com.quizapp.data;

import com.quizapp.model.Admin;
import com.quizapp.model.Difficulty;
import com.quizapp.model.FillBlankQuestion;
import com.quizapp.model.GradeScale;
import com.quizapp.model.MultipleChoiceQuestion;
import com.quizapp.model.Question;
import com.quizapp.model.Student;
import com.quizapp.model.TrueFalseQuestion;
import com.quizapp.util.CsvUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for managing application data.
 */
public class DataManager {
    private static volatile DataManager instance;
    private final List<Student> students;
    private final List<Admin> admins;
    private final List<Question> questions;
    private final List<String> validationErrors;
    private final ActivityLogger activityLogger;

    private String studentsFilePath = "students.csv";
    private String adminsFilePath = "admins.csv";
    private String questionsFilePath = "questions.csv";
    private String settingsFilePath = "settings.csv";

    // Quiz settings
    private int quizTimerSeconds = 120;
    private boolean quizTimerEnabled = false;
    private boolean quizShuffleEnabled = false;
    private final GradeScale gradeScale;

    // Current admin (for logging)
    private String currentAdmin = "system";

    private DataManager() {
        students = new ArrayList<>();
        admins = new ArrayList<>();
        questions = new ArrayList<>();
        validationErrors = new ArrayList<>();
        activityLogger = new ActivityLogger();
        gradeScale = new GradeScale();
        resolveFilePaths();
        loadSettings();
        loadData();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    public ActivityLogger getActivityLogger() {
        return activityLogger;
    }

    public void setCurrentAdmin(String admin) {
        this.currentAdmin = admin;
    }

    public String getCurrentAdmin() {
        return currentAdmin;
    }

    // ========== Quiz Settings ==========

    public int getQuizTimerSeconds() {
        return quizTimerSeconds;
    }

    public void setQuizTimerSeconds(int seconds) {
        this.quizTimerSeconds = Math.max(CsvUtils.TIMER_MIN_SECONDS, seconds);
        activityLogger.logActivity(currentAdmin, "SETTINGS_CHANGED", "Timer", seconds + " seconds");
        saveSettings();
    }

    public boolean isQuizTimerEnabled() {
        return quizTimerEnabled;
    }

    public void setQuizTimerEnabled(boolean enabled) {
        this.quizTimerEnabled = enabled;
        activityLogger.logActivity(currentAdmin, "SETTINGS_CHANGED", "Timer", enabled ? "Enabled" : "Disabled");
        saveSettings();
    }

    public boolean isQuizShuffleEnabled() {
        return quizShuffleEnabled;
    }

    public void setQuizShuffleEnabled(boolean enabled) {
        this.quizShuffleEnabled = enabled;
        activityLogger.logActivity(currentAdmin, "SETTINGS_CHANGED", "Shuffle", enabled ? "Enabled" : "Disabled");
        saveSettings();
    }

    public GradeScale getGradeScale() {
        return gradeScale;
    }

    // ========== Settings Persistence ==========

    private void loadSettings() {
        File file = new File(settingsFilePath);
        if (!file.exists()) {
            // Settings file doesn't exist in expected location - it will be created on save
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                String[] parts = line.split(",", 2);
                if (parts.length < 2)
                    continue;
                String key = parts[0].trim();
                String value = parts[1].trim();

                try {
                    switch (key) {
                        case "TIMER_ENABLED" -> {
                            if (!isValidBoolean(value)) {
                                validationErrors.add("settings.csv line " + lineNum + ": TIMER_ENABLED invalid value '"
                                        + value + "', using false");
                            }
                            quizTimerEnabled = parseBoolean(value);
                        }
                        case "TIMER_SECONDS" -> {
                            int seconds = Integer.parseInt(value);
                            if (seconds < 0) {
                                validationErrors.add("settings.csv line " + lineNum
                                        + ": TIMER_SECONDS cannot be negative, using default");
                                seconds = CsvUtils.TIMER_MIN_SECONDS;
                            } else if (seconds > 0 && seconds < CsvUtils.TIMER_MIN_SECONDS) {
                                validationErrors.add("settings.csv line " + lineNum + ": TIMER_SECONDS below minimum ("
                                        + CsvUtils.TIMER_MIN_SECONDS + "), adjusted");
                                seconds = CsvUtils.TIMER_MIN_SECONDS;
                            } else if (seconds > CsvUtils.TIMER_MAX_SECONDS) {
                                validationErrors.add("settings.csv line " + lineNum + ": TIMER_SECONDS above maximum ("
                                        + CsvUtils.TIMER_MAX_SECONDS + "), adjusted");
                                seconds = CsvUtils.TIMER_MAX_SECONDS;
                            }
                            quizTimerSeconds = seconds;
                        }
                        case "SHUFFLE_ENABLED" -> {
                            if (!isValidBoolean(value)) {
                                validationErrors.add("settings.csv line " + lineNum
                                        + ": SHUFFLE_ENABLED invalid value '" + value + "', using false");
                            }
                            quizShuffleEnabled = parseBoolean(value);
                        }
                        case "PASSING_GRADE" -> {
                            String grade = value.toUpperCase();
                            if (!isValidPassingGrade(grade)) {
                                validationErrors.add("settings.csv line " + lineNum + ": Invalid PASSING_GRADE '"
                                        + value + "', using CC");
                                grade = "CC";
                            }
                            gradeScale.setPassingGrade(grade);
                        }
                        case "AA", "BA", "BB", "CB", "CC", "DC", "DD", "FD" -> {
                            int threshold = Integer.parseInt(value);
                            if (threshold < 0 || threshold > 100) {
                                validationErrors.add("settings.csv line " + lineNum + ": " + key
                                        + " threshold must be 0-100, got " + threshold);
                                threshold = Math.max(0, Math.min(100, threshold));
                            }
                            gradeScale.setThreshold(key, threshold);
                        }
                    }
                } catch (NumberFormatException e) {
                    validationErrors
                            .add("settings.csv line " + lineNum + ": Invalid number for " + key + " = '" + value + "'");
                }
            }

            validateGradeOrder();

        } catch (IOException e) {
            validationErrors.add("settings.csv: Could not read file - " + e.getMessage());
        }
    }

    private boolean isValidBoolean(String value) {
        String v = value.toLowerCase();
        return v.equals("true") || v.equals("false");
    }

    private boolean parseBoolean(String value) {
        return value.equalsIgnoreCase("true");
    }

    private boolean isValidPassingGrade(String grade) {
        return grade.equals("DD") || grade.equals("DC") || grade.equals("CC") || 
               grade.equals("CB") || grade.equals("BB") || grade.equals("BA") || grade.equals("AA");
    }

    private void validateGradeOrder() {
        int aa = gradeScale.getAaThreshold();
        int ba = gradeScale.getBaThreshold();
        int bb = gradeScale.getBbThreshold();
        int cb = gradeScale.getCbThreshold();
        int cc = gradeScale.getCcThreshold();
        int dc = gradeScale.getDcThreshold();
        int dd = gradeScale.getDdThreshold();
        int fd = gradeScale.getFdThreshold();

        if (!(aa > ba && ba > bb && bb > cb && cb > cc && cc > dc && dc > dd && dd > fd)) {
            validationErrors.add(
                    "settings.csv: Grade thresholds are not in descending order (AA > BA > BB > CB > CC > DC > DD > FD)");
        }
    }

    public void saveSettings() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(settingsFilePath))) {
            pw.println("TIMER_ENABLED," + quizTimerEnabled);
            pw.println("TIMER_SECONDS," + quizTimerSeconds);
            pw.println("SHUFFLE_ENABLED," + quizShuffleEnabled);
            pw.println("PASSING_GRADE," + gradeScale.getPassingGrade());
            pw.println("AA," + gradeScale.getAaThreshold());
            pw.println("BA," + gradeScale.getBaThreshold());
            pw.println("BB," + gradeScale.getBbThreshold());
            pw.println("CB," + gradeScale.getCbThreshold());
            pw.println("CC," + gradeScale.getCcThreshold());
            pw.println("DC," + gradeScale.getDcThreshold());
            pw.println("DD," + gradeScale.getDdThreshold());
            pw.println("FD," + gradeScale.getFdThreshold());
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }

    // ========== File Paths ==========

    private static String dataFolder = null;

    private void resolveFilePaths() {
        String folder = getDataFolder();
        studentsFilePath = folder + "students.csv";
        adminsFilePath = folder + "admins.csv";
        questionsFilePath = folder + "questions.csv";
        settingsFilePath = folder + "settings.csv";
    }

    /**
     * Gets the data folder path. Uses the 'quiz-proje' folder which contains both 'src' and CSV files.
     * Always returns an absolute path to prevent duplicate files.
     */
    public static String getDataFolder() {
        if (dataFolder != null) {
            return dataFolder;
        }

        // First: try to get path from class location (most reliable)
        try {
            String classPath = DataManager.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath();
            
            // On Windows, remove leading slash if path starts with /C:
            if (classPath.matches("^/[A-Za-z]:.*")) {
                classPath = classPath.substring(1);
            }
            
            File classDir = new File(classPath);
            
            // Navigate up to find 'quiz-proje' folder (contains both src and CSV files)
            File current = classDir;
            for (int i = 0; i < 10 && current != null; i++) {
                // Look for a folder named 'quiz-proje' that contains 'src'
                if (current.getName().equals("quiz-proje")) {
                    File srcInCurrent = new File(current, "src");
                    if (srcInCurrent.exists() && srcInCurrent.isDirectory()) {
                        dataFolder = current.getAbsolutePath() + File.separator;
                        System.out.println("[DataManager] Data folder: " + dataFolder);
                        return dataFolder;
                    }
                }
                // Also check for 'quiz-proje' as a child directory
                File quizProje = new File(current, "quiz-proje");
                if (quizProje.exists() && quizProje.isDirectory()) {
                    File srcInQuizProje = new File(quizProje, "src");
                    if (srcInQuizProje.exists() && srcInQuizProje.isDirectory()) {
                        dataFolder = quizProje.getAbsolutePath() + File.separator;
                        System.out.println("[DataManager] Data folder: " + dataFolder);
                        return dataFolder;
                    }
                }
                current = current.getParentFile();
            }
        } catch (Exception e) {
            System.err.println("[DataManager] Error finding class path: " + e.getMessage());
        }

        // Second: try from current working directory
        try {
            File currentDir = new File(".").getCanonicalFile();
            File current = currentDir;
            for (int i = 0; i < 5 && current != null; i++) {
                // Look for 'quiz-proje' folder
                if (current.getName().equals("quiz-proje")) {
                    File srcInCurrent = new File(current, "src");
                    if (srcInCurrent.exists() && srcInCurrent.isDirectory()) {
                        dataFolder = current.getAbsolutePath() + File.separator;
                        System.out.println("[DataManager] Data folder: " + dataFolder);
                        return dataFolder;
                    }
                }
                // Also check for 'quiz-proje' as a child directory
                File quizProje = new File(current, "quiz-proje");
                if (quizProje.exists() && quizProje.isDirectory()) {
                    File srcInQuizProje = new File(quizProje, "src");
                    if (srcInQuizProje.exists() && srcInQuizProje.isDirectory()) {
                        dataFolder = quizProje.getAbsolutePath() + File.separator;
                        System.out.println("[DataManager] Data folder: " + dataFolder);
                        return dataFolder;
                    }
                }
                current = current.getParentFile();
            }
        } catch (Exception e) {
            System.err.println("[DataManager] Error finding working directory: " + e.getMessage());
        }

        // Final fallback: use current directory with absolute path
        try {
            dataFolder = new File(".").getCanonicalPath() + File.separator;
        } catch (Exception e) {
            dataFolder = "";
        }
        System.out.println("[DataManager] Data folder (fallback): " + dataFolder);
        return dataFolder;
    }

    public void loadData() {
        validationErrors.clear();
        loadStudents();
        loadAdmins();
        loadQuestions();
        if (admins.isEmpty()) {
            admins.add(new Admin("root", "root"));
            saveAdmins();
        }
    }

    public boolean hasValidationErrors() {
        return !validationErrors.isEmpty();
    }

    public List<String> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }

    public void clearValidationErrors() {
        validationErrors.clear();
    }

    public void saveData() {
        saveStudents();
        saveAdmins();
        saveQuestions();
    }

    // ========== Student Operations ==========

    private void loadStudents() {
        students.clear();
        File file = new File(studentsFilePath);
        if (!file.exists())
            return;

        int lineNumber = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] parts = parseCsvLine(line);
                if (parts.length >= 4 && "STUDENT".equals(parts[0])) {
                    Student student = new Student(parts[2], parts[1], parts[3]);
                    if (!student.isValid()) {
                        validationErrors.add("students.csv line " + lineNumber + " (ID: " + parts[1] + "): "
                                + student.getValidationError());
                    } else if (findStudentById(parts[1]) != null) {
                        // Duplicate ID - skip and log error
                        validationErrors.add("students.csv line " + lineNumber + " (ID: " + parts[1] + "): "
                                + "Duplicate ID - skipped.");
                    } else {
                        students.add(student);
                    }
                }
            }
        } catch (IOException e) {
            validationErrors.add("students.csv: Error - " + e.getMessage());
        }
    }

    private void saveStudents() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(studentsFilePath))) {
            for (Student s : students) {
                // Use Exportable interface for CSV generation
                pw.println(s.toCsvLine());
            }
        } catch (IOException e) {
            System.err.println("Error saving students: " + e.getMessage());
        }
    }

    public Student verifyStudent(String id, String password) {
        for (Student s : students) {
            if (s.getId().equals(id) && s.getPassword().equals(password))
                return s;
        }
        return null;
    }

    public Student findStudentById(String id) {
        for (Student s : students) {
            if (s.getId().equals(id))
                return s;
        }
        return null;
    }

    public boolean addStudent(Student student) {
        for (Student s : students) {
            if (s.getId().equals(student.getId()))
                return false;
        }
        students.add(student);
        saveStudents();
        activityLogger.logActivity(currentAdmin, "STUDENT_ADDED", student.getId(), student.getName());
        return true;
    }

    public boolean deleteStudent(String id) {
        Student toDelete = findStudentById(id);
        boolean removed = students.removeIf(s -> s.getId().equals(id));
        if (removed) {
            saveStudents();
            activityLogger.logActivity(currentAdmin, "STUDENT_DELETED", id, toDelete != null ? toDelete.getName() : "");
        }
        return removed;
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    // ========== Admin Operations ==========

    private void loadAdmins() {
        admins.clear();
        File file = new File(adminsFilePath);
        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = parseCsvLine(line);
                if (parts.length >= 3 && "ADMIN".equals(parts[0])) {
                    admins.add(new Admin(parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading admins: " + e.getMessage());
        }
    }

    private void saveAdmins() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(adminsFilePath))) {
            for (Admin a : admins) {
                pw.println("ADMIN," + CsvUtils.escapeCsv(a.getUsername()) + "," + CsvUtils.escapeCsv(a.getPassword()));
            }
        } catch (IOException e) {
            System.err.println("Error saving admins: " + e.getMessage());
        }
    }

    public Admin verifyAdmin(String username, String password) {
        for (Admin a : admins) {
            if (a.getUsername().equals(username) && a.getPassword().equals(password))
                return a;
        }
        return null;
    }

    public void updateAdminPassword(Admin admin, String newPassword) {
        admin.setPassword(newPassword);
        saveAdmins();
        activityLogger.logActivity(admin.getUsername(), "PASSWORD_CHANGED", admin.getUsername(), "");
    }

    // ========== Question Operations ==========

    private void loadQuestions() {
        questions.clear();
        File file = new File(questionsFilePath);
        if (!file.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = parseCsvLine(line);
                if (parts.length < 3)
                    continue;

                if ("MC".equals(parts[0])) {
                    String text = parts[1];
                    int correctIndex = Integer.parseInt(parts[2]);
                    Difficulty difficulty = Difficulty.MEDIUM;
                    int optionsStart = 3;

                    if (parts.length > 3) {
                        try {
                            difficulty = Difficulty.fromString(parts[3]);
                            optionsStart = 4;
                        } catch (Exception e) {
                            optionsStart = 3;
                        }
                    }

                    List<String> options = new ArrayList<>();
                    for (int i = optionsStart; i < parts.length; i++) {
                        options.add(parts[i]);
                    }
                    if (options.isEmpty() && parts.length > 3) {
                        for (int i = 3; i < parts.length; i++)
                            options.add(parts[i]);
                        difficulty = Difficulty.MEDIUM;
                    }
                    questions.add(new MultipleChoiceQuestion(text, options, correctIndex, difficulty));
                } else if ("TF".equals(parts[0])) {
                    String text = parts[1];
                    boolean answer = Boolean.parseBoolean(parts[2]);
                    Difficulty difficulty = parts.length > 3 ? Difficulty.fromString(parts[3]) : Difficulty.MEDIUM;
                    questions.add(new TrueFalseQuestion(text, answer, difficulty));
                } else if ("FB".equals(parts[0])) {
                    String text = parts[1];
                    Difficulty difficulty = Difficulty.MEDIUM;
                    int answersStart = 2;
                    if (parts.length > 2) {
                        try {
                            difficulty = Difficulty.fromString(parts[2]);
                            answersStart = 3;
                        } catch (Exception e) {
                            answersStart = 2;
                        }
                    }
                    List<String> answers = new ArrayList<>();
                    for (int i = answersStart; i < parts.length; i++) {
                        answers.add(parts[i]);
                    }
                    questions.add(new FillBlankQuestion(text, answers, difficulty));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading questions: " + e.getMessage());
        }
    }

    private void saveQuestions() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(questionsFilePath))) {
            for (Question q : questions) {
                if (q instanceof MultipleChoiceQuestion mc) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("MC,").append(CsvUtils.escapeCsv(mc.getQuestionText())).append(",")
                            .append(mc.getCorrectOptionIndex()).append(",").append(mc.getDifficulty().name());
                    for (String opt : mc.getOptions())
                        sb.append(",").append(CsvUtils.escapeCsv(opt));
                    pw.println(sb);
                } else if (q instanceof TrueFalseQuestion tf) {
                    pw.println("TF," + CsvUtils.escapeCsv(tf.getQuestionText()) + "," + tf.getCorrectAnswer() + ","
                            + tf.getDifficulty().name());
                } else if (q instanceof FillBlankQuestion fb) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("FB,").append(CsvUtils.escapeCsv(fb.getQuestionText())).append(",")
                            .append(fb.getDifficulty().name());
                    for (String ans : fb.getAcceptableAnswers()) {
                        sb.append(",").append(CsvUtils.escapeCsv(ans));
                    }
                    pw.println(sb);
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving questions: " + e.getMessage());
        }
    }

    public void addQuestion(Question q) {
        questions.add(q);
        saveQuestions();
        String type = q.getTypeName();
        activityLogger.logActivity(currentAdmin, "QUESTION_ADDED", type,
                q.getQuestionText().substring(0, Math.min(30, q.getQuestionText().length())));
    }

    public List<Question> getAllQuestions() {
        return new ArrayList<>(questions);
    }

    public boolean deleteQuestion(int index) {
        if (index >= 0 && index < questions.size()) {
            Question q = questions.remove(index);
            saveQuestions();
            activityLogger.logActivity(currentAdmin, "QUESTION_DELETED", q.getTypeName(),
                    q.getQuestionText().substring(0, Math.min(30, q.getQuestionText().length())));
            return true;
        }
        return false;
    }

    public boolean moveQuestion(int from, int to) {
        if (from < 0 || from >= questions.size() || to < 0 || to >= questions.size())
            return false;
        Question q = questions.remove(from);
        questions.add(to, q);
        saveQuestions();
        activityLogger.logActivity(currentAdmin, "QUESTION_REORDERED", "Position", from + " -> " + to);
        return true;
    }

    // ========== Quiz Result Logging ==========

    public void saveQuizResult(Student student, double score, int correct, int total, String difficulty) {
        activityLogger.saveQuizResult(student.getId(), student.getName(), score, correct, total, difficulty);
    }

    // ========== CSV Utilities ==========

    private String[] parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Handle escaped quote "" -> "
                    sb.append('"');
                    i++; // Skip next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }

    /**
     * Checks for grade-related settings errors.
     * Only errors affecting grade calculation/display are critical.
     * Timer and Shuffle errors are not critical - they use defaults.
     */
    public boolean hasGradeSettingsErrors() {
        for (String error : validationErrors) {
            if (error.contains("settings.csv")) {
                // Only grade-related errors are critical
                if (error.contains("PASSING_GRADE") || 
                    error.contains("threshold") ||
                    error.contains("descending order") ||
                    error.contains("AA") || error.contains("BA") || error.contains("BB") ||
                    error.contains("CB") || error.contains("CC") || error.contains("DC") ||
                    error.contains("DD") || error.contains("FD")) {
                    return true;
                }
            }
        }
        return false;
    }

}
