package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.GameWorld;
import ui.InGameEventsTrackerConsole;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

// This class tests the methods in model.Mage
public class MageTest {
    final static Difficulty CLASS_DEFAULT_DIFFICULTY = Difficulty.PRACTICE;

    GameWorld gameWorld;
    Dragon dragonB;
    GameDistrict gameDistrictB;
    Mage mage;

    @BeforeEach
    public void setUp() {
        gameWorld = new GameWorld(GameWorld.MAGE_DEFAULT_NAME, GameWorld.WORLD_DEFAULT_NAME);
        mage = gameWorld.getMage();
        gameDistrictB = new GameDistrict(DragonType.BLAZE, CLASS_DEFAULT_DIFFICULTY, gameWorld);
        dragonB = gameDistrictB.getDragon();
        InGameEventsTrackerConsole tracker = new InGameEventsTrackerConsole(gameDistrictB, gameWorld.getMage(),
                gameDistrictB.getDragon());
        gameDistrictB.setupGame(tracker);
        tracker.startTimer();
        mage.setGameDistrict(gameDistrictB);
        dragonB.setOpponent(mage);
        mage.setLocation(new Point(0, 0));
    }

    @Test
    public void testGetMageName() {
        assertEquals("Djorn", mage.getMageName());
    }

    @Test
    public void testTakeDamageSpecialSkillUp() {
        mage.setSkillPoints(10);
        mage.tryToUseSkill(true);
        mage.takeDamage(1000);
        assertEquals(Mage.MAGE_MAX_HP, mage.getHP());
    }

    @Test
    public void testAttackOpponentMageAttackingInRangeBoundary() {
        dragonB.setLocation(new Point(1, 2));
        mage.attackOpponent();
        assertEquals(Dragon.DRAGON_INITIAL_HP - mage.atk, dragonB.getHP());
        assertEquals(Mage.MAGE_MAX_HP, mage.getHP());
    }

    @Test
    public void testAttackOpponentMageAttackingInRangeNonBoundary() {
        dragonB.setLocation(new Point(1, 1));
        mage.attackOpponent();
        assertEquals(Dragon.DRAGON_INITIAL_HP - mage.atk, dragonB.getHP());
        assertEquals(Mage.MAGE_MAX_HP, mage.getHP());
    }

    @Test
    public void testAttackOpponentMageAttackingOutOfRangeBoundary() {
        dragonB.setLocation(new Point(0, 3));
        mage.attackOpponent();
        assertEquals(Dragon.DRAGON_INITIAL_HP, dragonB.getHP());
        assertEquals(Mage.MAGE_MAX_HP, mage.getHP());
    }

    @Test
    public void testAttackOpponentMageAttackingOutOfRangeNonBoundary() {
        dragonB.setLocation(new Point(0, 5));
        mage.attackOpponent();
        assertEquals(Dragon.DRAGON_INITIAL_HP, dragonB.getHP());
        assertEquals(Mage.MAGE_MAX_HP, mage.getHP());
    }

    @Test
    public void testFlagTileNoInitialFlagsValidDirectionIDUnknownToUser() {
        gameDistrictB.resetDistrictAndLifeForms();
        mage.setLocation(new Point(4, 4));
        for (Direction d : Direction.values()) {
            mage.flagTile(d);
            assertFalse(gameDistrictB.getGameTileAtPos(4 + d.shiftX, 4 + d.shiftY).getIsFlagged());
        }
    }

    @Test
    public void testFlagTileNoInitialFlagsValidDirectionIDKnownToUser() {
        mage.setLocation(new Point(4, 4));
        for (Direction d : Direction.values()) {
            mage.flagTile(d);
            assertFalse(gameDistrictB.getGameTileAtPos(4 + d.shiftX, 4 + d.shiftY).getIsFlagged());
        }
    }

    @Test
    public void testFlagTileOneInitialFlagValidDirection() {
        gameDistrictB.resetDistrictAndLifeForms();
        mage.setLocation(new Point(4, 4));
        mage.setNumFlags(1);
        for (Direction d : Direction.values()) {
            GameTile targetTile = gameDistrictB.getGameTileAtPos(4 + d.shiftX, 4 + d.shiftY);
            mage.flagTile(d);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {

            }
            assertEquals(0, mage.getNumFlags());
            assertTrue(targetTile.getIsFlagged());
            mage.flagTile(d);
            assertEquals(1, mage.getNumFlags());
            assertFalse(targetTile.getIsFlagged());
        }
    }

