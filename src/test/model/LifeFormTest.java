package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.GameWorld;
import ui.InGameEventsTrackerConsole;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

// This class tests the methods in model.LifeForm
public class LifeFormTest {
    GameWorld gameWorld;
    GameDistrict gameDistrictD;
    LifeForm mage;
    LifeForm dragonD;

    @BeforeEach
    public void setup() {
        gameWorld = new GameWorld(GameWorld.MAGE_DEFAULT_NAME, GameWorld.WORLD_DEFAULT_NAME);
        gameDistrictD = new GameDistrict(DragonType.DOWNBURST, Difficulty.PRACTICE, gameWorld);
        mage = gameWorld.getMage();
        dragonD = gameDistrictD.getDragon();
        InGameEventsTrackerConsole tracker = new InGameEventsTrackerConsole(gameDistrictD, gameWorld.getMage(),
                gameDistrictD.getDragon());
        gameDistrictD.setupGame(tracker);
        tracker.startTimer();
    }

    @Test
    public void testTakeDamageMageDoesNotDieNonBoundary() {
        mage.takeDamage(mage.getHP() / 2);
        assertEquals(EndGameOutcome.STILL_PLAYING_GAME, gameDistrictD.getGameOutcome());
    }

    @Test
    public void testTakeDamageMageDoesNotDieOneAwayFromDying() {
        mage.takeDamage(mage.getHP() - 1);
        assertEquals(EndGameOutcome.STILL_PLAYING_GAME, gameDistrictD.getGameOutcome());
    }

    @Test
    public void testTakeDamageMageDoesDieExactAmount() {
        mage.takeDamage(mage.getHP());
        assertEquals(EndGameOutcome.NO_MAGE_HP_LOSE, gameDistrictD.getGameOutcome());
    }

    @Test
    public void testTakeDamageMageDoesDieOneOverExactAmount() {
        mage.takeDamage(mage.getHP() + 1);
        assertEquals(EndGameOutcome.NO_MAGE_HP_LOSE, gameDistrictD.getGameOutcome());
    }

    @Test
    public void testTakeDamageMageDoesDieManyOverExactAmount() {
        mage.takeDamage(mage.getHP() + 10);
        assertEquals(EndGameOutcome.NO_MAGE_HP_LOSE, gameDistrictD.getGameOutcome());
    }

    @Test
    public void testTakeDamageDragonDoesNotDieNonBoundary() {
        dragonD.takeDamage(dragonD.getHP() / 2);
        assertEquals(EndGameOutcome.STILL_PLAYING_GAME, gameDistrictD.getGameOutcome());
    }

    @Test
    public void testTakeDamageDragonDoesNotDieOneAwayFromDying() {
        dragonD.takeDamage(dragonD.getHP() - 1);
        assertEquals(EndGameOutcome.STILL_PLAYING_GAME, gameDistrictD.getGameOutcome());
    }

    @Test
    public void testTakeDamageDragonDoesDieExactAmount() {
        dragonD.takeDamage(dragonD.getHP());
        assertEquals(EndGameOutcome.NO_DRAGON_HP_WIN, gameDistrictD.getGameOutcome());
    }

    @Test
    public void testTakeDamageDragonDoesDieOneOverExactAmount() {
        dragonD.takeDamage(dragonD.getHP() + 1);
        assertEquals(EndGameOutcome.NO_DRAGON_HP_WIN, gameDistrictD.getGameOutcome());
    }

    @Test
    public void testTakeDamageDragonDoesDieManyOverExactAmount() {
        dragonD.takeDamage(dragonD.getHP() + 10);
        assertEquals(EndGameOutcome.NO_DRAGON_HP_WIN, gameDistrictD.getGameOutcome());
    }

    @Test
    public void testMoveValid() {
        dragonD.setLocation(new Point(0, 0));
        for (Direction direction : Direction.values()) {
            mage.setLocation(new Point(4, 4));
            mage.move(direction);
            assertEquals(new Point(4 + direction.shiftX, 4 + direction.shiftY), mage.currentTile);
        }
    }


    @Test
    public void testMoveInvalidTileIsNull() {
        for (Direction direction : Direction.values()) {
            int setToX;
            int setToY;
            if (direction.shiftX == -1) {
                setToX = 0;
            } else {
                setToX = GameDistrict.BOARD_EDGE_LENGTH - 1;
            }
            if (direction.shiftY == -1) {
                setToY = 0;
            } else {
                setToY = GameDistrict.BOARD_EDGE_LENGTH - 1;
            }
            mage.setLocation(new Point(setToX, setToY));
            mage.move(direction);
            assertEquals(setToX, mage.currentTile.x);
            assertEquals(setToY, mage.currentTile.y);
        }
    }

    @Test
    public void testMoveInvalidTileIsOccupied() {
        dragonD.setLocation(new Point(0, 0));
        mage.setLocation(new Point(0, 1));
        mage.move(Direction.SOUTH);
        assertEquals(gameDistrictD.getGameTileAtPos(0, 1), mage.currentTile);
    }
}
