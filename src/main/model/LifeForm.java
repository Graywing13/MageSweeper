package model;

import java.awt.*;

// A form of life that is either a dragon or a mage. These are opponents and attack each other in the game.
public abstract class LifeForm {
    protected int hp;
    protected int atk;
    protected LifeForm opponent;
    protected GameDistrict gameDistrict;
    protected GameTile currentTile;

    // MODIFIES: this
    // EFFECTS: if a tile in the specified Direction is available, moves the life form onto that tile. Else do nothing.
    public void move(Direction d) {
        GameTile targetTile = gameDistrict.tileInDirection(currentTile.getTileXPos(), currentTile.getTileYPos(), d);
        if (null != targetTile && !targetTile.getOccupiedByLifeForm()) {
            currentTile.setOccupiedByLifeForm(false);
            updateCurrentLocation(targetTile);
        }
    }

    // REQUIRES: dmgToTake is a non-negative number
    // MODIFIES: this
    // EFFECTS: subtracts dmgToTake from the life form's hp and calls outOfHP() if hp is <= 0.
    protected void takeDamage(int dmgToTake) {
        hp -= dmgToTake;
        if (hp <= 0) {
            hp = 0;
            outOfHP();
        }
    }

    // EFFECTS: puts the game in a win or lose state, depending on if the life form is a mage or a dragon
    protected abstract void outOfHP();

    // MODIFIES: this.opponent
    // EFFECTS: deals damage to the opponent if they are in range; else does nothing. returns whether opponent was hit
    protected abstract boolean attackOpponent();

    // MODIFIES: this
    // EFFECTS: determines the range of the life form's attack at its current position
    protected abstract double determineRange();

    // EFFECTS: calculates the amount of damage to be done to the user if the attack connects
    protected abstract int attackFormula();

    // EFFECTS: returns true if the life form's opponent is in their attack range; else returns false
    protected boolean opponentIsInRange(double atkRange) {
        return atkRange >= currentTile.distance(opponent.currentTile);
    }

    // REQUIRES: the point provided has integer coordinates i, where 0 <= i <= BOARD_EDGE_LENGTH.
    //    Additionally, the point does not contain a mine and is not currently occupied by another life form.
    // MODIFIES: this
    // EFFECTS: places the life form at the specified location
    protected void setLocation(Point p) {
        int x = (int) p.getX();
        int y = (int) p.getY();
        GameTile targetTile = gameDistrict.getGameTileAtPos(x, y);
        updateCurrentLocation(targetTile);
    }

    // MODIFIES: this
    // EFFECTS: places the life form on the provided tile and updates its location records accordingly
    protected void updateCurrentLocation(GameTile targetTile) {
        currentTile = targetTile;
        currentTile.setOccupiedByLifeForm(true);
    }

    // MODIFIES: this
    // EFFECTS: resets the life form so they are ready for the game
    public abstract void resetLifeFormForGame();


    // getters
    public LifeForm getOpponent() {
        return opponent;
    }

    public int getHP() {
        return hp;
    }

    public GameTile getCurrentTile() {
        return currentTile;
    }


    // setters
    public void setOpponent(LifeForm lifeForm) {
        opponent = lifeForm;
    }

    public void setGameDistrict(GameDistrict gd) {
        gameDistrict = gd;
    }
}
