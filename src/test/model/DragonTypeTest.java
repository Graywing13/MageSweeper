package model;

import exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Tests for the model.DragonType enum class.
public class DragonTypeTest {
    final DragonType[] listOfDragonTypes = DragonType.values();
    final int lengthOfDragonType = listOfDragonTypes.length;

    @Test
    public void testAssignDragonNonBoundaryValid() {
        DragonType dt = null;
        try {
            dt = DragonType.assignDragon(lengthOfDragonType - 1);
        } catch (InvalidInputException e) {
            fail();
        }
        assertEquals(listOfDragonTypes[lengthOfDragonType - 2], dt);
    }

    @Test
    public void testAssignDragonLowerBoundaryValid() {
        DragonType dt = null;
        try {
            dt = DragonType.assignDragon(1);
        } catch (InvalidInputException e) {
            fail();
        }
        assertEquals(listOfDragonTypes[0], dt);
    }

    @Test
    public void testAssignDragonUpperBoundaryValid() {
        DragonType dt = null;
        try {
            dt = DragonType.assignDragon(lengthOfDragonType);
        } catch (InvalidInputException e) {
            fail();
        }
        assertEquals(listOfDragonTypes[lengthOfDragonType - 1], dt);
    }

    @Test
    public void testAssignDragonLowerBoundaryInvalid() {
        DragonType dt = null;
        try {
            dt = DragonType.assignDragon(0);
            fail();
        } catch (InvalidInputException e) {
            // expected
        }
        assertNull(dt);
    }

    @Test
    public void testAssignDragonBelowLowerBoundaryInvalid() {
        DragonType dt = null;
        try {
            dt = DragonType.assignDragon(-4);
            fail();
        } catch (InvalidInputException e) {
            // expected
        }
        assertNull(dt);
    }

    @Test
    public void testAssignDragonUpperBoundaryInvalid() {
        DragonType dt = null;
        try {
            dt = DragonType.assignDragon(lengthOfDragonType + 1);
            fail();
        } catch (InvalidInputException e) {
            // expected
        }
        assertNull(dt);
    }

    @Test
    public void testAssignDragonAboveUpperBoundaryInvalid() {
        DragonType dt = null;
        try {
            dt = DragonType.assignDragon(lengthOfDragonType + 4);
            fail();
        } catch (InvalidInputException e) {
            // expected
        }
        assertNull(dt);
    }
}
