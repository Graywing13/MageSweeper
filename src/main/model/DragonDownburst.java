package model;

// A type of dragon that can do all actions in DragonAction. Has an unique type and attack sequence.
public class DragonDownburst extends Dragon {
    // MODIFIES: this
    // EFFECTS: sets the dragonType and attack sequence of this dragon to predetermined values
    public DragonDownburst() {
        dragonType = DragonType.DOWNBURST;
        attackSequence = new DragonAction[]{DragonAction.DO_NOTHING, DragonAction.BLAST, DragonAction.RANGED,
                DragonAction.DO_NOTHING, DragonAction.RANGED, DragonAction.DO_NOTHING, DragonAction.MOVE_TO_MAGE,
                DragonAction.MOVE_TO_MAGE, DragonAction.MELEE, DragonAction.MELEE, DragonAction.MOVE_TO_MAGE};
    }
}
