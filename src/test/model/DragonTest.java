package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.GameWorld;
import ui.InGameEventsTrackerConsole;

import java.awt.*;

import static model.Dragon.initDragon;
import static org.junit.jupiter.api.Assertions.assertEquals;

// This class tests the accessible methods in model.Dragon
public class DragonTest {
    GameWorld gameWorld;
    Dragon dragonB;
    GameDistrict gameDistrictB;
    Mage mage;

    @BeforeEach
    public void setUp() {
        gameWorld = new GameWorld(GameWorld.MAGE_DEFAULT_NAME, GameWorld.WORLD_DEFAULT_NAME);
        mage = gameWorld.getMage();
        gameDistrictB = new GameDistrict(DragonType.BLAZE, Difficulty.PRACTICE, gameWorld);
        dragonB = gameDistrictB.getDragon();
        InGameEventsTrackerConsole tracker = new InGameEventsTrackerConsole(gameDistrictB, gameWorld.getMage(),
                gameDistrictB.getDragon());
        gameDistrictB.setupGame(tracker);
        tracker.startTimer();
    }

    @Test
    public void testInitDragon() {
        dragonB = initDragon(DragonType.BLAZE, null);
        assertEquals(dragonB.getConstantDragonAtk(), dragonB.getVarDragonAtk());
        assertEquals(dragonB.getConstantDragonMaxHp(), dragonB.getVarDragonHP());
        assertEquals(DragonType.BLAZE, dragonB.getDragonType());
    }

    @Test
    public void testAttackFormula() {
        DragonDownburst dragonD = new DragonDownburst();
        int atkSequenceLength = dragonD.attackSequence.length;
        for (int i = 0; i < atkSequenceLength; i++) {
            assertEquals(dragonD.atk * dragonD.attackSequence[i].damageModifier, dragonD.attackFormula());
        }
    }

    @Test
    public void testCurrentDragonAttack() {
        DragonDownburst dragonD = new DragonDownburst();
        int atkSequenceLength = dragonD.attackSequence.length;
        for (int i = 0; i < atkSequenceLength; i++) {
            assertEquals(dragonD.attackSequence[i], dragonD.incomingDragonAttack());
            dragonD.nextAttackSequencePos();
        }
    }

    @Test
    public void testDetermineDragonRange() {
        DragonDownburst dragonD = new DragonDownburst();
        int atkSequenceLength = dragonD.attackSequence.length;
        for (int i = 0; i < atkSequenceLength; i++) {
            assertEquals(dragonD.attackSequence[i].atkRange, dragonD.determineRange());
            dragonD.nextAttackSequencePos();
        }
    }

    @Test
    public void testNextAttackSequencePos() {
        dragonB = new DragonBlaze();
        int atkSequenceLength = dragonB.attackSequence.length;
        for (int i = 0; i < atkSequenceLength * 3; i++) {
            assertEquals(dragonB.attackSequence[i % atkSequenceLength], dragonB.incomingDragonAttack());
            dragonB.nextAttackSequencePos();
        }
    }

    @Test
    public void testSetGameDistrict() {
        GameWorld gameWorld = new GameWorld(GameWorld.MAGE_DEFAULT_NAME, GameWorld.WORLD_DEFAULT_NAME);
        gameDistrictB = new GameDistrict(DragonType.BLAZE, Difficulty.EASY, gameWorld);
        dragonB.setGameDistrict(gameDistrictB);
        assertEquals(gameDistrictB, dragonB.gameDistrict);
    }


    @Test
    public void testAttackOpponentDragonAttackingInRangeNonBoundary() {
        mage.setGameDistrict(gameDistrictB);
        dragonB.setOpponent(mage);
        mage.setLocation(new Point(0, 0));
        dragonB.setLocation(new Point(0, 1));
        dragonB.nextAttackSequencePos();
        dragonB.nextAttackSequencePos();
        assertEquals(2, dragonB.attackSequencePointer);
        dragonB.attackOpponent();
        assertEquals(Mage.MAGE_MAX_HP
                - Dragon.DRAGON_ATK * dragonB.attackSequence[2].damageModifier, mage.getHP());
        assertEquals(Dragon.DRAGON_INITIAL_HP, dragonB.getHP());
    }

    @Test
    public void testAttackOpponentDragonAttackingInRangeBoundary() {
        mage.setGameDistrict(gameDistrictB);
        dragonB.setOpponent(mage);
        mage.setLocation(new Point(0, 0));
        dragonB.setLocation(new Point(1, 1));
        dragonB.nextAttackSequencePos();
        dragonB.nextAttackSequencePos();
        assertEquals(2, dragonB.attackSequencePointer);
        dragonB.attackOpponent();
        assertEquals(Mage.MAGE_MAX_HP
                - Dragon.DRAGON_ATK * dragonB.attackSequence[2].damageModifier, mage.getHP());
        assertEquals(Dragon.DRAGON_INITIAL_HP, dragonB.getHP());
    }

