package model;

import exceptions.InvalidInputException;

// This enum lists the available types of dragons that are accessed throughout the project.
public enum DragonType {
    BLAZE,
    TSUNAMI,
    DOWNBURST;

    // EFFECTS: returns the Dragon that corresponds to the given integer
    public static DragonType assignDragon(int wantedDragon) throws InvalidInputException {
        if (wantedDragon < 1 || wantedDragon > DragonType.values().length) {
            throw new InvalidInputException();
        }
        return DragonType.values()[wantedDragon - 1];
    }
}
