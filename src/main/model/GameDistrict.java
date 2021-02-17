package model;

import exceptions.InvalidInputException;
import ui.GameWorld;
import ui.InGameEventsTracker;

import java.awt.*;
import java.util.Random;

// INVARIANT: for every Difficulty level, the amount of mines < BOARD_EDGE_LENGTH ^ 2
// A "map" in this game. Contains a grid of mine-storing tiles that life forms can walk on.
public class GameDistrict {
    public static final int BOARD_EDGE_LENGTH = 8;

    private String districtName;
    private Difficulty difficultyLevel;
    private Mage mage;
    private Dragon dragon;
    private GameWorld gameWorld;
    private boolean inGame = false;
    private EndGameOutcome gameOutcome = EndGameOutcome.STILL_PLAYING_GAME;
    private Point[] mineCoords;
    private InGameEventsTracker tracker;
    public GameTile[][] board = new GameTile[BOARD_EDGE_LENGTH][BOARD_EDGE_LENGTH]; // stores rows of game tiles
    private Point mageInitialLocation;
    private Point dragonInitialLocation;
    public int bestClearTimeInMS = -1; // -1 represents "never cleared before"; ceiling to nearest timerTicked interval

    // MODIFIES: this
    // EFFECTS: creates a new game map (district) with the given parameters.
    public GameDistrict(DragonType dt, Difficulty difficulty, GameWorld gw) {
        dragon = Dragon.initDragon(dt, this);
        initDistrictAsWorldPart(gw);
        difficultyLevel = difficulty;
        districtName = nameDistrict();
        setMineCoordsAndLifeFormLocations();
    }

    // MODIFIES: this
    // EFFECTS: tries to recreate a new game district based off a saved JSON district, throws exception if unsuccessful
    public GameDistrict(String districtName, Point[] mineCoords, Point mageInitLocation, Point dragonInitialLocation,
                        int bestClearTimeInMS, GameWorld gw) throws InvalidInputException {
        for (DragonType dt : DragonType.values()) {
            if (dt.toString().charAt(0) == districtName.charAt(9)) {
                dragon = Dragon.initDragon(dt, this);
                break;
            }
        }
        for (Difficulty d : Difficulty.values()) {
            if (d.toString().charAt(0) == districtName.charAt(10)) {
                difficultyLevel = d;
                break;
            }
        }
        if (difficultyLevel == null || dragon == null) {
            throw new InvalidInputException();
        }
        initDistrictAsWorldPart(gw);
        this.districtName = districtName;
        this.mineCoords = mineCoords;
        this.mageInitialLocation = mageInitLocation;
        this.dragonInitialLocation = dragonInitialLocation;
        this.bestClearTimeInMS = bestClearTimeInMS;
    }

    // MODIFIES: this.mage, this.dragon, this
    // EFFECTS: sets the dragon as the mage's opponent and records the world and mage that this dragon is in / against
    private void initDistrictAsWorldPart(GameWorld gw) {
        this.gameWorld = gw;
        mage = gameWorld.getMage();
        dragon.setOpponent(mage);
    }

    // MODIFIES: this
    // EFFECTS: names the current district via its dragon, difficulty level, and how many districts were made before it
    //          Note that the first district can be named "District BE-0", where
    //               'B' stands for Blaze, the name of the fire dragon
    //               'E' stands for Easy, the difficulty level of the minesweeper aspect of the district
    //               '0' means there were no districts made before this one in the current world.
    private String nameDistrict() {
        return "District " + dragon.getDragonType().toString().charAt(0) + difficultyLevel.toString().charAt(0) + "-"
                + gameWorld.getNumDistrictsMade();
    }

