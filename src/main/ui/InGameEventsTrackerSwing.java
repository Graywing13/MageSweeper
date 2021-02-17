package ui;

import model.*;
import ui.tools.SoundPlayer;
import ui.utilities.JLabelCustom;
import ui.utilities.JPanelImageBackground;
import ui.utilities.LabelType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static model.GameDistrict.BOARD_EDGE_LENGTH;
import static ui.MagesweeperAppSwing.FRAME_HEIGHT;
import static ui.MagesweeperAppSwing.FRAME_WIDTH;
import static ui.tools.TimeFormatter.formatMSAsTime;

// Note: yes, this frame uses null layout which is bad practice... but I'm really tight on time so this is faster.
// This class tracks the ongoing events, mutating objects, and in-game terminal input while the user is in a game.
public class InGameEventsTrackerSwing extends InGameEventsTracker implements KeyListener {
    // Constants - board setup
    private static final int MAGE_HP_BAR_HEIGHT = 16;
    private static final int MAGE_HP_BAR_FULL_WIDTH = 240;
    private static final int MAGE_PROFILE_PICTURE_EDGE_LENGTH = 120;
    private static final int MAGE_SKILL_PICTURE_EDGE_LENGTH = 64;
    private static final int GAME_TILE_SIZE = 50;
    private static final int DRAGON_MAX_HP_BAR_WIDTH = 500;
    private static final int DRAGON_HP_BAR_HEIGHT = 20;
    private static final int MAGE_SKILL_LENGTH_MS = 1000;
    private static final int MAGE_ATK_LENGTH_MS = 500;
    private static final int END_GAME_OVERLAY_TOPMOST_Y = 250;
    private static final int TILE_SEPARATION = 64;
    private static final int LEFTMOST_TILE_X = 434;
    private static final int BOTTOMMOST_TILE_Y = 648;
    private static final int DRAGON_HP_BAR_X = 460;
    private static final int DRAGON_HP_BAR_Y = 60;
    private static final int DRAGON_PIC_EDGE_LENGTH = 100;
    private static final int DRAGON_PIC_X = 400;
    private static final int DRAGON_PIC_Y = 13;
    private static final int MAGE_HP_BAR_AND_PFP_X = 48;
    private static final int MAGE_HP_BAR_Y = 682;
    private static final int MAGE_PFP_AND_FLAG_BOX_Y = 562;

    // Constants - used during game play
    private static final String IN_GAME_FOLDER_PATH = MagesweeperAppSwing.IMG_FOLDER_PATH + "inGame/";
    private static final ArrayList<Character> VALID_MOVE_KEYS
            = new ArrayList<>(Arrays.asList('w', 'q', 'a', 'z', 'x', 'c', 'd', 'e'));
    private static final ArrayList<Character> VALID_FLAG_KEYS
            = new ArrayList<>(Arrays.asList('W', 'Q', 'A', 'Z', 'X', 'C', 'D', 'E'));

    // Tools
    private SoundPlayer soundPlayer;

    // General game global variables
    private JFrame frame;
    long mageAtkCooledDown;
    private JPanel[] flags;
    private JLabel[][] gameImgTiles = new JLabel[BOARD_EDGE_LENGTH][BOARD_EDGE_LENGTH];

    // more specific / local game variables
    long mageSkillCooledDown;
    private JPanel gameBoardContainer;
    private volatile boolean mageWasHit;
    private JLabel[][] gameImgTileOverlays = new JLabel[BOARD_EDGE_LENGTH][BOARD_EDGE_LENGTH];

    // Game visuals with changing locations / sizes
    private JPanel blockMarker;
    private JPanel honeMarker;
    private JPanel dragonMarker;
    private JPanel playerMarker;
    private JLabel timerLabel;
    private JLabel hitComboLabel;
    private JPanel mageSkillGaugeOverlay;
    private JPanel mageHealthBarColored;
    private JPanel dragonHealthBarColored;
    private JPanel endGameOverlayPanel;
    private JLabel declareWinOrLose;
    private JLabel clearTime;
    private JLabel bestClearTime;
    private JPanel mageProfilePicPain;
    private long mageHitTimer;


