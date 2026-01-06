package com.quizapp.handler;

import com.quizapp.model.Difficulty;
import com.quizapp.model.FillBlankQuestion;
import com.quizapp.model.MultipleChoiceQuestion;
import com.quizapp.model.Question;
import com.quizapp.model.TrueFalseQuestion;
import com.quizapp.service.QuestionService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles question CRUD operations and search.
 */
public class QuestionHandler {
    private final Scanner scanner;
    private final QuestionService questionService;

    public QuestionHandler(Scanner scanner) {
        this.scanner = scanner;
        this.questionService = new QuestionService();
    }

    public void addQuestion() {
        while (true) {
            System.out.println("\n--- Add New Question ---");
            System.out.println("1. Multiple Choice");
            System.out.println("2. True/False");
            System.out.println("3. Fill in the Blank");
            System.out.println("0. Back");
            System.out.print("Choose type: ");
            String type = scanner.nextLine().trim();

            switch (type) {
                case "0":
                    return;
                case "1":
                    if (addMultipleChoiceQuestion())
                        return;
                    break;
                case "2":
                    if (addTrueFalseQuestion())
                        return;
                    break;
                case "3":
                    if (addFillBlankQuestion())
                        return;
                    break;
                default:
                    System.out.println("Enter 0-3.");
            }
        }
    }

    private Difficulty selectDifficulty() {
        System.out.println("Difficulty: 1=Easy, 2=Medium, 3=Hard, 0=Cancel");
        System.out.print("Choose: ");
        String input = scanner.nextLine().trim();

        switch (input) {
            case "0":
                return null;
            case "1":
                return Difficulty.EASY;
            case "3":
                return Difficulty.HARD;
            default:
                return Difficulty.MEDIUM;
        }
    }

    private boolean addMultipleChoiceQuestion() {
        Difficulty difficulty = selectDifficulty();
        if (difficulty == null)
            return false;

        System.out.print("Question text (0=cancel): ");
        String text = scanner.nextLine();
        if ("0".equals(text))
            return false;

        List<String> options = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            System.out.print("Option " + (i + 1) + " (0=cancel): ");
            String opt = scanner.nextLine();
            if ("0".equals(opt))
                return false;
            options.add(opt);
        }

        int correct = -1;
        while (correct < 0 || correct > 3) {
            System.out.print("Correct answer (1-4, 0=cancel): ");
            String input = scanner.nextLine().trim();
            if ("0".equals(input))
                return false;
            try {
                correct = Integer.parseInt(input) - 1;
                if (correct < 0 || correct > 3) {
                    System.out.println("Enter 1-4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Enter a number.");
            }
        }

        MultipleChoiceQuestion mcq = new MultipleChoiceQuestion(text, options, correct, difficulty);
        if (!mcq.isValid()) {
            System.out.println("Error: " + mcq.getValidationError());
            MenuHandler.pauseAndContinue(scanner);
            return false;
        }
        questionService.addQuestion(mcq);
        System.out.println("Question added! ID: " + mcq.getId());
        MenuHandler.pauseAndContinue(scanner);
        return true;
    }

