package model;

// The abstract class for the dragons used in this project; specifies general dragon behaviour and methods.
public abstract class Dragon extends LifeForm {
    public static final int INTERVAL_BETWEEN_DRAGON_ATTACKS = 1000; // NOTE: this must be a multiple of 1000
    protected static final int DRAGON_INITIAL_HP = 1000;
    protected static final int DRAGON_ATK = 50;

    protected DragonType dragonType;
    protected DragonAction[] attackSequence;
    protected int attackSequencePointer = 0;

    // EFFECTS: initiates a dragon of the given type with the given HP and ATK and returns it
    public static Dragon initDragon(DragonType dt, GameDistrict gd) {
        Dragon d;
        switch (dt) {
            case BLAZE:
                d = new DragonBlaze();
                break;
            case TSUNAMI:
                d = new DragonTsunami();
                break;
            default:
                d = new DragonDownburst();
                break;
        }
        d.hp = DRAGON_INITIAL_HP;
        d.atk = DRAGON_ATK;
        d.gameDistrict = gd;
        return d;
    }

    // EFFECTS: puts the game in a win state because the dragon is out of HP.
    @Override
    protected void outOfHP() {
        gameDistrict.getTracker().endGame(EndGameOutcome.NO_DRAGON_HP_WIN);
    }

    // EFFECTS: calculates how much damage the dragon's attack does to the user
    @Override
    protected int attackFormula() {
        return atk * incomingDragonAttack().damageModifier;
    }

    // EFFECTS: returns the type of attack that the dragon is about to do / currently doing
    //    and advances the attack marker by one position in the loop
    protected DragonAction incomingDragonAttack() {
        return attackSequence[attackSequencePointer];
    }

    // MODIFIES: this
    // EFFECTS: returns the range of the dragon's upcoming attack attack
    @Override
    public double determineRange() {
        return incomingDragonAttack().atkRange;
    }

    @Override
    // MODIFIES: this.opponent
    // EFFECTS: deals damage to the opponent if they are in range; always advances to next attack sequence position
    //    note: if the upcoming move is MOVE_TO_MAGE, attempts to move toward mage instead of attacks them.
    public boolean attackOpponent() {
        boolean attackedMage = false;
        if (incomingDragonAttack().equals(DragonAction.MOVE_TO_MAGE)) {
            double twoPi = Math.PI * 2;
            double relativeDirection = Math.atan2(opponent.currentTile.y - this.currentTile.y,
                    opponent.currentTile.x - this.currentTile.x) - Math.PI / 2;
            if (relativeDirection < 0) {
                relativeDirection += twoPi;
            }
            Direction dirToMove = Direction.values()[(int) (relativeDirection / twoPi * Direction.values().length)];
            move(dirToMove);
        } else if (opponentIsInRange(determineRange())) {
            opponent.takeDamage(attackFormula());
            attackedMage = true;
        }
        nextAttackSequencePos();
        return attackedMage;
    }

    // MODIFIES: this
    // EFFECTS: gets the position of the next attack in this dragon's attack sequence
    protected void nextAttackSequencePos() {
        if (attackSequencePointer < attackSequence.length - 1) {
            attackSequencePointer += 1;
        } else {
            attackSequencePointer = 0;
        }
    }

    // MODIFIES: this
    // EFFECTS: resets the life form so they are ready for the game
    @Override
    public void resetLifeFormForGame() {
        hp = DRAGON_INITIAL_HP;
        attackSequencePointer = 0;
    }

    // getters
    public int getConstantDragonMaxHp() {
        return DRAGON_INITIAL_HP;
    }

    public int getConstantDragonAtk() {
        return DRAGON_ATK;
    }

    public int getVarDragonAtk() {
        return this.atk;
    }

    public int getVarDragonHP() {
        return this.hp;
    }

    public DragonType getDragonType() {
        return this.dragonType;
    }
}
