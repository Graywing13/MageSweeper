package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.GameWorld;
import ui.InGameEventsTrackerConsole;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

// This class tests the methods in model.GameTile
public class GameTileTest {
    GameWorld gameWorld;
    GameDistrict gameDistrictWithMines;
    GameDistrict gameDistrictNoMines;
    Point[] mineCoords;

    @BeforeEach
    public void setup() {
        gameWorld = new GameWorld(GameWorld.MAGE_DEFAULT_NAME, GameWorld.WORLD_DEFAULT_NAME);
        gameDistrictWithMines = new GameDistrict(DragonType.BLAZE, Difficulty.NORMAL, gameWorld);
        gameDistrictNoMines = new GameDistrict(DragonType.BLAZE, Difficulty.PRACTICE, gameWorld);
        gameDistrictWithMines.initializeGameBoard();
        gameDistrictNoMines.initializeGameBoard();
        mineCoords = gameDistrictWithMines.getMineCoords();
        InGameEventsTrackerConsole tracker = new InGameEventsTrackerConsole(gameDistrictWithMines, gameWorld.getMage(),
                gameDistrictWithMines.getDragon());
        gameDistrictWithMines.setupGame(tracker);
        tracker.startTimer();
    }

    @Test
    public void testRevealNearbyTilesNoNearbyMines() {
        GameTile initTile = gameDistrictNoMines.getGameTileAtPos(4, 4);
        char tileID = initTile.getUIIdentity();
        assertEquals('?', tileID);
        assertFalse(initTile.getUserKnowsIdentity());
        initTile.revealNearbyTiles();
        assertEquals(EndGameOutcome.STILL_PLAYING_GAME, gameDistrictNoMines.getGameOutcome());
        for (GameTile tile : initTile.getNearbyTiles()) {
            if (tile != null) {
                assertTrue(tile.getUserKnowsIdentity());
            }
        }
        assertEquals('0', initTile.getUIIdentity());
    }

    @Test
    public void testRevealNearbyTilesYesNearbyMines() {
        gameDistrictWithMines.resetDistrictAndLifeForms();
        GameTile initTile = null;
        while (initTile == null || initTile.getNumNearbyMines() == 0) {
            Point initiationPoint = gameDistrictWithMines.generateUniquePoint(mineCoords);
            initTile = gameDistrictWithMines.getGameTileAtPos(initiationPoint.x, initiationPoint.y);
        }
        assertEquals('?', initTile.getUIIdentity());
        assertFalse(initTile.getUserKnowsIdentity());
        initTile.revealNearbyTiles();
        assertEquals(EndGameOutcome.STILL_PLAYING_GAME, gameDistrictNoMines.getGameOutcome());
        for (GameTile tile : initTile.getNearbyTiles()) {
            if (tile != null) {
                assertFalse(tile.getUserKnowsIdentity());
            }
        }
        assertEquals((char) (initTile.getNumNearbyMines() + '0'), initTile.getUIIdentity());
    }

    @Test
    public void testRevealNearbyTilesSelfIsAMine() {
        Point initiationPoint = mineCoords[0];
        GameTile initTile = gameDistrictWithMines.getGameTileAtPos(initiationPoint.x, initiationPoint.y);
        char tileID = initTile.getUIIdentity();
        assertEquals('?', tileID);
        assertFalse(initTile.getUserKnowsIdentity());
        initTile.revealNearbyTiles();
        assertEquals(EndGameOutcome.STEPPED_IN_MINE_LOSE, gameDistrictWithMines.getGameOutcome());
        assertEquals('M', initTile.getUIIdentity());
    }

    @Test
    public void testRevealNearbyTilesTilesAreFlagged() {
        GameTile targetTile = gameDistrictNoMines.getGameTileAtPos(4, 4);
        GameTile[] listOfModifiedTiles = new GameTile[Direction.values().length];
        for (int i = 0; i < Direction.values().length; i++) {
            Direction d = Direction.values()[i];
            GameTile tileToModify = gameDistrictNoMines.getGameTileAtPos(4 + d.shiftX, 4 + d.shiftY);
            tileToModify.setIsFlagged(true);
            assertTrue(tileToModify.getIsFlagged());
            listOfModifiedTiles[i] = tileToModify;
        }
        targetTile.revealNearbyTiles();
        for (GameTile tile : listOfModifiedTiles) {
            assertFalse(tile.getUserKnowsIdentity());
        }
    }
}
