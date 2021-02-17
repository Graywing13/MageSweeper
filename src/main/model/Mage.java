package model;

// The hero that a user plays as. Attacks dragons and is a life form.
public class Mage extends LifeForm {
    public static final int MAGE_INITIAL_SKILL_POINTS = 0;
    public static final int SKILL_INACTIVE_POINTS_GAIN_PER_SECOND = 1;
    public static final int SKILL_COST_IN_POINTS_PER_SECOND = 2;
    public static final int MAX_SKILL_POINTS = 20;
    public static final int MAGE_MAX_HP = 1000;
    private static final double MAGE_ATK_RANGE = 2.3;                   // ceiling of sqrt(5) to one decimal place
    private static final int MAGE_ATK = 50;

    private final String mageName;
    private int hitCombo;
    private int skillPoints;
    private boolean skillInUse;
    private int numFlags;

    // EFFECTS: creates a new Mage with the given name that has 0 skill points and hit combo
    public Mage(String name) {
        mageName = name;
        hitCombo = 0;
        hp = MAGE_MAX_HP;
        atk = MAGE_ATK;
        skillInUse = false;
    }

    // REQUIRES: mage is on this
    // MODIFIES: this
    // EFFECTS: if the tile in a given direction exists and has an unknown identity, toggles whether it is flagged
    //    else does nothing.
    public void flagTile(Direction d) {
        GameTile targetTile = gameDistrict.tileInDirection(currentTile.getTileXPos(), currentTile.getTileYPos(), d);
        if (null != targetTile && !targetTile.getUserKnowsIdentity()) {
            if (targetTile.getIsFlagged()) {
                targetTile.setIsFlagged(false);
                numFlags++;
            } else if (numFlags > 0) {
                targetTile.setIsFlagged(true);
                numFlags--;
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: resets the life form so they are ready for the game
    @Override
    public void resetLifeFormForGame() {
        hp = MAGE_MAX_HP;
        hitCombo = 0;
        tryToUseSkill(false);
        skillPoints = MAGE_INITIAL_SKILL_POINTS;
    }

    // EFFECTS: puts the game in a lose state because the mage is out of health points
    @Override
    protected void outOfHP() {
        gameDistrict.getTracker().endGame(EndGameOutcome.NO_MAGE_HP_LOSE);
    }

    // EFFECTS: gets the mage's attack based on its hitCombo.
    @Override
    protected int attackFormula() {
        return (int) (atk * (1 + hitCombo * 0.01));
    }

    // MODIFIES: this.opponent
    // EFFECTS: deals damage to the opponent if they are in range; else does nothing
    @Override
    public boolean attackOpponent() {
        if (opponentIsInRange(determineRange())) {
            hitCombo++;
            opponent.takeDamage(attackFormula());
            return true;
        }
        return false;
    }

    // MODIFIES: this
    // EFFECTS: moves this in the specified direction, then reveals any tiles that need to be revealed
    @Override
    public void move(Direction d) {
        super.move(d);
        currentTile.revealNearbyTiles();
    }

    // MODIFIES: this
    // EFFECTS: if skill is in use, does not take damage; else reduces hp by the amount of damage specified.
    @Override
    protected void takeDamage(int dmgToTake) {
        if (!skillInUse) {
            super.takeDamage(dmgToTake);
        }
    }

    // MODIFIES: this
    // EFFECTS: returns the range of the mage's attack
    @Override
    public double determineRange() {
        return MAGE_ATK_RANGE;
    }

    // REQUIRES: skillPoints - SKILL_COST_POINTS_IN_SECONDS >= 0 in order for skillInUse to be true.
    // MODIFIES: this
    // EFFECTS: increase skillPoints by a constant if skill is inactive; else decreases skillPoints by another constant
    //    note that the amount of skill points will not drop below 0 or go above the maximum skill points allowed.
    public void modifyMageSkillPoints() {
        if (skillInUse) {
            skillPoints = skillPoints - SKILL_COST_IN_POINTS_PER_SECOND;
        } else {
            skillPoints = Math.min(MAX_SKILL_POINTS, skillPoints + SKILL_INACTIVE_POINTS_GAIN_PER_SECOND);
        }
    }

    // MODIFIES: this
    // EFFECTS: sets whether the mage is using skill; can only be true if the mage has enough skill points to use skill
    public boolean tryToUseSkill(boolean usingSkill) {
        skillInUse = usingSkill && skillPoints >= SKILL_COST_IN_POINTS_PER_SECOND;
        return skillInUse;
    }

    // getters
    public int getNumFlags() {
        return numFlags;
    }

    public String getMageName() {
        return mageName;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public boolean getSkillInUse() {
        return skillInUse;
    }

    public int getHitCombo() {
        return hitCombo;
    }

    // setters
    public void setNumFlags(int numFlags) {
        this.numFlags = numFlags;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }
}
