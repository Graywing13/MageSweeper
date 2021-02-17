package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// This class tests the methods in model.DragonDownburst
public class DragonDownburstTest {
    DragonDownburst dragonDownburst;

    @Test
    public void testConstructor() {
        dragonDownburst = new DragonDownburst();
        assertEquals(DragonType.DOWNBURST, dragonDownburst.dragonType);
    }
}
