package com.quizapp.test;

import com.quizapp.model.Difficulty;
import com.quizapp.model.MultipleChoiceQuestion;
import com.quizapp.model.Question;
import com.quizapp.model.TrueFalseQuestion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Question classes.
 */
@DisplayName("Question Tests")
class QuestionTest {

    @Test
    @DisplayName("Multiple choice correct answer should return true")
    void testMultipleChoiceCorrectAnswer() {
        Question q = new MultipleChoiceQuestion("Test", Arrays.asList("A", "B", "C"), 1);
        assertTrue(q.checkAnswer("2"), "Option 2 (index 1) should be correct");
    }

    @Test
    @DisplayName("Multiple choice incorrect answers should return false")
    void testMultipleChoiceIncorrectAnswer() {
        Question q = new MultipleChoiceQuestion("Test", Arrays.asList("A", "B", "C"), 1);
        assertFalse(q.checkAnswer("1"), "Option 1 should be incorrect");
        assertFalse(q.checkAnswer("3"), "Option 3 should be incorrect");
    }

    @Test
    @DisplayName("Multiple choice invalid input should return false")
    void testMultipleChoiceInvalidInput() {
        Question q = new MultipleChoiceQuestion("Test", Arrays.asList("A", "B", "C"), 1);
        assertFalse(q.checkAnswer("invalid"), "Invalid input should return false");
        assertFalse(q.checkAnswer(""), "Empty input should return false");
    }

    @Test
    @DisplayName("True/False correct answers should return true")
    void testTrueFalseCorrectAnswer() {
        Question qTrue = new TrueFalseQuestion("Test", true);
        assertTrue(qTrue.checkAnswer("True"), "True should be correct");
        assertTrue(qTrue.checkAnswer("true"), "true should be correct");
        assertTrue(qTrue.checkAnswer("T"), "T should be correct");

        Question qFalse = new TrueFalseQuestion("Test", false);
        assertTrue(qFalse.checkAnswer("False"), "False should be correct");
        assertTrue(qFalse.checkAnswer("F"), "F should be correct");
    }

    @Test
    @DisplayName("True/False incorrect answers should return false")
    void testTrueFalseIncorrectAnswer() {
        Question q = new TrueFalseQuestion("Test", true);
        assertFalse(q.checkAnswer("False"), "False should be incorrect");
        assertFalse(q.checkAnswer("F"), "F should be incorrect");
    }

    @Test
    @DisplayName("True/False valid inputs should be recognized")
    void testTrueFalseValidInput() {
        TrueFalseQuestion q = new TrueFalseQuestion("Test", true);
        assertTrue(q.isValidAnswer("true"), "true is valid input");
        assertTrue(q.isValidAnswer("T"), "T is valid input");
        assertTrue(q.isValidAnswer("FALSE"), "FALSE is valid input");
        assertFalse(q.isValidAnswer("maybe"), "maybe is not valid input");
        assertFalse(q.isValidAnswer(""), "empty is not valid input");
    }

    @Test
    @DisplayName("Question validation should work correctly")
    void testQuestionValidation() {
        Question valid = new MultipleChoiceQuestion("Valid", Arrays.asList("A", "B"), 0);
        assertTrue(valid.isValid(), "Valid question should be valid");

        Question emptyText = new MultipleChoiceQuestion("", Arrays.asList("A", "B"), 0);
        assertFalse(emptyText.isValid(), "Empty text should be invalid");

        Question oneOption = new MultipleChoiceQuestion("Text", List.of("A"), 0);
        assertFalse(oneOption.isValid(), "MCQ with 1 option should be invalid");

        Question invalidIndex = new MultipleChoiceQuestion("Text", Arrays.asList("A", "B"), 5);
        assertFalse(invalidIndex.isValid(), "Invalid correct index should be invalid");
    }

    @Test
    @DisplayName("Question search should find text in question and options")
    void testQuestionSearch() {
        Question q = new MultipleChoiceQuestion("What is Java?",
                Arrays.asList("Language", "Coffee"), 0);
        assertTrue(q.search("Java"), "Should find by question text");
        assertTrue(q.search("java"), "Should be case insensitive");
        assertTrue(q.search("Coffee"), "Should find by option text");
        assertFalse(q.search("Python"), "Should not find irrelevant text");
    }

    @Test
    @DisplayName("Question difficulty should be set correctly")
    void testQuestionDifficulty() {
        Question easy = new MultipleChoiceQuestion("Test", Arrays.asList("A", "B"), 0, Difficulty.EASY);
        assertEquals(Difficulty.EASY, easy.getDifficulty(), "Difficulty should be EASY");

        Question medium = new TrueFalseQuestion("Test", true);
        assertEquals(Difficulty.MEDIUM, medium.getDifficulty(), "Default difficulty should be MEDIUM");

        Question hard = new TrueFalseQuestion("Test", true, Difficulty.HARD);
        assertEquals(Difficulty.HARD, hard.getDifficulty(), "Difficulty should be HARD");
    }

    @Test
    @DisplayName("Displayable methods should return valid values")
    void testQuestionDisplayable() {
        Question q = new MultipleChoiceQuestion("What is 2+2?", Arrays.asList("3", "4"), 1);
        assertNotNull(q.getDisplaySummary(), "Display summary should not be null");
        assertNotNull(q.getDisplayDetails(), "Display details should not be null");
        assertTrue(q.getDisplaySummary().contains("MC"), "Summary should contain type");
    }
}
