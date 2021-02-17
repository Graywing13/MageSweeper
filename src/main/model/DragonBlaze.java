package model;

// A type of dragon that only moves and does melee attacks. Has an unique type and attack sequence.
public class DragonBlaze extends Dragon {

    // MODIFIES: this
    // EFFECTS: sets the dragonType and attack sequence of this dragon to predetermined values
    public DragonBlaze() {
        dragonType = DragonType.BLAZE;
        attackSequence = new DragonAction[]{DragonAction.DO_NOTHING, DragonAction.MOVE_TO_MAGE, DragonAction.MELEE,
                DragonAction.DO_NOTHING, DragonAction.MELEE, DragonAction.MOVE_TO_MAGE, DragonAction.MOVE_TO_MAGE};
    }
}
