package ui;

import model.Dragon;
import model.EndGameOutcome;
import model.GameDistrict;
import model.Mage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

// This class describes functionality that the game trackers, which keep track of in-game activities and states, have.
public abstract class InGameEventsTracker {
    // Constants - related to swing timer
    public static final int TIME_LIMIT_IN_MILLISECONDS = 84000;
    protected static final int TIMER_DELAY_IN_MS = 250;

    protected static final ArrayList<Character> VALID_MOVE_KEYS
            = new ArrayList<>(Arrays.asList('w', 'q', 'a', 'z', 'x', 'c', 'd', 'e'));
    protected static final ArrayList<Character> VALID_FLAG_KEYS
            = new ArrayList<>(Arrays.asList('W', 'Q', 'A', 'Z', 'X', 'C', 'D', 'E'));


    protected Timer timer;

    protected GameDistrict gd;
    protected Mage mage;
    protected Dragon dragon;
    protected boolean inGame;
    protected int timeLeftInGame = TIME_LIMIT_IN_MILLISECONDS;

    // EFFECTS: constructor for this class which records down the objects that this tracker keeps track of
    public InGameEventsTracker(GameDistrict gd, Mage mage, Dragon dragon) {
        this.gd = gd;
        this.mage = mage;
        this.dragon = dragon;
        inGame = false;
    }

    // MODIFIES: this
    // EFFECTS: initializes a swing timer that begins counting down
    public void startTimer() {
        timer = new Timer(TIMER_DELAY_IN_MS, this::timerTicked);
        timer.start();
    }

    // REQUIRES: timer was fired
    // MODIFIES: this
    // EFFECTS: fires a list of events that need to be completed and/or checked each clock tick
    protected abstract void timerTicked(ActionEvent e);

    // MODIFIES: this
    // EFFECTS: initializes a swing timer that starts counting down, then makes the game start.
    public void playDistrict() {
        inGame = true;
        startTimer();
    }

    // EFFECTS: calls the necessary events when it is time for the dragon to attack
    protected abstract void firePerDragonAtkIntervalEvents();

    // EFFECTS: renders the game board
    protected abstract void renderBoard();

    // MODIFIES: this
    // EFFECTS: stops the timer and displays out the appropriate message based on how the game ends.
    public void endGame(EndGameOutcome outcome) {
        gd.endGame(outcome);
        timer.stop();
    }
}
