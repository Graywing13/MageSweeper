package model;

import exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// This class tests the methods in model.Difficulty
class DifficultyTest {
    final Difficulty[] listOfDifficulty = Difficulty.values();
    final int lengthOfDifficulty = listOfDifficulty.length;

    @Test
    public void testConstructor() {
        assertEquals(0, Difficulty.PRACTICE.numMines);
        assertEquals(4, Difficulty.EASY.numMines);
        assertEquals(6, Difficulty.NORMAL.numMines);
        assertEquals(10, Difficulty.HARD.numMines);
    }

    @Test
    public void testAssignDifficultyLvlNonBoundaryValid() {
        Difficulty d = null;
        try {
            d = Difficulty.assignDifficultyLvl(lengthOfDifficulty - 1);
        } catch (InvalidInputException e) {
            fail();
        }
        assertEquals(listOfDifficulty[lengthOfDifficulty - 2], d);
    }

    @Test
    public void testAssignDifficultyLvlLowerBoundaryValid() {
        Difficulty dt = null;
        try {
            dt = Difficulty.assignDifficultyLvl(1);
        } catch (InvalidInputException e) {
            fail();
        }
        assertEquals(listOfDifficulty[0], dt);
    }

    @Test
    public void testAssignDifficultyLvlUpperBoundaryValid() {
        Difficulty dt = null;
        try {
            dt = Difficulty.assignDifficultyLvl(lengthOfDifficulty);
        } catch (InvalidInputException e) {
            fail();
        }
        assertEquals(listOfDifficulty[lengthOfDifficulty - 1], dt);
    }

    @Test
    public void testAssignDifficultyLvlLowerBoundaryInvalid() {
        Difficulty dt = null;
        try {
            dt = Difficulty.assignDifficultyLvl(0);
            fail();
        } catch (InvalidInputException e) {
            // expected
        }
        assertNull(dt);
    }

    @Test
    public void testAssignDifficultyLvlBelowLowerBoundaryInvalid() {
        Difficulty dt = null;
        try {
            dt = Difficulty.assignDifficultyLvl(-4);
            fail();
        } catch (InvalidInputException e) {
            // expected
        }
        assertNull(dt);
    }

    @Test
    public void testAssignDifficultyLvlUpperBoundaryInvalid() {
        Difficulty dt = null;
        try {
            dt = Difficulty.assignDifficultyLvl(lengthOfDifficulty + 1);
            fail();
        } catch (InvalidInputException e) {
            // expected
        }
        assertNull(dt);
    }

    @Test
    public void testAssignDifficultyLvlAboveUpperBoundaryInvalid() {
        Difficulty dt = null;
        try {
            dt = Difficulty.assignDifficultyLvl(lengthOfDifficulty + 4);
            fail();
        } catch (InvalidInputException e) {
            // expected
        }
        assertNull(dt);
    }
}