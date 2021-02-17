package model;

// An enum for the different types of actions the dragons can do, includes the action's damage modifier & attack range.
public enum DragonAction {
    DO_NOTHING(0, 0),    // do nothing (stall)
    MOVE_TO_MAGE(0, 0),  // just moving the dragon, not attacking
    MELEE(1, 1.5),       // range is the tiles adjacent to dragon; therefore ceiling(sqrt(2))
    RANGED(4, 2.3),      // range is equal to the mage's range
    BLAST(9, 9.9);       // range is whole map; therefore ceiling(sqrt(98))

    public final int damageModifier;
    public final double atkRange;

    // EFFECTS: a constructor that saves the damage modifier and attack range of each action into public variables
    DragonAction(int damageModifier, double atkRange) {
        this.damageModifier = damageModifier;
        this.atkRange = atkRange;
    }
}
