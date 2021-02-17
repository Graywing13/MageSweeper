package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.GameWorld;
import ui.InGameEventsTrackerConsole;

import java.awt.*;

import static model.GameDistrict.BOARD_EDGE_LENGTH;
import static org.junit.jupiter.api.Assertions.*;

// This class tests the accessible methods in model.GameDistrict
public class GameDistrictTest {
    GameDistrict gameDistrict;
    GameWorld gameWorld;

    @BeforeEach
    public void setup() {
        gameWorld = new GameWorld(GameWorld.MAGE_DEFAULT_NAME, GameWorld.WORLD_DEFAULT_NAME);
    }

    @Test
    public void testConstructorEasyBlaze() {
        gameDistrict = new GameDistrict(DragonType.BLAZE, Difficulty.EASY, gameWorld);
        assertEquals("District BE-0", gameDistrict.getDistrictName());
        assertEquals(Difficulty.EASY, gameDistrict.getDifficultyLevel());
    }

    @Test
    public void testConstructorNormalBlaze() {
        gameDistrict = new GameDistrict(DragonType.BLAZE, Difficulty.NORMAL, gameWorld);
        assertEquals("District BN-0", gameDistrict.getDistrictName());
        assertEquals(Difficulty.NORMAL, gameDistrict.getDifficultyLevel());
    }

    @Test
    public void testConstructorHardBlaze() {
        gameDistrict = new GameDistrict(DragonType.BLAZE, Difficulty.HARD, gameWorld);
        assertEquals("District BH-0", gameDistrict.getDistrictName());
        assertEquals(Difficulty.HARD, gameDistrict.getDifficultyLevel());
    }

    @Test
    public void testConstructorNormalTsunami() {
        gameDistrict = new GameDistrict(DragonType.TSUNAMI, Difficulty.NORMAL, gameWorld);
        assertEquals("District TN-0", gameDistrict.getDistrictName());
        assertEquals(Difficulty.NORMAL, gameDistrict.getDifficultyLevel());
    }

    @Test
    public void testConstructorHardDownburst() {
        gameDistrict = new GameDistrict(DragonType.DOWNBURST, Difficulty.NORMAL, gameWorld);
        assertEquals("District DN-0", gameDistrict.getDistrictName());
        assertEquals(Difficulty.NORMAL, gameDistrict.getDifficultyLevel());
    }

    @Test
    public void testStartGame() {
        gameDistrict = new GameDistrict(DragonType.DOWNBURST, Difficulty.NORMAL, gameWorld);
        InGameEventsTrackerConsole tracker = new InGameEventsTrackerConsole(gameDistrict, gameWorld.getMage(),
                gameDistrict.getDragon());
        gameDistrict.setupGame(tracker);
        tracker.startTimer();
        assertTrue(gameDistrict.isInGame());
        Dragon dragon = gameDistrict.getDragon();
        Mage mage = gameWorld.getMage();
        assertEquals(mage, dragon.getOpponent());
        assertEquals(dragon, mage.getOpponent());
        assertNotEquals(mage.currentTile, dragon.currentTile);
        assertEquals(gameDistrict, mage.gameDistrict);
        assertEquals(Difficulty.NORMAL.numMines, mage.getNumFlags());
    }

    @Test
    public void testInitializeGameBoard() {
        gameDistrict = new GameDistrict(DragonType.BLAZE, Difficulty.EASY, gameWorld);
        InGameEventsTrackerConsole tracker = new InGameEventsTrackerConsole(gameDistrict, gameWorld.getMage(),
                gameDistrict.getDragon());
        gameDistrict.setupGame(tracker);
        tracker.startTimer();
        for (int rowAtY = 0; rowAtY < BOARD_EDGE_LENGTH; rowAtY++) {
            for (int tileAtX = 0; tileAtX < BOARD_EDGE_LENGTH; tileAtX++) {
                GameTile currentTile = gameDistrict.getGameTileAtPos(tileAtX, rowAtY);
                assertNotNull(currentTile);
                currentTile.revealNearbyTiles();
                if (currentTile.getIsMine()) {
                    assertEquals('M', currentTile.getUIIdentity());
                    tracker = new InGameEventsTrackerConsole(gameDistrict, gameWorld.getMage(),
                            gameDistrict.getDragon());
                    gameDistrict.setupGame(tracker);
                    tracker.startTimer();
                } else {
                    assertEquals((char) (currentTile.getNumNearbyMines() + '0'), currentTile.getUIIdentity());
                }
            }
        }
        assertEquals(gameDistrict.getDifficultyLevel().numMines, gameDistrict.getMineCoords().length);
    }

    // Note: this test operates on the basis that the chances of getting 64 unique points consecutively is very low;
    //    it is on this basis that this test tests for randomness and repetition.
    @Test
    public void testGenerateUniquePoint() {
        gameDistrict = new GameDistrict(DragonType.BLAZE, Difficulty.PRACTICE, gameWorld);
        int boardSize = BOARD_EDGE_LENGTH * BOARD_EDGE_LENGTH;

        Point[] pointList = new Point[boardSize];
        for (int i = 0; i < boardSize; i++) {
            pointList[i] = gameDistrict.generateUniquePoint(pointList);
        }
        for (Point point : pointList) {
            assertNotNull(point);
        }

        Point blackoutPoint = new Point(0, 0);
        Point[] pointList2 = new Point[boardSize - 1];
        for (int i = 0; i < boardSize - 1; i++) {
            pointList2[i] = gameDistrict.generateUniquePoint(pointList2, blackoutPoint);
        }
        for (Point point : pointList2) {
            assertNotEquals(point, blackoutPoint);
            assertNotNull(point);
        }
    }
}
