package model;

// The list of directions used in this game.
// Note: 0, 0 is the lower left hand corner.
public enum Direction {
    NORTH(0, 1),
    NORTHWEST(-1, 1),
    WEST(-1, 0),
    SOUTHWEST(-1, -1),
    SOUTH(0, -1),
    SOUTHEAST(1, -1),
    EAST(1, 0),
    NORTHEAST(1, 1);

    public final int shiftX; // the change in x-position that movement in the given direction causes
    public final int shiftY; // the change in y-position that movement in the given direction causes

    // EFFECTS: the constructor for this enum; assigns the enumeration list's parameters to accessible variables
    Direction(int shiftX, int shiftY) {
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }
}