    @Test
    public void testAttackOpponentDragonAttackingOutOfRangeBoundary() {
        mage.setGameDistrict(gameDistrictB);
        dragonB.setOpponent(mage);
        mage.setLocation(new Point(0, 0));
        dragonB.setLocation(new Point(0, 2));
        dragonB.nextAttackSequencePos();
        dragonB.nextAttackSequencePos();
        assertEquals(2, dragonB.attackSequencePointer);
        dragonB.attackOpponent();
        assertEquals(Mage.MAGE_MAX_HP, mage.getHP());
        assertEquals(Dragon.DRAGON_INITIAL_HP, dragonB.getHP());
    }

    @Test
    public void testAttackOpponentDragonAttackingOutOfRangeNonBoundary() {
        mage.setGameDistrict(gameDistrictB);
        dragonB.setOpponent(mage);
        mage.setLocation(new Point(0, 0));
        dragonB.setLocation(new Point(0, 5));
        dragonB.nextAttackSequencePos();
        dragonB.nextAttackSequencePos();
        assertEquals(2, dragonB.attackSequencePointer);
        dragonB.attackOpponent();
        assertEquals(Mage.MAGE_MAX_HP, mage.getHP());
        assertEquals(Dragon.DRAGON_INITIAL_HP, dragonB.getHP());
    }

    @Test
    public void testAttackOpponentDragonMoveSWToMageCanMove() {
        dragonMoveLocationChecking(0, 0, 1, 1);
    }

    @Test
    public void testAttackOpponentDragonMoveSWToMageCannotMove() {
        dragonMoveLocationChecking(1, 1, 2, 2);
    }

    @Test
    public void testAttackOpponentDragonMoveWToMageCanMove() {
        dragonMoveLocationChecking(0, 2, 1, 2);
    }

    @Test
    public void testAttackOpponentDragonMoveWToMageCannotMove() {
        dragonMoveLocationChecking(1, 2, 2, 2);
    }

    @Test
    public void testAttackOpponentDragonMoveNWToMageCanMove() {
        dragonMoveLocationChecking(0, 4, 1, 3);
    }

    @Test
    public void testAttackOpponentDragonMoveNWToMageCannotMove() {
        dragonMoveLocationChecking(1, 3, 2, 2);
    }

    @Test
    public void testAttackOpponentDragonMoveNToMageCanMove() {
        dragonMoveLocationChecking(2, 4, 2, 3);
    }

    @Test
    public void testAttackOpponentDragonMoveNToMageCannotMove() {
        dragonMoveLocationChecking(2, 3, 2, 2);
    }

    @Test
    public void testAttackOpponentDragonMoveNEToMageCanMove() {
        dragonMoveLocationChecking(4, 4, 3, 3);
    }

    @Test
    public void testAttackOpponentDragonMoveNEToMageCannotMove() {
        dragonMoveLocationChecking(3, 3, 2, 2);
    }

    @Test
    public void testAttackOpponentDragonMoveEToMageCanMove() {
        dragonMoveLocationChecking(4, 2, 3, 2);
    }

    @Test
    public void testAttackOpponentDragonMoveEToMageCannotMove() {
        dragonMoveLocationChecking(3, 2, 2, 2);
    }

    @Test
    public void testAttackOpponentDragonMoveSEToMageCanMove() {
        dragonMoveLocationChecking(4, 0, 3, 1);
    }

    @Test
    public void testAttackOpponentDragonMoveSEToMageCannotMove() {
        dragonMoveLocationChecking(3, 1, 2, 2);
    }

    @Test
    public void testAttackOpponentDragonMoveSToMageCanMove() {
        dragonMoveLocationChecking(2, 0, 2, 1);
    }

    @Test
    public void testAttackOpponentDragonMoveSToMageCannotMove() {
        dragonMoveLocationChecking(2, 1, 2, 2);
    }

    @Test
    public void testResetLifeFormForGame() {
        assertEquals(Dragon.DRAGON_INITIAL_HP, dragonB.hp);
        assertEquals(0, dragonB.attackSequencePointer);
    }

    // EFFECTS: makes the dragon try to walk towards the mage and checks for expected behaviour.
    // Note: this method was refactored out to avoid repetition.
    private void dragonMoveLocationChecking(int mageX, int mageY, int dragonEndX, int dragonEndY) {
        mage.setGameDistrict(gameDistrictB);
        dragonB.setOpponent(mage);
        dragonB.nextAttackSequencePos();
        assertEquals(1, dragonB.attackSequencePointer);
        dragonB.setLocation(new Point(2, 2));
        mage.setLocation(new Point(mageX, mageY));
        dragonB.attackOpponent();
        assertEquals(dragonB.currentTile, new Point(dragonEndX, dragonEndY));
        assertEquals(Mage.MAGE_MAX_HP, mage.getHP());
        assertEquals(Dragon.DRAGON_INITIAL_HP, dragonB.getHP());
    }
}