    private boolean addTrueFalseQuestion() {
        Difficulty difficulty = selectDifficulty();
        if (difficulty == null)
            return false;

        System.out.print("Question text (0=cancel): ");
        String text = scanner.nextLine();
        if ("0".equals(text))
            return false;

        Boolean correct = null;
        while (correct == null) {
            System.out.print("Correct answer (T/F, 0=cancel): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if ("0".equals(input))
                return false;
            if (input.equals("true") || input.equals("t"))
                correct = true;
            else if (input.equals("false") || input.equals("f"))
                correct = false;
            else
                System.out.println("Enter T/F.");
        }

        TrueFalseQuestion tfq = new TrueFalseQuestion(text, correct, difficulty);
        if (!tfq.isValid()) {
            System.out.println("Error: " + tfq.getValidationError());
            MenuHandler.pauseAndContinue(scanner);
            return false;
        }
        questionService.addQuestion(tfq);
        System.out.println("Question added! ID: " + tfq.getId());
        MenuHandler.pauseAndContinue(scanner);
        return true;
    }

    private boolean addFillBlankQuestion() {
        Difficulty difficulty = selectDifficulty();
        if (difficulty == null)
            return false;

        System.out.print("Question text with ___ for blank (0=cancel): ");
        String text = scanner.nextLine();
        if ("0".equals(text))
            return false;

        List<String> answers = new ArrayList<>();
        System.out.println("Enter acceptable answers (empty to finish, at least 1 required):");

        while (true) {
            System.out.print("Answer " + (answers.size() + 1) + " (0=cancel, Enter=done): ");
            String ans = scanner.nextLine().trim();

            if ("0".equals(ans))
                return false;

            if (ans.isEmpty()) {
                if (answers.isEmpty()) {
                    System.out.println("At least one answer is required.");
                    continue;
                }
                break;
            }

            answers.add(ans);
        }

        FillBlankQuestion fbq = new FillBlankQuestion(text, answers, difficulty);
        if (!fbq.isValid()) {
            System.out.println("Error: " + fbq.getValidationError());
            MenuHandler.pauseAndContinue(scanner);
            return false;
        }
        questionService.addQuestion(fbq);
        System.out.println("Question added! ID: " + fbq.getId());
        MenuHandler.pauseAndContinue(scanner);
        return true;
    }

    public void deleteQuestion() {
        System.out.println("\n--- Delete Question ---");
        List<Question> questions = questionService.getAllQuestions();
        if (questions.isEmpty()) {
            System.out.println("No questions.");
            MenuHandler.pauseAndContinue(scanner);
            return;
        }

        // Use Displayable interface
        for (int i = 0; i < questions.size(); i++) {
            System.out.println((i + 1) + ". " + questions.get(i).getDisplaySummary());
        }

        System.out.print("Delete # (0=cancel): ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index == -1)
                return;
            if (questionService.deleteQuestion(index)) {
                System.out.println("Deleted.");
            } else {
                System.out.println("Invalid #.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid.");
        }
        MenuHandler.pauseAndContinue(scanner);
    }

    public void listQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        if (questions.isEmpty()) {
            System.out.println("No questions.");
            MenuHandler.pauseAndContinue(scanner);
            return;
        }

        // Group by difficulty using Displayable
        List<Question> easy = new ArrayList<>();
        List<Question> medium = new ArrayList<>();
        List<Question> hard = new ArrayList<>();

        for (Question q : questions) {
            switch (q.getDifficulty()) {
                case EASY -> easy.add(q);
                case MEDIUM -> medium.add(q);
                case HARD -> hard.add(q);
            }
        }

        System.out.println("\n=== QUESTIONS (" + questions.size() + " total) ===\n");

        if (!easy.isEmpty()) {
            System.out.println("--- EASY (" + easy.size() + ") ---");
            for (Question q : easy) {
                System.out.println("  " + q.getDisplaySummary());
            }
        }

        if (!medium.isEmpty()) {
            System.out.println("\n--- MEDIUM (" + medium.size() + ") ---");
            for (Question q : medium) {
                System.out.println("  " + q.getDisplaySummary());
            }
        }

        if (!hard.isEmpty()) {
            System.out.println("\n--- HARD (" + hard.size() + ") ---");
            for (Question q : hard) {
                System.out.println("  " + q.getDisplaySummary());
            }
        }

        MenuHandler.pauseAndContinue(scanner);
    }

    public void searchQuestions() {
        System.out.println("\n--- Search Questions ---");
        System.out.print("Query: ");
        String query = scanner.nextLine();
        List<Question> questions = questionService.getAllQuestions();
        boolean found = false;

        // Use Searchable interface
        for (Question q : questions) {
            if (q.search(query)) {
                System.out.println("  " + q.getDisplaySummary());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No results.");
        }
        MenuHandler.pauseAndContinue(scanner);
    }

    public void modifyQuestionOrder() {
        System.out.println("\n--- Reorder Questions ---");
        List<Question> questions = questionService.getAllQuestions();
        if (questions.isEmpty()) {
            System.out.println("No questions.");
            MenuHandler.pauseAndContinue(scanner);
            return;
        }

        for (int i = 0; i < questions.size(); i++) {
            System.out.println((i + 1) + ". " + questions.get(i).getDisplaySummary());
        }

        try {
            System.out.print("Move # (0=cancel): ");
            int from = Integer.parseInt(scanner.nextLine()) - 1;
            if (from == -1)
                return;

            System.out.print("To position # (0=cancel): ");
            int to = Integer.parseInt(scanner.nextLine()) - 1;
            if (to == -1)
                return;

            if (questionService.moveQuestion(from, to)) {
                System.out.println("Moved.");
            } else {
                System.out.println("Invalid positions.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid.");
        }
        MenuHandler.pauseAndContinue(scanner);
    }

    /**
     * Shows detailed view of a question using Displayable interface.
     */
    public void viewQuestionDetails() {
        System.out.println("\n--- View Question Details ---");
        List<Question> questions = questionService.getAllQuestions();
        if (questions.isEmpty()) {
            System.out.println("No questions.");
            MenuHandler.pauseAndContinue(scanner);
            return;
        }

        for (int i = 0; i < questions.size(); i++) {
            System.out.println((i + 1) + ". " + questions.get(i).getDisplaySummary());
        }

        System.out.print("View # (0=cancel): ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index == -1)
                return;
            if (index >= 0 && index < questions.size()) {
                System.out.println("\n" + questions.get(index).getDisplayDetails());
            } else {
                System.out.println("Invalid #.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid.");
        }
        MenuHandler.pauseAndContinue(scanner);
    }
}