    @Test
    public void testFlagTileSeveralInitialFlagDownAtSameTimeValidDirection() {
        gameDistrictB.resetDistrictAndLifeForms();
        mage.setLocation(new Point(4, 4));
        mage.setNumFlags(8);
        for (int i = Direction.values().length - 1; i >= 0; i--) {
            Direction d = Direction.values()[i];
            GameTile targetTile = gameDistrictB.getGameTileAtPos(4 + d.shiftX, 4 + d.shiftY);
            mage.flagTile(d);
            assertEquals(i, mage.getNumFlags());
            assertTrue(targetTile.getIsFlagged());
        }
        for (int i = 1; i <= Direction.values().length; i++) {
            Direction d = Direction.values()[i - 1];
            GameTile targetTile = gameDistrictB.getGameTileAtPos(4 + d.shiftX, 4 + d.shiftY);
            mage.flagTile(d);
            assertEquals(i, mage.getNumFlags());
            assertFalse(targetTile.getIsFlagged());
        }
    }

    @Test
    public void testFlagTileNoInitialFlagsInvalidDirection() {
        try {
            mage.flagTile(Direction.SOUTH);
        } catch (NullPointerException e) {
            fail();
        }
        assertEquals(0, mage.getNumFlags());
    }

    @Test
    public void testFlagTileHasInitialFlagsInvalidDirection() {
        mage.setNumFlags(1);
        try {
            mage.flagTile(Direction.SOUTH);
        } catch (NullPointerException e) {
            fail();
        }
        assertEquals(1, mage.getNumFlags());
    }

    @Test
    public void testModifyMageSkillPointsSkillInUseHasSP() {
        mage.setSkillPoints(Mage.MAX_SKILL_POINTS);
        assertTrue(mage.tryToUseSkill(true));
        mage.modifyMageSkillPoints();
        assertEquals(Mage.MAX_SKILL_POINTS - Mage.SKILL_COST_IN_POINTS_PER_SECOND, mage.getSkillPoints());
    }

    @Test
    public void testModifyMageSkillPointsSkillInUseBoundaryEnoughSP() {
        mage.setSkillPoints(Mage.SKILL_COST_IN_POINTS_PER_SECOND);
        assertTrue(mage.tryToUseSkill(true));
        mage.modifyMageSkillPoints();
        assertEquals(0, mage.getSkillPoints());
    }

    @Test
    public void testModifyMageSkillPointsSkillInUseBoundaryNotEnoughSP() {
        mage.setSkillPoints(Mage.SKILL_COST_IN_POINTS_PER_SECOND - Mage.SKILL_INACTIVE_POINTS_GAIN_PER_SECOND);
        assertFalse(mage.tryToUseSkill(true));
        mage.modifyMageSkillPoints();
        assertEquals(Mage.SKILL_COST_IN_POINTS_PER_SECOND, mage.getSkillPoints());
    }

    @Test
    public void testModifyMageSkillPointsSkillInUseNotEnoughSP() {
        mage.setSkillPoints(0);
        assertFalse(mage.tryToUseSkill(true));
        mage.modifyMageSkillPoints();
        assertEquals(Mage.SKILL_INACTIVE_POINTS_GAIN_PER_SECOND, mage.getSkillPoints());
    }

    @Test
    public void testModifyMageSkillPointsSkillNotInUseNotMaxedSP() {
        mage.setSkillPoints(0);
        assertFalse(mage.tryToUseSkill(false));
        mage.modifyMageSkillPoints();
        assertEquals(Mage.SKILL_INACTIVE_POINTS_GAIN_PER_SECOND, mage.getSkillPoints());
    }

    @Test
    public void testModifyMageSkillPointsSkillNotInUseBoundaryNotMaxedSP() {
        mage.setSkillPoints(Mage.MAX_SKILL_POINTS - Mage.SKILL_INACTIVE_POINTS_GAIN_PER_SECOND);
        assertFalse(mage.tryToUseSkill(false));
        mage.modifyMageSkillPoints();
        assertEquals(Mage.MAX_SKILL_POINTS, mage.getSkillPoints());
    }

    @Test
    public void testModifyMageSkillPointsSkillNotInUseBoundaryMaxedSP() {
        mage.setSkillPoints(Mage.MAX_SKILL_POINTS);
        assertFalse(mage.tryToUseSkill(false));
        mage.modifyMageSkillPoints();
        assertEquals(Mage.MAX_SKILL_POINTS, mage.getSkillPoints());
    }

    @Test
    public void testResetLifeFormForGame() {
        mage.resetLifeFormForGame();
        assertEquals(Mage.MAGE_INITIAL_SKILL_POINTS, mage.getSkillPoints());
        assertEquals(Mage.MAGE_MAX_HP, mage.getHP());
        assertFalse(mage.getSkillInUse());
        assertEquals(0, mage.getHitCombo());
    }
}
