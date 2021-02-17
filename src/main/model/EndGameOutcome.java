package model;

// This enum lists the available types of end game outcomes that are accessed throughout the project.
// Note: there are no tests that are writeable for this enum class but all code in this file is covered in other tests.
public enum EndGameOutcome {
    NO_DRAGON_HP_WIN,
    STEPPED_IN_MINE_LOSE,
    NO_MAGE_HP_LOSE,
    NO_TIME_LOSE,
    STILL_PLAYING_GAME
}
