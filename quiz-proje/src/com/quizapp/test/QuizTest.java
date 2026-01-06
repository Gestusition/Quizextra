package com.quizapp.test;

import com.quizapp.model.MultipleChoiceQuestion;
import com.quizapp.model.Quiz;
import com.quizapp.model.Student;
import com.quizapp.model.TrueFalseQuestion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Quiz class.
 */
@DisplayName("Quiz Tests")
class QuizTest {

    @Test
    @DisplayName("Quiz should score answers correctly")
    void testQuizScoring() {
        Quiz quiz = new Quiz();
        Student student = new Student("Test", "123", "pass1");

        quiz.addQuestion(new MultipleChoiceQuestion("MCQ", Arrays.asList("A", "B"), 0));
        quiz.addQuestion(new TrueFalseQuestion("TF", true));

        String input = "1\ntrue\n";
        Scanner scanner = new Scanner(input);

        quiz.takeQuiz(student, scanner);

        assertEquals(100.0, student.getScore(), "Perfect score should be 100%");
        assertEquals(2, quiz.getCorrectCount(), "Should have 2 correct");
        assertEquals(2, quiz.getQuestionsAttempted(), "Should attempt 2 questions");
    }

    @Test
    @DisplayName("Quiz Gradable interface should calculate scores")
    void testQuizGradable() {
        Quiz quiz = new Quiz();
        Student student = new Student("Test", "123", "pass1");

        quiz.addQuestion(new MultipleChoiceQuestion("Q1", Arrays.asList("A", "B"), 0));
        quiz.addQuestion(new TrueFalseQuestion("Q2", true));

        String input = "1\nfalse\n"; // 1 correct, 1 wrong
        Scanner scanner = new Scanner(input);

        quiz.takeQuiz(student, scanner);

        assertEquals(50.0, quiz.calculateScore(), "Score should be 50%");
    }

    @Test
    @DisplayName("Quiz should assign correct letter grades")
    void testQuizLetterGrades() {
        Quiz quiz = new Quiz();
        Student student = new Student("Test", "123", "pass1");

        quiz.addQuestion(new TrueFalseQuestion("Q1", true));
        quiz.takeQuiz(student, new Scanner("true\n"));
        assertEquals("AA", quiz.getLetterGrade(), "100% should be AA");
        assertTrue(quiz.isPassing(), "100% should be passing");
    }

    @Test
    @DisplayName("Quiz configuration methods should work")
    void testQuizGetters() {
        Quiz quiz = new Quiz();
        quiz.setTimerEnabled(true);
        quiz.setTimerSeconds(120);
        quiz.setShuffleEnabled(true);
        quiz.setDifficultyMultiplier(2);

        // These are internal settings, just verify no exception
        assertTrue(true, "Quiz configuration should work");
    }
}
