package ui;

import model.Difficulty;
import model.DragonType;
import model.EndGameOutcome;
import model.GameDistrict;
import persistence.PersistenceTool;
import persistence.SaveGameWorldReader;
import persistence.SaveGameWorldWriter;
import ui.tools.FrameContentController;
import ui.tools.SoundPlayer;
import ui.utilities.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

// A swing-based UI that allows users to learn about, set up, and play the game.
public class MagesweeperAppSwing implements ActionListener {
    // Paths to other resources
    public static final String IMG_FOLDER_PATH = "./././data/images/";
    private static final String BKG_FOLDER_PATH = IMG_FOLDER_PATH + "backgrounds/";
    // Frame Constants - General
    public static final int FRAME_WIDTH = 1366 + 5; // the 5 is because windows margins need extra space
    public static final int FRAME_HEIGHT = 768 + 50; // the 50 is because windows' top bar has size

    // Frame Constants - Main Menu
    private static final int MAIN_MENU_BUTTON_X = 700;
    private static final int MAIN_MENU_TOPMOST_BUTTON_Y = 350;
    private static final int MAIN_MENU_BUTTON_SPACING_Y = 30;

    // Frame Constants - Selection Menus
    private static final int SELECTION_MENU_PADDING = 25;
    private static final Dimension SELECTION_MENU_TEXT_FIELD_DIMENSION = new Dimension(200, 24);
    private static final int SELECTION_MENU_MAX_COMPONENT_WIDTH = 1300;
    private static final Dimension SELECTION_MENU_LARGE_FIELD_DIMENSION =
            new Dimension(SELECTION_MENU_MAX_COMPONENT_WIDTH, FRAME_HEIGHT - 100);
    private static final int SELECTION_MENU_MAX_COMPONENT_HEIGHT = 50;
    private static final Font COURIER_NEW = new Font("Courier New", Font.PLAIN, 18);

    // variables
    private GameWorld world;
    private final SoundPlayer soundPlayer = SoundPlayer.getSoundPlayer();
    private final SaveGameWorldWriter writer = new SaveGameWorldWriter();
    private final SaveGameWorldReader reader = new SaveGameWorldReader();
    private JFrame frame = new JFrame("Magesweeper");
    private final FrameContentController frameContentController
            = FrameContentController.getFrameContentController(frame);

    // EFFECTS: creates a new swing ui (and prints messages to let the user know the game is loading; initial build
    //    can take a while).
    public MagesweeperAppSwing() {
        System.out.println("Loading... Please wait.");
        initFrame();
        System.out.println("Done.");
        welcomePanel();
    }