    // MODIFIES: this
    // EFFECTS: creates a 8x8 grid of GameTiles and stores it as the game board for this,
    //    sets some tiles as mines, then initializes each tile, setting 2 tiles for the life forms to be on.
    public void initializeGameBoard() {
        for (int tileAtXPos = 0; tileAtXPos < BOARD_EDGE_LENGTH; tileAtXPos++) {
            for (int tilesRowAtYPos = 0; tilesRowAtYPos < BOARD_EDGE_LENGTH; tilesRowAtYPos++) {
                board[tilesRowAtYPos][tileAtXPos] = new GameTile(tileAtXPos, tilesRowAtYPos, this);
            }
        }
        for (Point p : mineCoords) {
            board[p.y][p.x].setIsMine();
        }
        for (int tileAtXPos = 0; tileAtXPos < BOARD_EDGE_LENGTH; tileAtXPos++) {
            for (int tilesRowAtYPos = 0; tilesRowAtYPos < BOARD_EDGE_LENGTH; tilesRowAtYPos++) {
                board[tilesRowAtYPos][tileAtXPos].initializeTileInGrid();
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: selects mines and player/dragon locations for the district
    public void setMineCoordsAndLifeFormLocations() {
        mineCoords = selectMines();
        mageInitialLocation = generateUniquePoint(mineCoords);
        dragonInitialLocation = generateUniquePoint(mineCoords, mageInitialLocation);
    }

    // MODIFIES: this
    // EFFECTS: records the number of mines to be defused in this map based on the difficulty level, then
    //    returns that amount of randomly generated mine location coordinates
    private Point[] selectMines() {
        mineCoords = new Point[difficultyLevel.numMines];
        for (int i = 0; i < difficultyLevel.numMines; i++) {
            mineCoords[i] = generateUniquePoint(mineCoords);
        }
        return mineCoords;
    }

    // Note: the following method is documented with comments b/c it is convoluted; please read comments for details
    // REQUIRES: the list passed in has less unique elements than the largest board size
    // EFFECTS: generates a random coordinate in a game district that is unique from the points in the provided list
    protected Point generateUniquePoint(Point[] pointList) {
        Point possiblePt = null;
        Random generateRandomInts = new Random();
        boolean b = false; // Note: the uniqueness of the point unverified right now, so the while loop is needed
        while (!b) {
            // b is false (it is named 'b' because its value keeps changing and there is no word to describe its role)
            // generate new possibly unique point
            possiblePt = new Point(generateRandomInts.nextInt(GameDistrict.BOARD_EDGE_LENGTH),
                    generateRandomInts.nextInt(GameDistrict.BOARD_EDGE_LENGTH));
            // check the possible point against all points in the given list to ensure uniqueness
            for (Point pt : pointList) {
                // if the two points are equal, set b as true, else keep b as false
                b = b || possiblePt.equals(pt);
            }
            // If b is true, set b to false so loop reruns; else, b is false so set b to true to exit the loop
            b = !b;
        }
        return possiblePt;
    }

    // REQUIRES: the list + point passed in has less unique elements than the largest board size
    // EFFECTS: generates a random coordinate in a game district that is
    //    unique from both the points in the list & the single point
    protected Point generateUniquePoint(Point[] pointList, Point point) {
        Point possiblePt = null;
        Random generateRandomInts = new Random();
        boolean b = false;
        while (!b) {
            possiblePt = new Point(generateRandomInts.nextInt(GameDistrict.BOARD_EDGE_LENGTH),
                    generateRandomInts.nextInt(GameDistrict.BOARD_EDGE_LENGTH));
            for (Point pt : pointList) {
                b = b || possiblePt.equals(pt);
            }
            // generates a new point if the current point is equal to the singleton invalid point
            b = possiblePt.equals(point) || b;
            b = !b;
        }
        return possiblePt;
    }

    // EFFECTS: gets the tile that results from moving 1 unit in a certain direction away from the given x, y position
    public GameTile tileInDirection(int x, int y, Direction d) {
        return getGameTileAtPos(x + d.shiftX, y + d.shiftY);
    }

    // MODIFIES: this, this.mage, this.tracker
    // EFFECTS: starts the game (sets the opponents against each other, resets the life forms and the district)
    public void setupGame(InGameEventsTracker tracker) {
        gameOutcome = EndGameOutcome.STILL_PLAYING_GAME;
        mage.setOpponent(dragon);
        mage.setGameDistrict(this);
        mage.setNumFlags(difficultyLevel.numMines);
        resetDistrictAndLifeForms();
        board[mageInitialLocation.y][mageInitialLocation.x].revealNearbyTiles();
        inGame = true;
        this.tracker = tracker;
    }

    // MODIFIES: the dragon, mage, and board associated with this.
    // EFFECTS: resets the game district's tiles and life forms
    public void resetDistrictAndLifeForms() {
        initializeGameBoard();
        for (GameTile[] row : board) {
            for (GameTile tile : row) {
                tile.setUserKnowsIdentity(false);
                tile.setIsFlagged(false);
                tile.setOccupiedByLifeForm(false);
                tile.setUIIdentityToQuestionMark();
            }
        }

        mage.resetLifeFormForGame();
        dragon.resetLifeFormForGame();
        mage.setLocation(mageInitialLocation);
        dragon.setLocation(dragonInitialLocation);
    }

    // MODIFIES: this
    // EFFECTS: stops the swing timer if it's still running, then displays statistics of the game that was just played
    public void endGame(EndGameOutcome outcome) {
        gameOutcome = outcome;
        inGame = false;
        mage.setOpponent(null);
    }

    // EFFECTS: gets the tile at the specified 0-indexed x and y position
    public GameTile getGameTileAtPos(int x, int y) {
        if (x >= BOARD_EDGE_LENGTH || y >= BOARD_EDGE_LENGTH || x < 0 || y < 0) {
            return null;
        } else {
            return board[y][x];
        }
    }

    // getters
    public String getDistrictName() {
        return districtName;
    }

    public boolean isInGame() {
        return inGame;
    }

    public Difficulty getDifficultyLevel() {
        return difficultyLevel;
    }

    public EndGameOutcome getGameOutcome() {
        return gameOutcome;
    }

    public Dragon getDragon() {
        return dragon;
    }

    public Point[] getMineCoords() {
        return mineCoords;
    }

    public Point getMageInitialLocation() {
        return mageInitialLocation;
    }

    public Point getDragonInitialLocation() {
        return dragonInitialLocation;
    }

    public int getBestClearTimeInMS() {
        return bestClearTimeInMS;
    }

    public InGameEventsTracker getTracker() {
        return tracker;
    }
}
