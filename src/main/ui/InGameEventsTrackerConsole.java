package ui;

import model.*;

import java.awt.event.ActionEvent;
import java.util.Scanner;

import static ui.tools.TimeFormatter.formatMSAsTime;

// This class tracks the ongoing events, mutating objects, and in-game terminal input while the user is in a game.
public class InGameEventsTrackerConsole extends InGameEventsTracker {
    private static final int INTERVAL_BETWEEN_MAGE_MOVEMENTS = 1000;

    private Scanner scanner;


    // MODIFIES: this
    // EFFECTS: initiates a new tracker with the given life forms and game state.
    public InGameEventsTrackerConsole(GameDistrict gd, Mage mage, Dragon dragon) {
        super(gd, mage, dragon);
    }


    // MODIFIES: this
    // EFFECTS: initializes a swing timer that starts counting down, then makes the game start.
    @Override
    public void playDistrict() {
        scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        super.playDistrict();
        renderBoard();
        while (inGame) {
            try {
                Thread.sleep(INTERVAL_BETWEEN_MAGE_MOVEMENTS);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    // REQUIRES: timer was fired
    // MODIFIES: this
    // EFFECTS: fires a list of events that need to be completed each clock tick
    @Override
    protected void timerTicked(ActionEvent e) {
        timeLeftInGame -= TIMER_DELAY_IN_MS;
        if (timeLeftInGame % INTERVAL_BETWEEN_MAGE_MOVEMENTS == 0 && inGame) {
            processMageMovements();
            if (timeLeftInGame % Dragon.INTERVAL_BETWEEN_DRAGON_ATTACKS == 0) {
                firePerDragonAtkIntervalEvents();
            }
            if (inGame) {
                renderBoard();
            }
        }
        if (timeLeftInGame <= 0) {
            endGame(EndGameOutcome.NO_TIME_LOSE);
        }
    }

    // MODIFIES: this.mage
    // EFFECTS: Only allows the mage to either block or move/attack/flag, then modifies
    //    their skill points and draws the game board in the terminal.
    // Note: the mage can only attack once per turn
    public void processMageMovements() {
        String inputString = scanner.next();
        if (inputString.indexOf('b') != -1) {
            mage.tryToUseSkill(true);
        } else {
            mage.tryToUseSkill(false);
            boolean mageAttackedThisTurn = false;
            for (char input : inputString.toCharArray()) {
                if (VALID_MOVE_KEYS.contains(input)) {
                    Direction d = Direction.values()[VALID_MOVE_KEYS.indexOf(input)];
                    mage.move(d);
                } else if (!mageAttackedThisTurn && input == 'f') {
                    mage.attackOpponent();
                    mageAttackedThisTurn = true;
                } else if (VALID_FLAG_KEYS.contains(input)) {
                    mage.flagTile(Direction.values()[VALID_FLAG_KEYS.indexOf(input)]);
                }
            }
        }
        mage.modifyMageSkillPoints();
    }

    // EFFECTS: calls the necessary events when it is time for the dragon to attack
    @Override
    protected void firePerDragonAtkIntervalEvents() {
        dragon.attackOpponent();
    }

    // EFFECTS: renders the game board, with mines denoted by their knowledge states
    protected void renderBoard() {
        StringBuilder boardString = new StringBuilder();
        GameTile[][] board = gd.board;
        for (GameTile[] row : board) {
            StringBuilder stringRow = new StringBuilder("\n|");
            for (GameTile tile : row) {
                stringRow.append(renderTile(tile));
            }
            stringRow.append("\n________________________________________________");
            boardString.insert(0, stringRow);
        }
        System.out.println(boardString);
        renderInfoBar();
    }

    // EFFECTS: renders a single tile in a game board
    //    If dragon will do damage on that tile for its next move, mark the tile with "*"
    //    Mark the tile as "?" for unknown identity; if identity is known, the number represents its nearby mines count
    //    If the tile is flagged as safe, mark the tile with "F"
    //    If the tile is occupied with a life form, use "P" to mark that form as a player, and use "D" to mark as dragon
    //          "B" represents the mage using their block skill
    //    Mark the end of that tile with "|"
    private StringBuilder renderTile(GameTile tile) {
        StringBuilder tileString = new StringBuilder();
        if (tile.distance(dragon.getCurrentTile()) <= dragon.determineRange()) {
            tileString.append("*");
        } else {
            tileString.append(" ");
        }
        tileString.append(tile.getUIIdentity());
        if (tile.getIsFlagged()) {
            tileString.append("F");
        } else {
            tileString.append(" ");
        }
        if (tile.getOccupiedByLifeForm()) {
            tileString.append(determineLifeFormSymbol(tile));
        } else {
            tileString.append(" ");
        }
        tileString.append(" |");
        return tileString;
    }

    // EFFECTS: determines which life form symbol to add to the renderTile string.
    private String determineLifeFormSymbol(GameTile tile) {
        if (tile.equals(mage.getCurrentTile())) {
            if (mage.getSkillInUse()) {
                return "B";
            } else {
                return "P";
            }
        } else {
            return "D";
        }
    }

    // EFFECTS: renders an information bar that describes game state.
    private void renderInfoBar() {
        System.out.println("Time Left: " + formatMSAsTime(timeLeftInGame));
        System.out.println("Num Flags Left: " + mage.getNumFlags());
        System.out.println("SP bar: " + mage.getSkillPoints() + "/" + Mage.MAX_SKILL_POINTS);
        System.out.println("Hit Combo: " + mage.getHitCombo());
        System.out.println("Mage hp: " + mage.getHP() + "/" + Mage.MAGE_MAX_HP);
        System.out.println("Dragon hp: " + dragon.getHP() + "/" + dragon.getConstantDragonMaxHp());
    }

    // MODIFIES: this
    // EFFECTS: stops the timer and prints out the appropriate message based on how the game ends.
    @Override
    public void endGame(EndGameOutcome outcome) {
        super.endGame(outcome);
        renderBoard();
        System.out.println("\n=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n");
        switch (outcome) {
            case NO_DRAGON_HP_WIN:
                System.out.println("WIN: Good job, you have bound the dragon!");
                break;
            case STEPPED_IN_MINE_LOSE:
                System.out.println("LOSE: You detonated a mine.");
                break;
            case NO_MAGE_HP_LOSE:
                System.out.println("LOSE: You are out of health points.");
                break;
            case NO_TIME_LOSE:
                System.out.println("LOSE: You ran out of time.");
                break;
            default:
                System.out.println("Game aborted.");
        }
        wrapUpLastGame();
    }

    // MODIFIES: this' GameDistrict
    // EFFECTS: prints out end game statistics and saves the best clear time
    private void wrapUpLastGame() {
        inGame = false;
        EndGameOutcome gameOutcome = gd.getGameOutcome();
        if (gameOutcome.equals(EndGameOutcome.NO_DRAGON_HP_WIN)) {
            int timeTookToClear = TIME_LIMIT_IN_MILLISECONDS - timeLeftInGame;
            System.out.println("Clear time: " + formatMSAsTime(timeTookToClear));
            if (gd.bestClearTimeInMS == -1 || gd.bestClearTimeInMS > timeTookToClear) {
                gd.bestClearTimeInMS = timeTookToClear;
            }
        } else {
            String loseCause = gameOutcome.toString();
            System.out.println("No clear: lose caused by " + loseCause.substring(0, loseCause.length() - 5));
        }
        System.out.print("Best Clear Time: ");
        if (gd.bestClearTimeInMS == -1) {
            System.out.println("--:--");
        } else {
            System.out.println(formatMSAsTime(gd.bestClearTimeInMS));
        }
    }
}
