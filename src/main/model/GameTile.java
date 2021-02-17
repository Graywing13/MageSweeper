package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

// A tile in a game district map. This tile can be occupied by LifeForms and may contain mines.
public class GameTile extends Point {
    private static final int MAX_NEARBY_MINES = 8; // note: "nearby tiles" refers to this amount of the closest tiles

    private int numNearbyMines;
    private boolean isMine;
    private boolean isFlagged;
    private boolean userKnowsIdentity;
    private char uiIdentity;
    private final GameDistrict gameDistrict;
    private boolean occupiedByLifeForm;
    private GameTile[] nearbyTiles = new GameTile[MAX_NEARBY_MINES];

    // EFFECTS: creates a new, unoccupied tile that hides its identity from the user
    public GameTile(int tileXPos, int tileYPos, GameDistrict gameDistrict) {
        userKnowsIdentity = false;
        isFlagged = false;
        occupiedByLifeForm = false;
        this.x = tileXPos;
        this.y = tileYPos;
        this.gameDistrict = gameDistrict;
        uiIdentity = '?';
    }

    // EFFECTS: records the identities (information about mines) of this tile and its neighbours
    public void initializeTileInGrid() {
        discoverNearbyTiles();
    }

    // MODIFIES: this
    // EFFECTS: saves the nearby tiles as a list of tiles, then finds how many nearby tiles are mines.
    private void discoverNearbyTiles() {
        int count = 0;
        for (Direction direction : Direction.values()) {
            nearbyTiles[count] = gameDistrict.tileInDirection(x, y, direction);
            count++;
        }
        findNumNearbyMines();
    }

    // MODIFIES: this
    // EFFECTS: finds out how many mines on the nearby tiles and saves this number;
    private void findNumNearbyMines() {
        numNearbyMines = 0;
        for (GameTile tile : nearbyTiles) {
            if (tile != null && tile.isMine) {
                numNearbyMines++;
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: sets the identity of this as 'M' if this is a mine; else sets the identity as the number of nearby mines
    public void giveIdentity() {
        if (isMine) {
            uiIdentity = 'M';
        } else {
            uiIdentity = (char) (numNearbyMines + '0');
        }
        setUserKnowsIdentity(true);
    }

    // REQUIRES: there is a mage on this
    // MODIFIES: this
    /*
     * EFFECTS: when the user moves onto this:
     *    does nothing if this is a flagged tile or if the player already knows this tile's identity
     *    reveals only this tile's identity if there is at least one mine on any of this tile's adjacent tiles
     *    recursively reveals this tile's and neighbouring tiles with no nearby mines' identities if no nearby mines
     *    detonates if this is an unflagged mine and puts the current game into a lose state
     */
    public void revealNearbyTiles() {
        if (!userKnowsIdentity && !isFlagged) {
            giveIdentity();
            if (isMine) {
                gameDistrict.getTracker().endGame(EndGameOutcome.STEPPED_IN_MINE_LOSE);
            }
            if (!isMine && numNearbyMines == 0) {
                ArrayList<GameTile> tilesToReveal = new ArrayList<>(Arrays.asList(nearbyTiles));
                for (GameTile tile : tilesToReveal) {
                    if (tile != null && !tile.getUserKnowsIdentity()) {
                        tile.revealNearbyTiles();
                    }
                }
            }
        }
    }


    // getters
    public boolean getUserKnowsIdentity() {
        return userKnowsIdentity;
    }

    public boolean getOccupiedByLifeForm() {
        return occupiedByLifeForm;
    }

    public int getTileXPos() {
        return x;
    }

    public int getTileYPos() {
        return y;
    }

    public boolean getIsFlagged() {
        return isFlagged;
    }

    public char getUIIdentity() {
        return uiIdentity;
    }

    public int getNumNearbyMines() {
        return numNearbyMines;
    }

    public GameTile[] getNearbyTiles() {
        return nearbyTiles;
    }

    public boolean getIsMine() {
        return isMine;
    }


    // setters
    public void setIsMine() {
        isMine = true;
    }

    public void setIsFlagged(boolean hasFlag) {
        this.isFlagged = hasFlag;
    }

    public void setOccupiedByLifeForm(boolean occupied) {
        occupiedByLifeForm = occupied;
    }

    public void setUserKnowsIdentity(boolean knowsID) {
        userKnowsIdentity = knowsID;
    }

    public void setUIIdentityToQuestionMark() {
        this.uiIdentity = '?';
    }
}
