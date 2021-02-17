package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// This class tests the methods in model.Direction
public class DirectionTest {
    @Test
    public void testConstructorX() {
        assertEquals(0, Direction.NORTH.shiftX);
        assertEquals(1, Direction.NORTHEAST.shiftX);
        assertEquals(1, Direction.EAST.shiftX);
        assertEquals(1, Direction.SOUTHEAST.shiftX);
        assertEquals(0, Direction.SOUTH.shiftX);
        assertEquals(-1, Direction.SOUTHWEST.shiftX);
        assertEquals(-1, Direction.WEST.shiftX);
        assertEquals(-1, Direction.NORTHWEST.shiftX);
    }

    @Test
    public void testConstructorY() {
        assertEquals(1, Direction.NORTH.shiftY);
        assertEquals(1, Direction.NORTHEAST.shiftY);
        assertEquals(0, Direction.EAST.shiftY);
        assertEquals(-1, Direction.SOUTHEAST.shiftY);
        assertEquals(-1, Direction.SOUTH.shiftY);
        assertEquals(-1, Direction.SOUTHWEST.shiftY);
        assertEquals(0, Direction.WEST.shiftY);
        assertEquals(1, Direction.NORTHWEST.shiftY);
    }
}
