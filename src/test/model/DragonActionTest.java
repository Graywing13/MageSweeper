package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// This class tests the methods in model.DragonAction
public class DragonActionTest {
    @Test
    public void testConstructorDamageModifier() {
        assertEquals(0, DragonAction.DO_NOTHING.damageModifier);
        assertEquals(0, DragonAction.MOVE_TO_MAGE.damageModifier);
        assertEquals(1, DragonAction.MELEE.damageModifier);
        assertEquals(4, DragonAction.RANGED.damageModifier);
        assertEquals(9, DragonAction.BLAST.damageModifier);
    }

    @Test
    public void testConstructorAtkRange() {
        assertEquals(0, DragonAction.DO_NOTHING.atkRange);
        assertEquals(0, DragonAction.MOVE_TO_MAGE.atkRange);
        assertEquals(1.5, DragonAction.MELEE.atkRange);
        assertEquals(2.3, DragonAction.RANGED.atkRange);
        assertEquals(9.9, DragonAction.BLAST.atkRange);
    }
}
