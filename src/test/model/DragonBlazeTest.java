package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// This class tests the methods in model.DragonBlaze
public class DragonBlazeTest {
    DragonBlaze dragonBlaze;

    @Test
    public void testConstructor() {
        dragonBlaze = new DragonBlaze();
        assertEquals(DragonType.BLAZE, dragonBlaze.dragonType);
    }
}
