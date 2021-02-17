package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// This class tests the methods in model.DragonTsunami
public class DragonTsunamiTest {
    DragonTsunami dragonTsunami;

    @Test
    public void testConstructor() {
        dragonTsunami = new DragonTsunami();
        assertEquals(DragonType.TSUNAMI, dragonTsunami.dragonType);
    }
}
