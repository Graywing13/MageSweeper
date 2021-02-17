package model;

// A type of dragon that can do all actions in DragonAction except blasts. Has an unique type and attack sequence.
public class DragonTsunami extends Dragon {
    // MODIFIES: this
    // EFFECTS: sets the dragonType and attack sequence of this dragon to predetermined values
    public DragonTsunami() {
        dragonType = DragonType.TSUNAMI;
        attackSequence = new DragonAction[]{DragonAction.DO_NOTHING, DragonAction.MOVE_TO_MAGE, DragonAction.MELEE,
                DragonAction.DO_NOTHING, DragonAction.RANGED, DragonAction.RANGED, DragonAction.MOVE_TO_MAGE,
                DragonAction.MOVE_TO_MAGE, DragonAction.MELEE};
    }
}