    // MODIFIES: this
    // EFFECTS: initiates a new tracker with the given life forms and game state.
    public InGameEventsTrackerSwing(GameDistrict gd, Mage mage, Dragon dragon) {
        super(gd, mage, dragon);
        this.soundPlayer = SoundPlayer.getSoundPlayer();
        initFrame();
    }

    // MODIFIES: this
    // EFFECTS: initializes a JFrame that will house the current game
    private void initFrame() {
        frame = new JFrame(gd.getDistrictName());
        try {
            frame.setIconImage(ImageIO.read(new File(MagesweeperAppSwing.IMG_FOLDER_PATH + "logo.png")));
        } catch (IOException e) {
            System.out.println("Could not find logo path.");
        }
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setVisible(true);
        frame.setFocusable(true);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (inGame) {
                    endGame(EndGameOutcome.STILL_PLAYING_GAME);
                }
                frame.setVisible(false);
                frame.dispose();
            }
        });
        frame.addKeyListener(this);
        frame.setFocusable(true);
        frame.requestFocus();
    }

    // MODIFIES: this
    // EFFECTS: initializes a swing timer that starts counting down, then makes the game start.
    @Override
    public void playDistrict() {
        setupBoardFrameAndBackground();
        soundPlayer.playOnboard();
        soundPlayer.playInGameBGM();
        super.playDistrict();
    }

    // MODIFIES: this, this.frame
    // EFFECTS: initializes the playing board's frame
    private void setupBoardFrameAndBackground() {
        frame.setVisible(false);

        gameBoardContainer = new JPanelImageBackground(MagesweeperAppSwing.IMG_FOLDER_PATH + "backgrounds/gameBkg.png");
        gameBoardContainer.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        gameBoardContainer.setLayout(null);

        setupEndGameOverlay();
        setupGameMarkers();
        setupGameTiles();
        setupDragonInfo();
        setupMageInfo();
        setupFlagBox();

        frame.getContentPane().removeAll();
        frame.getContentPane().add(gameBoardContainer);
        frame.setVisible(true);

        renderBoard();
    }

    // MODIFIES: this
    // EFFECTS: adds the end game overlay onto the game board
    private void setupEndGameOverlay() {
        endGameOverlayPanel = new JPanel();
        endGameOverlayPanel.setOpaque(true);
        endGameOverlayPanel.setBackground(ColorTheme.TRANSLUCENT_DARK_BLACK.color);
        endGameOverlayPanel.setLayout(new BoxLayout(endGameOverlayPanel, BoxLayout.Y_AXIS));

        endGameOverlayPanel.add(Box.createRigidArea(new Dimension(0, END_GAME_OVERLAY_TOPMOST_Y)));

        declareWinOrLose = new JLabelCustom("", LabelType.IN_GAME_BIG_BLACK);
        declareWinOrLose.setForeground(ColorTheme.WHITE.color);
        endGameOverlayPanel.add(declareWinOrLose);
        declareWinOrLose.setAlignmentX(Component.CENTER_ALIGNMENT);

        make0x50RigidBox(endGameOverlayPanel);
        clearTime = new JLabelCustom("", LabelType.IN_GAME_NORMAL_WHITE);
        endGameOverlayPanel.add(clearTime);
        clearTime.setAlignmentX(Component.CENTER_ALIGNMENT);

        bestClearTime = new JLabelCustom("", LabelType.IN_GAME_NORMAL_WHITE);
        endGameOverlayPanel.add(bestClearTime);
        bestClearTime.setAlignmentX(Component.CENTER_ALIGNMENT);

        make0x50RigidBox(endGameOverlayPanel);
        JLabel exit = new JLabelCustom("You may exit this window to return to the world.", LabelType.SELECTION_ACTION);
        endGameOverlayPanel.add(exit);
        exit.setAlignmentX(Component.CENTER_ALIGNMENT);

        gameBoardContainer.add(endGameOverlayPanel);
        endGameOverlayPanel.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        endGameOverlayPanel.setVisible(false);
    }

    // MODIFIES: this
    // EFFECTS: creates and adds a rigid box of 0 x 50 pixels to the given panel
    private void make0x50RigidBox(JPanel panel) {
        panel.add(Box.createRigidArea(new Dimension(0, 50)));
    }

    // MODIFIES: this
    // EFFECTS: adds the game's markers onto the game board
    private void setupGameMarkers() {
        blockMarker = new JPanelImageBackground(IN_GAME_FOLDER_PATH + "hexagon.png");
        honeMarker = new JPanelImageBackground(IN_GAME_FOLDER_PATH + "honeMarker.png");
        dragonMarker = new JPanelImageBackground(IN_GAME_FOLDER_PATH + "markerd90.png");
        playerMarker = new JPanelImageBackground(IN_GAME_FOLDER_PATH + "markerp90.png");
        blockMarker.setSize(GAME_TILE_SIZE, GAME_TILE_SIZE);
        honeMarker.setSize(GAME_TILE_SIZE, GAME_TILE_SIZE);
        dragonMarker.setSize(GAME_TILE_SIZE, GAME_TILE_SIZE);
        playerMarker.setSize(GAME_TILE_SIZE, GAME_TILE_SIZE);
        blockMarker.setOpaque(false);
        honeMarker.setOpaque(false);
        dragonMarker.setOpaque(false);
        playerMarker.setOpaque(false);
        gameBoardContainer.add(blockMarker);
        gameBoardContainer.add(honeMarker);
        gameBoardContainer.add(dragonMarker);
        gameBoardContainer.add(playerMarker);
        blockMarker.setVisible(false);
        honeMarker.setVisible(false);
    }

    // MODIFIES: this
    // EFFECTS: makes a grid of game tiles and adds them to the game board
    private void setupGameTiles() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                JLabel currentOverlay = new JLabel();
                gameBoardContainer.add(currentOverlay);
                currentOverlay.setBounds(LEFTMOST_TILE_X + TILE_SEPARATION * x,
                        BOTTOMMOST_TILE_Y - TILE_SEPARATION * y, GAME_TILE_SIZE, GAME_TILE_SIZE);
                currentOverlay.setOpaque(true);
                currentOverlay.setBackground(ColorTheme.TRANSPARENT.color);
                gameImgTileOverlays[y][x] = currentOverlay;

                JLabel currentTile = new JLabel();
                gameBoardContainer.add(currentTile);
                currentTile.setBounds(LEFTMOST_TILE_X + TILE_SEPARATION * x, BOTTOMMOST_TILE_Y - TILE_SEPARATION * y,
                        GAME_TILE_SIZE, GAME_TILE_SIZE);
                currentTile.setOpaque(true);
                gameImgTiles[y][x] = currentTile;
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: adds the dragon's info to the game board
    private void setupDragonInfo() {
        JPanel dragonPic = new JPanelImageBackground(IN_GAME_FOLDER_PATH
                + dragon.getDragonType().toString().toLowerCase() + ".png");
        dragonPic.setOpaque(false);
        gameBoardContainer.add(dragonPic);
        dragonPic.setBounds(DRAGON_PIC_X, DRAGON_PIC_Y, DRAGON_PIC_EDGE_LENGTH, DRAGON_PIC_EDGE_LENGTH);

        dragonHealthBarColored = new JPanel();
        dragonHealthBarColored.setBackground(ColorTheme.ORANGE.color);
        gameBoardContainer.add(dragonHealthBarColored);
        dragonHealthBarColored.setBounds(DRAGON_HP_BAR_X, DRAGON_HP_BAR_Y,
                DRAGON_MAX_HP_BAR_WIDTH, DRAGON_HP_BAR_HEIGHT);

        JPanel dragonHealthBarBkg = new JPanel();
        dragonHealthBarBkg.setBackground(ColorTheme.BLACK.color);
        gameBoardContainer.add(dragonHealthBarBkg);
        dragonHealthBarBkg.setBounds(DRAGON_HP_BAR_X, DRAGON_HP_BAR_Y, DRAGON_MAX_HP_BAR_WIDTH, DRAGON_HP_BAR_HEIGHT);
    }

    // Note: this is the only piece of code that changes the below constants so I didn't refactor the constants out.
    // MODIFIES: this
    // EFFECTS: creates a box with flags and a timer.
    private void setupFlagBox() {
        timerLabel = new JLabelCustom(formatMSAsTime(timeLeftInGame), LabelType.IN_GAME_BIG_BLACK);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameBoardContainer.add(timerLabel);
        timerLabel.setBounds(1078, 575, 240, 136);

        int numMines = gd.getDifficultyLevel().numMines;
        flags = new JPanelImageBackground[numMines];
        for (int i = 0; i < numMines; i++) {
            JPanel flag = new JPanelImageBackground(IN_GAME_FOLDER_PATH + "flag.png");
            flag.setOpaque(false);
            gameBoardContainer.add(flag);
            flag.setBounds(1092 + 20 * i, 528, 56, 62);
            flags[i] = flag;
        }

        JPanel flagBoxPic = new JPanelImageBackground(IN_GAME_FOLDER_PATH + "flagBox2.png");
        gameBoardContainer.add(flagBoxPic);
        flagBoxPic.setBounds(1078, MAGE_PFP_AND_FLAG_BOX_Y, 240, 136);
    }


    // MODIFIES: this
    // EFFECTS: creates the mage info panel
    private void setupMageInfo() {
        hitComboLabel = new JLabelCustom("00", LabelType.IN_GAME_BIG_BLACK);
        hitComboLabel.setForeground(ColorTheme.WHITE.color);
        hitComboLabel.setHorizontalAlignment(SwingConstants.LEFT);
        gameBoardContainer.add(hitComboLabel);
        hitComboLabel.setBounds(48, 450, 240, 136);

        addMagePFP();
        JPanel mageSkillGaugePic = new JPanelImageBackground(IN_GAME_FOLDER_PATH + "skillIcon.png");

        JPanel mageHealthBarBkg = new JPanel();
        mageHealthBarBkg.setBackground(ColorTheme.BLACK.color);

        mageHealthBarColored = new JPanel();
        mageHealthBarColored.setBackground(ColorTheme.BLUE.color);

        mageSkillGaugeOverlay = new JPanel();
        mageSkillGaugeOverlay.setOpaque(true);
        mageSkillGaugeOverlay.setBackground(ColorTheme.TRANSLUCENT_BLUE.color);

        gameBoardContainer.add(mageHealthBarColored);
        mageHealthBarColored.setBounds(MAGE_HP_BAR_AND_PFP_X, MAGE_HP_BAR_Y,
                MAGE_HP_BAR_FULL_WIDTH, MAGE_HP_BAR_HEIGHT);
        gameBoardContainer.add(mageSkillGaugeOverlay);

        gameBoardContainer.add(mageSkillGaugePic);
        mageSkillGaugePic.setBounds(196, 590, MAGE_SKILL_PICTURE_EDGE_LENGTH, MAGE_SKILL_PICTURE_EDGE_LENGTH);
        gameBoardContainer.add(mageHealthBarBkg);
        mageHealthBarBkg.setBounds(MAGE_HP_BAR_AND_PFP_X, MAGE_HP_BAR_Y, MAGE_HP_BAR_FULL_WIDTH, MAGE_HP_BAR_HEIGHT);
    }

    // MODIFIES: this
    // EFFECTS: adds the mage's profile pictures to the board
    public void addMagePFP() {
        mageProfilePicPain = new JPanelImageBackground(IN_GAME_FOLDER_PATH + "magePain.png");
        mageProfilePicPain.setBounds(MAGE_HP_BAR_AND_PFP_X, MAGE_PFP_AND_FLAG_BOX_Y, MAGE_PROFILE_PICTURE_EDGE_LENGTH,
                MAGE_PROFILE_PICTURE_EDGE_LENGTH);
        gameBoardContainer.add(mageProfilePicPain);
        mageProfilePicPain.setVisible(false);

        JPanel mageProfilePicNormal = new JPanelImageBackground(IN_GAME_FOLDER_PATH + "mageNormal.png");
        mageProfilePicNormal.setBounds(MAGE_HP_BAR_AND_PFP_X, MAGE_PFP_AND_FLAG_BOX_Y, MAGE_PROFILE_PICTURE_EDGE_LENGTH,
                MAGE_PROFILE_PICTURE_EDGE_LENGTH);
        gameBoardContainer.add(mageProfilePicNormal);
    }

    // EFFECTS: processes the user keyboard input when a key is typed
    @Override
    public void keyTyped(KeyEvent e) {
        if (inGame) {
            processMageMovements(e.getKeyChar());
            renderBoard();
        }
    }

    // EFFECTS: does nothing
    @Override
    public void keyPressed(KeyEvent e) {

    }

    // EFFECTS: does nothing
    @Override
    public void keyReleased(KeyEvent e) {

    }

    // REQUIRES: timer was fired
    // MODIFIES: this
    // EFFECTS: fires a list of events that need to be completed and/or checked each clock tick
    @Override
    protected void timerTicked(ActionEvent e) {
        timeLeftInGame -= TIMER_DELAY_IN_MS;
        if (mageSkillCooledDown >= System.currentTimeMillis()) {
            mage.tryToUseSkill(true);
        } else {
            mage.tryToUseSkill(false);
        }

        if ((timeLeftInGame % MAGE_SKILL_LENGTH_MS == 0) && inGame) {
            mage.modifyMageSkillPoints();
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
    // EFFECTS: decides how the mage should move/block/attack based on user keyboard input
    public void processMageMovements(char input) {
        long currentTime = System.currentTimeMillis();
        if (input == 'b' && !mage.getSkillInUse() && (mageSkillCooledDown < currentTime)) {
            mage.tryToUseSkill(true);
            soundPlayer.playSkill();
            mageSkillCooledDown = currentTime + MAGE_SKILL_LENGTH_MS;
        } else {
            mage.tryToUseSkill(false);
            if (VALID_MOVE_KEYS.contains(input)) {
                Direction d = Direction.values()[VALID_MOVE_KEYS.indexOf(input)];
                mage.move(d);
            } else if (input == 'f' && (mageAtkCooledDown < currentTime)) {
                if (mage.attackOpponent()) {
                    soundPlayer.playHitDragon();
                }
                mageAtkCooledDown = currentTime + MAGE_ATK_LENGTH_MS;
            } else if (VALID_FLAG_KEYS.contains(input)) {
                mage.flagTile(Direction.values()[VALID_FLAG_KEYS.indexOf(input)]);
                soundPlayer.playFlagDown();
            }
        }
    }

    // EFFECTS: calls the necessary events when it is time for the dragon to attack
    @Override
    protected void firePerDragonAtkIntervalEvents() {
        if (dragon.attackOpponent()) {
            mageWasHit = true;
            soundPlayer.playHitByDragon();
            renderBoard();
        }
    }

    // MODIFIES: this, this.frame
    // EFFECTS: updates the playing board
    protected void renderBoard() {
        renderGrid();

        int skillOverlayHeight = Math.max(1, 64 * mage.getSkillPoints() / Mage.MAX_SKILL_POINTS);
        mageSkillGaugeOverlay.setBounds(196, 654 - skillOverlayHeight, 64, skillOverlayHeight);

        dragonHealthBarColored.setSize(500 * dragon.getHP() / dragon.getConstantDragonMaxHp(), 20);

        long currentTime = System.currentTimeMillis();
        if (mageWasHit) {
            mageHealthBarColored.setSize(240 * mage.getHP() / Mage.MAGE_MAX_HP, 16);
            mageProfilePicPain.setVisible(true);
            mageWasHit = false;
            mageHitTimer = currentTime + MAGE_ATK_LENGTH_MS;
        } else if (currentTime > mageHitTimer) {
            mageProfilePicPain.setVisible(false);
        }

        timerLabel.setText(formatMSAsTime(timeLeftInGame));
        hitComboLabel.setText(Integer.toString(mage.getHitCombo()));

        for (int i = 0; i < flags.length; i++) {
            flags[i].setVisible(i < mage.getNumFlags());
        }

        frame.getContentPane().repaint();
    }

    // MODIFIES: this
    // EFFECTS: renders the game tiles and their relevant markers in the right place with the right image
    private void renderGrid() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                GameTile currentTile = gd.board[y][x];
                JLabel currentImgTile = gameImgTiles[y][x];
                JLabel currentImgOverlay = gameImgTileOverlays[y][x];

                char uiIdentity = currentTile.getUIIdentity();
                if (uiIdentity == '?') {
                    uiIdentity = 'q';
                }

                if (currentTile.getIsFlagged()) {
                    currentImgTile.setIcon(new ImageIcon(IN_GAME_FOLDER_PATH + "tilef.png"));
                } else {
                    currentImgTile.setIcon(new ImageIcon(IN_GAME_FOLDER_PATH + "tile" + uiIdentity + ".png"));
                }

                setTileOverlayColor(currentImgOverlay, currentTile);

                if (currentTile.getOccupiedByLifeForm()) {
                    tileOccupiedByLifeFormEvents(currentImgTile, currentTile);
                }
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: changes the tile's color to red if the dragon is going to attack on it the next move
    private void setTileOverlayColor(JLabel currentImgOverlay, GameTile currentTile) {
        if (currentTile.distance(dragon.getCurrentTile()) <= dragon.determineRange()) {
            currentImgOverlay.setBackground(ColorTheme.TRANSLUCENT_RED.color);
        } else {
            currentImgOverlay.setBackground(ColorTheme.TRANSPARENT.color);
        }
    }

    // MODIFIES: this
    // EFFECTS: renders the necessary tile overlays in the right location if a life form is on it
    private void tileOccupiedByLifeFormEvents(JLabel currentImgTile, GameTile currentTile) {
        if (currentTile == mage.getCurrentTile()) {
            playerMarker.setLocation(currentImgTile.getX(), currentImgTile.getY());
            if (mage.getSkillInUse()) {
                blockMarker.setLocation(currentImgTile.getX(), currentImgTile.getY());
                blockMarker.setVisible(true);
            } else {
                blockMarker.setVisible(false);
            }
        } else {
            dragonMarker.setLocation(currentImgTile.getX(), currentImgTile.getY());
            if (mage.determineRange() >= currentTile.distance(dragon.getCurrentTile())) {
                honeMarker.setLocation(currentImgTile.getX(), currentImgTile.getY());
                honeMarker.setVisible(true);
            } else {
                honeMarker.setVisible(false);
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: stops the timer and displays out the appropriate message based on how the game ends.
    @Override
    public void endGame(EndGameOutcome outcome) {
        super.endGame(outcome);
        switch (outcome) {
            case NO_DRAGON_HP_WIN:
                soundPlayer.playSuccess();
                declareWinOrLose.setText("WIN: Good job, you bound the dragon!");
                break;
            case STEPPED_IN_MINE_LOSE:
                soundPlayer.playExplosion();
                soundPlayer.playMineKO();
                declareWinOrLose.setText("LOSE: You detonated a mine.");
                break;
            case NO_MAGE_HP_LOSE:
                soundPlayer.playDragonKO();
                declareWinOrLose.setText("LOSE: You are out of health points.");
                break;
            case NO_TIME_LOSE:
                soundPlayer.playTimeKO();
                declareWinOrLose.setText("LOSE: You ran out of time.");
                break;
            default:
                declareWinOrLose.setText("Game aborted.");
        }
        wrapUpLastGame();
    }

    // MODIFIES: this' GameDistrict
    // EFFECTS: displays out last game's statistics and saves the best clear time
    private void wrapUpLastGame() {
        soundPlayer.stopBGM();
        inGame = false;
        EndGameOutcome gameOutcome = gd.getGameOutcome();
        if (gameOutcome.equals(EndGameOutcome.NO_DRAGON_HP_WIN)) {
            int timeTookToClear = TIME_LIMIT_IN_MILLISECONDS - timeLeftInGame;
            clearTime.setText("Clear time: " + formatMSAsTime(timeTookToClear));
            if (gd.bestClearTimeInMS == -1 || gd.bestClearTimeInMS > timeTookToClear) {
                gd.bestClearTimeInMS = timeTookToClear;
            }
        } else {
            clearTime.setText("Clear time: " + "--:--");
        }
        String bestClearTimeText = ("Best Clear Time: ");
        if (gd.bestClearTimeInMS == -1) {
            bestClearTime.setText(bestClearTimeText + "--:--");
        } else {
            bestClearTime.setText(bestClearTimeText + formatMSAsTime(gd.bestClearTimeInMS));
        }
        endGameOverlayPanel.setVisible(true);
    }


    // getters
    public boolean isInGame() {
        return inGame;
    }
}