    // MODIFIES: this
    // EFFECTS: initializes a JFrame that will house the swing ui
    private void initFrame() {
        try {
            frame.setIconImage(ImageIO.read(new File(IMG_FOLDER_PATH + "logo.png")));
        } catch (IOException e) {
            System.out.println("Could not find logo path.");
        }
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int userChoice = JOptionPane.showConfirmDialog(frame, "Do you want to save before exiting?",
                        "Save?", JOptionPane.YES_NO_OPTION);
                if (userChoice == JOptionPane.YES_OPTION) {
                    saveWorld();
                }
                frame.setVisible(false);
                frame.dispose();
                System.exit(0);
            }
        });
        frame.setJMenuBar(createJMenuBar());
    }

    // MODIFIES: this
    // EFFECTS: creates a JMenuBar for this.
    private JMenuBar createJMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Quick Select");
        JMenuItem menuItem = new JMenuItem("Create new random district");
        menu.add(menuItem);
        menuItem.addActionListener(this);
        menuBar.add(menu);
        return menuBar;
    }

    // EFFECTS: creates a new district when action is performed
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            createDistrict();
        } catch (NullPointerException error) {
            JOptionPane.showMessageDialog(frame, "Please make or enter a world first.", null,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // MODIFIES: this.frame
    // EFFECTS: displays a JPanel that lets the user choose to generate/recreate a world
    private void welcomePanel() {
        JPanel panel = new JPanelImageBackground(BKG_FOLDER_PATH + "mainMenu.png");
        panel.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(Box.createRigidArea(new Dimension(MAIN_MENU_BUTTON_X, FRAME_HEIGHT)));

        JPanel buttonContainer = new JPanel();
        buttonContainer.setBackground(ColorTheme.TRANSPARENT.color);
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.add(Box.createRigidArea(new Dimension(0, MAIN_MENU_TOPMOST_BUTTON_Y)));
        String[] buttonTextForWelcomeMenu = new String[]{"Setup Custom World", "Setup Default World",
                "Play pre-existing world", "Settings/Credits/About"};
        createMainMenuButtons(buttonContainer, buttonTextForWelcomeMenu);

        panel.add(buttonContainer);
        panel.add(Box.createHorizontalGlue());

        frameContentController.showPanelOnFrame(panel);
    }

    // EFFECTS: creates the buttons for the welcome menu
    private void createMainMenuButtons(JPanel buttonContainer, String[] buttonTextForWelcomeMenu) {
        for (int i = 0; i < buttonTextForWelcomeMenu.length; i++) {
            int finalI = i;
            JButton button = new JButtonCustom(buttonTextForWelcomeMenu[i], ButtonType.NORMAL,
                    e -> processWelcomeMessage(finalI + 1));
            button.addActionListener(e -> soundPlayer.playFSharp4());
            button.setMinimumSize(button.getSize());
            button.setMaximumSize(button.getSize());
            buttonContainer.add(button);
            buttonContainer.add(Box.createRigidArea(new Dimension(0, MAIN_MENU_BUTTON_SPACING_Y)));
        }
    }

    // MODIFIES: this
    // EFFECTS: decides what world to make based on user input
    private void processWelcomeMessage(int userChoice) {
        switch (userChoice) {
            case 1:
                setupCustomWorld();
                break;
            case 2:
                setupDefaultWorld();
                break;
            case 3:
                setupExistingWorld();
                break;
            case 4:
                showSettingsCreditsAbout();
        }
    }

    // MODIFIES: this
    // EFFECTS: creates a page for the setup menu to be on
    private JPanel setupSelectionMenu() {
        JPanel selectionPanel = new JPanelImageBackground(BKG_FOLDER_PATH + "selectionMenu.png");

        selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.PAGE_AXIS));
        selectionPanel.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(SELECTION_MENU_PADDING,
                SELECTION_MENU_PADDING, SELECTION_MENU_PADDING, SELECTION_MENU_PADDING));
        selectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return selectionPanel;
    }

    // MODIFIES: this
    // EFFECTS: adds each content item passed into the method to the JPanel in the right location
    public void addSelectionMenuContent(JPanel panel, JComponent... components) {
        for (JComponent c : components) {
            c.setMaximumSize(new Dimension(SELECTION_MENU_MAX_COMPONENT_WIDTH, SELECTION_MENU_MAX_COMPONENT_HEIGHT));
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(c);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a "next" button at the bottom of the screen
    private void addNextButton(JPanel panel, ActionListener l) {
        Dimension minSize = new Dimension(0, 100);
        Dimension prefSize = new Dimension(0, 100);
        Dimension maxSize = new Dimension(0, 400);
        Box.Filler filler = new Box.Filler(minSize, prefSize, maxSize);
        panel.add(filler);
        JButton nextButton = new JButtonCustom("Next", ButtonType.SELECTION_MENU_NEXT, l);
        nextButton.addActionListener(e -> soundPlayer.playFSharp3());
        nextButton.setBorder(BorderFactory.createEmptyBorder(SELECTION_MENU_PADDING, SELECTION_MENU_PADDING,
                SELECTION_MENU_PADDING, SELECTION_MENU_PADDING));
        nextButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        panel.add(nextButton);
    }

    // MODIFIES: this
    // EFFECTS: Adds specially formatted action text of the right size and customization to the selected JPanel
    private void addActionText(JPanel panel, String actionText) {
        JLabel label = new JLabelCustom(actionText, LabelType.SELECTION_ACTION);
        label.setMaximumSize(new Dimension(label.getWidth(), SELECTION_MENU_MAX_COMPONENT_HEIGHT));
        panel.add(label);
    }

    // MODIFIES: this
    // EFFECTS: creates a world using a default mage name and world name
    private void setupDefaultWorld() {
        createAndEnterWorld(GameWorld.MAGE_DEFAULT_NAME, GameWorld.WORLD_DEFAULT_NAME);
    }

    // MODIFIES: this
    // EFFECTS: recreates the saved world if possible; else redirects user to create a new world.
    private void setupExistingWorld() {
        JPanel setupWorldPanel = setupSelectionMenu();
        JLabel titleLabel = new JLabelCustom("Locating saved world...", LabelType.SELECTION_TITLE);
        addSelectionMenuContent(setupWorldPanel, titleLabel);
        try {
            world = reader.readWorld(PersistenceTool.GAME_SAVE_PATH);
            JLabel label1 = new JLabelCustom(String.format("World %s located. Welcome back, %s.", world.getWorldName(),
                    world.getMage().getMageName()), LabelType.SELECTION_NORMAL);
            JLabel label2 = new JLabelCustom("", LabelType.SELECTION_NORMAL);
            addSelectionMenuContent(setupWorldPanel, titleLabel, label1, label2);
            addActionText(setupWorldPanel, ">>> Press Next to enter world.");
            addNextButton(setupWorldPanel, successful -> enterWorldMenu());
        } catch (Exception e) {
            JLabel label1 = new JLabelCustom("Could not load pre-existing world due to " + e.toString(),
                    LabelType.SELECTION_NORMAL);
            JLabel label2 = new JLabelCustom(">>> Please create a new world instead...", LabelType.SELECTION_NORMAL);
            JLabel label3 = new JLabelCustom("", LabelType.SELECTION_NORMAL);
            addSelectionMenuContent(setupWorldPanel, titleLabel, label1, label2, label3);
            addActionText(setupWorldPanel, ">>> Press Next to return to main menu.");
            addNextButton(setupWorldPanel, unsuccessful -> welcomePanel());
        }
        frameContentController.showPanelOnFrame(setupWorldPanel);
    }

    // MODIFIES: this
    // EFFECTS: shows the settings / credits / about page
    private void showSettingsCreditsAbout() {
        JPanel settingsCreditsAboutPanel = setupSelectionMenu();
        JLabel titleLabel = new JLabelCustom("Settings / Credits / About Menu", LabelType.SELECTION_TITLE);
        JButton settingsButton = new JButtonCustom("Settings Menu", ButtonType.NORMAL,
                e -> openSettingsMenu());
        JButton creditsButton = new JButtonCustom("Credits", ButtonType.NORMAL,
                e -> openCredits());
        JButton aboutButton = new JButtonCustom("About: Game Mechanics & Story", ButtonType.NORMAL,
                e -> openAbout());

        Dimension minSize = new Dimension(0, 100);
        Dimension prefSize = new Dimension(0, 100);
        Dimension maxSize = new Dimension(0, 400);
        Box.Filler filler = new Box.Filler(minSize, prefSize, maxSize);

        JButton returnButton = new JButtonCustom("Back", ButtonType.SELECTION_MENU_NEXT,
                e -> welcomePanel());
        returnButton.setBorder(BorderFactory.createEmptyBorder(SELECTION_MENU_PADDING, SELECTION_MENU_PADDING,
                SELECTION_MENU_PADDING, SELECTION_MENU_PADDING));
        returnButton.setAlignmentY(Component.BOTTOM_ALIGNMENT);

        addSelectionMenuContent(settingsCreditsAboutPanel, titleLabel, settingsButton, creditsButton, aboutButton,
                filler, returnButton);
        frameContentController.showPanelOnFrame(settingsCreditsAboutPanel);
    }

    // MODIFIES: this
    // EFFECTS: shows the settings menu.
    private void openSettingsMenu() {
        JPanel settingsPanel = setupSelectionMenu();
        JLabel title = new JLabelCustom("Settings.", LabelType.SELECTION_TITLE);
        JCheckBox soundEffects = new JCheckBox("Sound Effects?", true);
        JCheckBox voiceLines = new JCheckBox("Voice Lines?", true);
        JCheckBox backgroundMusic = new JCheckBox("Background Music?", true);
        addSelectionMenuContent(settingsPanel, title, soundEffects, voiceLines, backgroundMusic);
        addNextButton(settingsPanel, e -> {
            soundPlayer.haveSoundEffectsOrNot(soundEffects.isSelected());
            soundPlayer.haveVoiceLinesOrNot(voiceLines.isSelected());
            soundPlayer.haveBackgroundMusicOrNot(backgroundMusic.isSelected());
            showSettingsCreditsAbout();
        });
        frameContentController.showPanelOnFrame(settingsPanel);
    }

    // MODIFIES: this
    // EFFECTS: shows the credits of this application
    private void openCredits() {
        JPanel creditsPanel = setupSelectionMenu();
        JLabel title = new JLabelCustom("Credits", LabelType.SELECTION_TITLE);
        JLabel lb1 = new JLabelCustom("Game Ideation: BluePoisonDartFrog (BPDF)", LabelType.SELECTION_NORMAL);
        JLabel lb2 = new JLabelCustom("Code (unless credited in code text): BPDF", LabelType.SELECTION_NORMAL);
        JLabel lb3 = new JLabelCustom("Visuals: BPDF", LabelType.SELECTION_NORMAL);
        JLabel lb4 = new JLabelCustom("Sound Effects: BPDF (via audacity)", LabelType.SELECTION_NORMAL);
        JLabel lb5 = new JLabelCustom("Voice Lines: BPDF", LabelType.SELECTION_NORMAL);
        JLabel lb6 = new JLabelCustom("In-Game Background Music: Original song is Gurenge by LiSA, "
                + "Piano cover and arrangement by BPDF", LabelType.SELECTION_NORMAL);
        addSelectionMenuContent(creditsPanel, title, lb1, lb2, lb3, lb4, lb5, lb6);
        addNextButton(creditsPanel, e -> showSettingsCreditsAbout());
        frameContentController.showPanelOnFrame(creditsPanel);
    }

    // MODIFIES: this
    // EFFECTS: shows the about page.
    private void openAbout() {
        JPanel aboutPanel = setupSelectionMenu();
        JLabel title = new JLabelCustom("About.", LabelType.SELECTION_TITLE);
        JLabel lb1 = new JLabelCustom("Game Mechanics", LabelType.SELECTION_ACTION);
        JLabel lb2 = new JLabelCustom("Mage HP = Dragon HP = 1000", LabelType.SELECTION_NORMAL);
        JLabel lb3 = new JLabelCustom("Mage ATK = 50 * (1 + hitCombo * 0.01)"
                + " | Mage range: 2.3", LabelType.SELECTION_NORMAL);
        JLabel lb4 = new JLabelCustom("Dragon ATK = 50 * ATK_MODIFIER", LabelType.SELECTION_NORMAL);
        JLabel lb5 = new JLabelCustom("Dragon ATK Types (atkMod, tileRange): "
                + "MELEE(1, 1.5) | RANGED(4, 2.3) | BLAST (9, 9.9)", LabelType.SELECTION_NORMAL);
        JLabel lb6 = new JLabelCustom("", LabelType.SELECTION_NORMAL);
        JLabel lb7 = new JLabelCustom("Time limit for all difficulties: 84 seconds", LabelType.SELECTION_NORMAL);
        JLabel lb8 = new JLabelCustom("", LabelType.SELECTION_NORMAL);
        JLabel lb9 = new JLabelCustom("Note: The above is subject to change.", LabelType.SELECTION_NORMAL);
        JButton openStoryButton = new JButtonCustom("View Game Story", ButtonType.NORMAL, e -> openStory());
        addSelectionMenuContent(aboutPanel, title, lb1, lb2, lb3, lb4, lb5, lb6, lb7, lb8, lb9, openStoryButton);
        addNextButton(aboutPanel, e -> showSettingsCreditsAbout());
        frameContentController.showPanelOnFrame(aboutPanel);
    }

    // MODIFIES: this
    // EFFECTS: shows the story page
    private void openStory() {
        JPanel storyPanel = setupSelectionMenu();
        JTextArea storyField = new JTextArea();
        storyField.setEditable(false);
        storyField.setLineWrap(true);
        storyField.setWrapStyleWord(true);
        storyField.setFont(COURIER_NEW);
        setStoryFieldText(storyField);
        storyField.setMaximumSize(SELECTION_MENU_LARGE_FIELD_DIMENSION);
        storyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        storyPanel.add(storyField);
        addNextButton(storyPanel, e -> openAbout());
        frameContentController.showPanelOnFrame(storyPanel);
    }

    // EFFECTS: sets the text of the story field.
    private void setStoryFieldText(JTextArea storyField) {
        storyField.setText("As the omega of the most esteemed mage school in your world, you have taken on the "
                + "daunting task of binding three dangerous dragons to prove your worth. You have only one skill, Force"
                + " Shield, can block you from all dragons' attack patterns, but you are unable to move or attack "
                + "during the skill and it takes time for the skill to be prepared. This skill is unique to you, "
                + "rendering you the only hero able to save your world from these havoc-wreaking dragons.\n\n"

                + "However, these three dragons can attack and lower your HP; if you take too much damage, you will "
                + "have to retreat. Additionally, they are only bindable in their dwellings, caverns filled with "
                + "underground tunneling mines from ancient scientists' failed attempt to weaken the dragons; the "
                + "dragons cannot detonate mines because they always only fly or hover. Unfortunately, you don't fly, "
                + "so you would instantly be injured if you stepped into any live mines and would yet again be forced "
                + "to retreat.\n\n"

                + "Thankfully, you have the scientists' Mine Controller which can tell you the total amount of mines in"
                + " the caverns and the amount of mines in the 8 tiles around you. The controller can also disable the "
                + "mines' ability to move around for the duration of a \"pacify period\" and deploy a contact defuser "
                + "on the next tile you move onto, changing the tile to a mine-free safe square. However, there's a "
                + "catch: because the controller is so old, its software has mutated to detonate all the mines after "
                + "the pacify period has ended, meaning you have to act fast.\n\n"

                + "Equipped with your skill Force Shield, the Mine Controller, and the standard dragon binding spells, "
                + "you set out on this perilous but rewarding journey! Your task is to bind the dragon without stepping"
                + " in any of the mines before the time limit!");
    }

    // MODIFIES: this
    // EFFECTS: sets up a world using a user-defined mage name and world name
    private void setupCustomWorld() {
        JPanel setupWorldPanel = setupSelectionMenu();

        JLabel titleLabel = new JLabelCustom("Creating new custom world.", LabelType.SELECTION_TITLE);
        JLabel mageNameLabel = new JLabelCustom("Pick your mage's name:", LabelType.SELECTION_NORMAL);
        JTextField mageNameField = new JTextField(GameWorld.MAGE_DEFAULT_NAME);
        mageNameField.setSize(SELECTION_MENU_TEXT_FIELD_DIMENSION);
        JLabel worldNameLabel = new JLabelCustom("Pick your world's name:", LabelType.SELECTION_NORMAL);
        JTextField worldNameField = new JTextField(GameWorld.WORLD_DEFAULT_NAME);
        mageNameField.setSize(SELECTION_MENU_TEXT_FIELD_DIMENSION);
        addSelectionMenuContent(setupWorldPanel, titleLabel, mageNameLabel, mageNameField, worldNameLabel,
                worldNameField);

        addNextButton(setupWorldPanel, e -> createAndEnterWorld(mageNameField.getText(), worldNameField.getText()));

        frameContentController.showPanelOnFrame(setupWorldPanel);
    }

    // MODIFIES: this
    // EFFECTS: creates and enters the world that was just defined
    public void createAndEnterWorld(String mageName, String worldName) {
        JPanel setupWorldPanel = setupSelectionMenu();
        world = new GameWorld(mageName, worldName);

        JLabel titleLabel = new JLabelCustom("Creating new default world...", LabelType.SELECTION_TITLE);
        JLabel label1 = new JLabelCustom(String.format("New world %s created. Welcome, Mage %s.", world.getWorldName(),
                world.getMage().getMageName()), LabelType.SELECTION_NORMAL);
        JLabel label2 = new JLabelCustom("", LabelType.SELECTION_NORMAL);
        addSelectionMenuContent(setupWorldPanel, titleLabel, label1, label2);
        addActionText(setupWorldPanel, ">>> Press Next to enter world.");
        addNextButton(setupWorldPanel, e -> enterWorldMenu());

        frameContentController.showPanelOnFrame(setupWorldPanel);
    }

    // EFFECTS: presents a user that just entered the world lobby with a menu of options to pick from
    public void enterWorldMenu() {
        JPanel setupWorldPanel = setupSelectionMenu();
        JComboBox<String> chooser;

        JLabel title = new JLabelCustom("You are in the world! Select your next move:", LabelType.SELECTION_TITLE);
        JLabel label1 = new JLabelCustom("[1] Save current world.", LabelType.SELECTION_NORMAL);
        JLabel label2 = new JLabelCustom("[2] Tell me about this game and how to play it!", LabelType.SELECTION_NORMAL);
        JLabel label3 = new JLabelCustom("[3] View Districts and High Scores.", LabelType.SELECTION_NORMAL);
        JLabel label4 = new JLabelCustom("[4] Return to main menu (remember to save first!).",
                LabelType.SELECTION_NORMAL);
        JLabel label5 = new JLabelCustom("[5] Create a new district to play in.", LabelType.SELECTION_NORMAL);
        if (world.getDistricts().size() > 0) {
            JLabel label6 = new JLabelCustom("[6] Play an existing district.", LabelType.SELECTION_NORMAL);
            JLabel label7 = new JLabelCustom("[7] Delete an existing district.", LabelType.SELECTION_NORMAL);
            chooser = new JComboBoxCustom<>("1", "2", "3", "4", "5", "6", "7");
            addSelectionMenuContent(setupWorldPanel, title, label1, label2, label3, label4, label5,
                    label6, label7, chooser);
        } else {
            chooser = new JComboBoxCustom<>("1", "2", "3", "4", "5");
            addSelectionMenuContent(setupWorldPanel, title, label1, label2, label3, label4, label5, chooser);
        }
        addNextButton(setupWorldPanel, e -> processWorldMenu(chooser.getSelectedIndex() + 1));

        frameContentController.showPanelOnFrame(setupWorldPanel);
    }

    // EFFECTS: decides on the next course of action given the user's choice in world menu,
    //    then brings them back to the world menu.
    private void processWorldMenu(int userChoice) {
        switch (userChoice) {
            case 1:
                saveWorld();
                break;
            case 2:
                showInstructionsPanel();
                break;
            case 3:
                showGameDistricts();
                break;
            case 4:
                welcomePanel();
                break;
            case 5:
                addNewDistrict();
                break;
            case 6:
                chooseDistrictToPlay();
                break;
            case 7:
                removeADistrict();
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: prints out game instructions.
    private void showInstructionsPanel() {
        JPanel instructionsPanel = setupSelectionMenu();

        JLabel titleLabel = new JLabelCustom("How to play", LabelType.SELECTION_TITLE);
        JLabel lb1 = new JLabelCustom("- Your task is to use your spells to bind a dragon "
                + "without losing all your health points or stepping into any mines!", LabelType.SELECTION_NORMAL);
        JLabel lb2 = new JLabelCustom("- You can move in 8 directions; "
                + "use the direction keys q, w, e, a, d, z, x, c.", LabelType.SELECTION_NORMAL);
        JLabel lb3 = new JLabelCustom("- To flag a tile, press SHIFT + direction key that "
                + "corresponds to the direction of the mine; eg SHIFT + [a] flags up", LabelType.SELECTION_NORMAL);
        JLabel lb4 = new JLabelCustom("- To attack the dragon, press f (don't hold SHIFT).",
                LabelType.SELECTION_NORMAL);
        JLabel lb5 = new JLabelCustom("- To block an attack, press b", LabelType.SELECTION_NORMAL);
        JLabel lb6 = new JLabelCustom("", LabelType.SELECTION_NORMAL);
        JLabel lb7 = new JLabelCustom("- The dragon attacks once per second, and the "
                + "zone of the dragon's next attack is red.", LabelType.SELECTION_NORMAL);
        JLabel lb8 = new JLabelCustom("- The mage can attack once every 0.5 seconds. Each attack "
                + "that connects adds to the hit combo and boosts their dmg.", LabelType.SELECTION_NORMAL);
        JLabel lb9 = new JLabelCustom("- The mage's BLOCK skill lasts for 1 second; during that second,"
                + " the mage cannot move nor deal damage.", LabelType.SELECTION_NORMAL);
        JLabel lb10 = new JLabelCustom("Please read README.md for more information if you are interested.",
                LabelType.SELECTION_NORMAL);

        addSelectionMenuContent(instructionsPanel, titleLabel, lb1, lb2, lb3, lb4, lb5, lb6, lb7, lb8, lb9, lb10);
        addNextButton(instructionsPanel, e -> enterWorldMenu());

        frameContentController.showPanelOnFrame(instructionsPanel);
    }

    // MODIFIES: this
    // EFFECTS: shows all the districts that were created for this world, with their best clear times if applicable.
    private void showGameDistricts() {
        JPanel showGameDistrictsPanel = setupSelectionMenu();
        JLabel title = new JLabelCustom("Game Districts Saved in This World", LabelType.SELECTION_TITLE);

        int numDistricts = world.getDistricts().size();
        String[] districtsList = new String[numDistricts];
        int i = 0;
        for (GameDistrict district : world.getDistricts()) {
            String districtString = district.getDistrictName() + " | Best Clear Time: ";
            int bestClearTime = district.bestClearTimeInMS;
            if (bestClearTime != -1) {
                districtString += ui.tools.TimeFormatter.formatMSAsTime(bestClearTime);
            } else {
                districtString += "--:--";
            }
            districtsList[i] = districtString;
            i++;
        }

        JList<String> list = new JList<>(districtsList);
        list.setFont(COURIER_NEW);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(SELECTION_MENU_LARGE_FIELD_DIMENSION);
        addSelectionMenuContent(showGameDistrictsPanel, title, scrollPane);
        addNextButton(showGameDistrictsPanel, e -> enterWorldMenu());

        frameContentController.showPanelOnFrame(showGameDistrictsPanel);
    }


    // MODIFIES: this
    // EFFECTS: lets the user choose a district to play
    private void chooseDistrictToPlay() {
        JPanel chooseDistrictToPlayPanel = setupSelectionMenu();
        JLabel title = new JLabelCustom("Choose district to play menu", LabelType.SELECTION_TITLE);
        addSelectionMenuContent(chooseDistrictToPlayPanel, title);
        JComboBox<String> removeDistrictChooser = setupDistrictJComboBox(chooseDistrictToPlayPanel);
        addNextButton(chooseDistrictToPlayPanel,
                e -> playDistrict(world.getDistricts().get(removeDistrictChooser.getSelectedIndex())));

        frameContentController.showPanelOnFrame(chooseDistrictToPlayPanel);
    }

    // MODIFIES: the given game district
    // EFFECTS: starts the game play for the given game district
    private void playDistrict(GameDistrict gd) {
        InGameEventsTrackerSwing currentTracker = new InGameEventsTrackerSwing(gd, world.getMage(), gd.getDragon());
        gd.setupGame(currentTracker);
        waitForWorldReturn(currentTracker, gd);
        currentTracker.playDistrict();
    }

    // MODIFIES: this
    // EFFECTS: asks the user to press "Next" to return to the world menu.
    private void waitForWorldReturn(InGameEventsTrackerSwing tracker, GameDistrict gd) {
        JPanel waitForWorldReturnPanel = setupSelectionMenu();
        JLabel title = new JLabelCustom("Replay?", LabelType.SELECTION_TITLE);
        JButton replayButton = new JButtonCustom("Replay Level", ButtonType.NORMAL, e -> {
            if (tracker.isInGame()) {
                tracker.endGame(EndGameOutcome.STILL_PLAYING_GAME);
            }
            playDistrict(gd);
        });
        JLabel label = new JLabelCustom("Press Next to return to the world menu.", LabelType.SELECTION_ACTION);
        addSelectionMenuContent(waitForWorldReturnPanel, title, replayButton, label);
        addNextButton(waitForWorldReturnPanel, e -> {
            if (tracker.isInGame()) {
                tracker.endGame(EndGameOutcome.STILL_PLAYING_GAME);
            }
            enterWorldMenu();
        });
        frameContentController.showPanelOnFrame(waitForWorldReturnPanel);
    }

    // MODIFIES: this, this.world
    // EFFECTS: creates a new district and adds it to the list of saved districts.
    private void addNewDistrict() {
        JPanel addNewDistrictPanel = setupSelectionMenu();
        JLabel title = new JLabelCustom("Pick the requirements for your new district... ", LabelType.SELECTION_NORMAL);
        JLabel pickDragonLabel = new JLabelCustom("Pick a dragon: ", LabelType.SELECTION_ACTION);
        JComboBox<String> dragonChooser = new JComboBoxCustom<>("[1] Blaze -     melee attacks only.",
                "[2] Tsunami -   melee and ranged attacks.", "[3] Downburst - melee, ranged, and blast attacks.");

        JLabel pickMineLabel = new JLabelCustom("Pick a minesweeper difficulty: ", LabelType.SELECTION_ACTION);
        String[] mines = new String[Difficulty.values().length];
        for (int i = 0; i < Difficulty.values().length; i++) {
            Difficulty difficulty = Difficulty.values()[i];
            mines[i] = (String.format("\t [%d] %s - %d  Mines", i + 1, difficulty.name(), difficulty.numMines));
        }
        JComboBox<String> mineChooser = new JComboBoxCustom<>(mines);

        addSelectionMenuContent(addNewDistrictPanel, title, pickDragonLabel, dragonChooser,
                pickMineLabel, mineChooser);
        addNextButton(addNewDistrictPanel, e -> createDistrict(dragonChooser.getSelectedIndex(),
                mineChooser.getSelectedIndex()));

        frameContentController.showPanelOnFrame(addNewDistrictPanel);
    }

    // MODIFIES: this, this.world
    // EFFECTS: lets the user select a dragon and minesweeper difficulty for a district then adds it to the world
    private void createDistrict(int dragonIndex, int mineIndex) {
        JPanel createDistrictPanel = setupSelectionMenu();

        DragonType dragonDifficulty = DragonType.values()[dragonIndex];
        Difficulty mineDifficulty = Difficulty.values()[mineIndex];
        GameDistrict d = world.makeDistrict(dragonDifficulty, mineDifficulty);
        world.addDistrict(d);
        JLabel title = new JLabelCustom("\nYou have discovered the new district " + d.getDistrictName() + ".",
                LabelType.SELECTION_TITLE);
        addSelectionMenuContent(createDistrictPanel, title);
        addNextButton(createDistrictPanel, e -> enterWorldMenu());

        frameContentController.showPanelOnFrame(createDistrictPanel);
    }

    // MODIFIES: this, this.world
    // EFFECTS: lets the user select a dragon and minesweeper difficulty for a district then adds it to the world
    private void createDistrict() {
        JPanel createDistrictPanel = setupSelectionMenu();

        Random random = new Random();
        DragonType dragonDifficulty = DragonType.values()[random.nextInt(DragonType.values().length)];
        Difficulty mineDifficulty = Difficulty.values()[random.nextInt(Difficulty.values().length)];
        GameDistrict d = world.makeDistrict(dragonDifficulty, mineDifficulty);
        world.addDistrict(d);
        JLabel title = new JLabelCustom("\nYou have discovered the new district " + d.getDistrictName() + ".",
                LabelType.SELECTION_TITLE);
        addSelectionMenuContent(createDistrictPanel, title);
        addNextButton(createDistrictPanel, e -> enterWorldMenu());

        frameContentController.showPanelOnFrame(createDistrictPanel);
    }

    // MODIFIES: this, this.world
    // EFFECTS: removes a world district from the list of saved districts.
    private void removeADistrict() {
        JPanel removeADistrictPanel = setupSelectionMenu();
        JLabel title = new JLabelCustom("Remove district menu", LabelType.SELECTION_TITLE);
        addSelectionMenuContent(removeADistrictPanel, title);
        JComboBox<String> removeDistrictChooser = setupDistrictJComboBox(removeADistrictPanel);
        addNextButton(removeADistrictPanel, e -> {
            world.removeDistrict(world.getDistricts().get(removeDistrictChooser.getSelectedIndex()));
            enterWorldMenu();
        });

        frameContentController.showPanelOnFrame(removeADistrictPanel);
    }

    // MODIFIES: this
    // EFFECTS: lets the user select a game district from the list of saved districts
    private JComboBox<String> setupDistrictJComboBox(JPanel panel) {
        JLabel label = new JLabelCustom("Pick one of the following districts:", LabelType.SELECTION_ACTION);
        JComboBox<String> chooser = new JComboBoxCustom<>(world.listAvailableDistricts());
        addSelectionMenuContent(panel, label, chooser);
        return chooser;
    }

    // MODIFIES: the save json file
    // EFFECTS: saves the current world
    public void saveWorld() {
        JPanel saveWorldPanel = setupSelectionMenu();
        JLabel title = new JLabelCustom("Saving world...", LabelType.SELECTION_TITLE);
        JLabel resultTest;

        try {
            try {
                writer.saveWorld(PersistenceTool.GAME_SAVE_PATH, world.getMage().getMageName(),
                        world.getWorldName(), world.getNumDistrictsMade(), world.getDistricts());
            } catch (NullPointerException e) {
                System.out.println("No world was found. Save unsuccessful.");
            }
            resultTest = new JLabelCustom("World save successful.", LabelType.SELECTION_NORMAL);
        } catch (FileNotFoundException e) {
            resultTest = new JLabelCustom("Unable to save file: the save path was not found.",
                    LabelType.SELECTION_ACTION);
        }

        addSelectionMenuContent(saveWorldPanel, title, resultTest);
        addNextButton(saveWorldPanel, e -> enterWorldMenu());

        frameContentController.showPanelOnFrame(saveWorldPanel);
    }
}
