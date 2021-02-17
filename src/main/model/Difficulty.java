package model;

import exceptions.InvalidInputException;

// An enum for the difficulty levels available for the minesweeper aspect of this project.
// INVARIANT: for every Difficulty level, the amount of mines < GameDistrict.BOARD_EDGE_LENGTH ^ 2
public enum Difficulty {
    PRACTICE(0),
    EASY(4),
    NORMAL(6),
    HARD(10);

    public final int numMines;

    // EFFECTS: a constructor that saves the amount of mines in the given difficulty level into public variables
    Difficulty(int numMines) {
        this.numMines = numMines;
    }

    // EFFECTS: returns the Difficulty level that corresponds to the given integer
    public static Difficulty assignDifficultyLvl(int wantedDifficulty) throws InvalidInputException {
        if (wantedDifficulty < 1 || wantedDifficulty > Difficulty.values().length) {
            throw new InvalidInputException();
        }
        return Difficulty.values()[wantedDifficulty - 1];
    }
}
