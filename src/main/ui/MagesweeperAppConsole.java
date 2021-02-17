package ui;

import model.Difficulty;
import model.DragonType;
import model.GameDistrict;
import persistence.PersistenceTool;
import persistence.SaveGameWorldReader;
import persistence.SaveGameWorldWriter;

import java.io.FileNotFoundException;
import java.util.Scanner;

// A terminal-based UI that allows users to play the game.
public class MagesweeperAppConsole {
    private GameWorld world;
    private final Scanner scanner;
    private final SaveGameWorldWriter writer = new SaveGameWorldWriter();
    private final SaveGameWorldReader reader = new SaveGameWorldReader();

    private boolean inGame;

    // EFFECTS: creates a new console ui, sets this to be not in a game.
    public MagesweeperAppConsole() {
        scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        inGame = false;
        welcomeMessage();
    }

    // EFFECTS: gets an integer from the user that is between 1 and the given parameter,
    //    additionally gives them the option to quit the game.
    private int returnUserIntegerInput(int numOptions) {
        System.out.println("\t [q] to quit the application (remember to save first!)");
        int result = 0;
        while (result < 1 || result > numOptions) {
            System.out.println("Please pick one of the above options by entering a number or [q].");
            if (scanner.hasNextInt()) {
                result = scanner.nextInt();
            } else if (scanner.hasNext("q")) {
                quitApp();
            } else {
                scanner.next();
            }
        }
        return result;
    }

    // MODIFIES: this
    // EFFECTS: prints a welcome menu to the terminal and generates/recreates a world
    private void welcomeMessage() {
        System.out.println("Welcome. Please select what you would like to do:");
        System.out.println("\t Note: the first two options will overwrite any existing worlds:");
        System.out.println("\t [1] Setup custom world");
        System.out.println("\t [2] Make default world");
        System.out.println("\t [3] Play your pre-existing world");
        processWelcomeMessage();
    }

    // MODIFIES: this
    // EFFECTS: decides what world to make based on user input
    private void processWelcomeMessage() {
        int userChoice = returnUserIntegerInput(3);
        switch (userChoice) {
            case 1:
                setupCustomWorld();
                enterWorldMenu();
                break;
            case 2:
                System.out.println("Generating default world...");
                world = new GameWorld(GameWorld.MAGE_DEFAULT_NAME, GameWorld.WORLD_DEFAULT_NAME);
                enterWorldMenu();
                break;
            case 3:
                try {
                    world = reader.readWorld(PersistenceTool.GAME_SAVE_PATH);
                    enterWorldMenu();
                } catch (Exception e) {
                    System.out.println("Could not load pre-existing world due to " + e.toString()
                            + "\n>>> Please choose option [1] or [2] instead.\n\n");
                    welcomeMessage();
                }
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: creates a world using a user-defined mage name and world name
    private void setupCustomWorld() {
        System.out.println("\n");
        System.out.println("Creating new custom world.");
        System.out.print("\t Pick your mage's name: ");
        String mageName = scanner.next();
        System.out.print("\t Pick your world's name: ");
        String worldName = scanner.next();
        world = new GameWorld(mageName, worldName);
    }

    // EFFECTS: presents a user that just entered the world lobby with a menu of options to pick from
    public void enterWorldMenu() {
        int userChoice;
        System.out.println("\n");
        System.out.println("You are in the world! \n Select your next move:");
        System.out.println("\t [1] Save current world.");
        System.out.println("\t [2] Tell me about this game and how to play it!");
        System.out.println("\t [3] Create a new district to play in.");
        if (world.getDistricts().size() > 0) {
            System.out.println("\t [4] Play an existing district.");
            System.out.println("\t [5] Delete an existing district.");
            userChoice = returnUserIntegerInput(5);
        } else {
            userChoice = returnUserIntegerInput(3);
        }
        processWorldMenu(userChoice);
    }

    // EFFECTS: decides on the next course of action given the user's choice in world menu,
    //    then brings them back to the world menu.
    private void processWorldMenu(int userChoice) {
        System.out.println("\n");
        switch (userChoice) {
            case 1:
                saveWorld();
                break;
            case 2:
                printInstructions();
                break;
            case 3:
                addNewDistrict();
                break;
            case 4:
                playDistrict(getDistrictFromUser());
                break;
            case 5:
                removeADistrict();
                break;
        }
        if (!inGame) {
            waitForWorldReturn();
        } else {
            System.out.println("Configuring game...");
        }
    }

    // EFFECTS: asks the user to press "1" to return to the world menu.
    private void waitForWorldReturn() {
        System.out.println("\nPress [1] to return to the world menu.");
        returnUserIntegerInput(1);
        enterWorldMenu();
    }

    // EFFECTS: removes a world district from the list of saved districts.
    private void removeADistrict() {
        GameDistrict toRemove = getDistrictFromUser();
        world.removeDistrict(toRemove);
    }

    // EFFECTS: prints out game instructions.
    private void printInstructions() {
        System.out.println("- Your task is to use your spells to bind a dragon "
                + "\n\twithout losing all your health points or stepping into any mines!");
        System.out.println("- You can move in 8 directions; use the direction keys q, w, e, a, d, z, x, c.");
        System.out.println("- To flag a tile, press SHIFT + key that corresponds to the direction of the mine");
        System.out.println("- To attack the dragon, press f (don't hold SHIFT).");
        System.out.println("- To block an attack, press b");
        System.out.println("Please read README.md for more information if you are interested.");
    }

    // MODIFIES: the given game district
    // EFFECTS: starts the game play for the given game district
    private void playDistrict(GameDistrict gd) {
        inGame = true;
        System.out.println("\n");
        System.out.format("Opening %s...\n", gd.getDistrictName());

        InGameEventsTrackerConsole currentTracker = new InGameEventsTrackerConsole(gd, world.getMage(), gd.getDragon());
        gd.setupGame(currentTracker);
        currentTracker.playDistrict();
        inGame = false;
    }

    // MODIFIES: this.world
    // EFFECTS: creates a new district and adds it to the list of saved districts.
    private void addNewDistrict() {
        System.out.println("\n");
        System.out.println("Pick the requirements for your new district... ");
        GameDistrict d = world.makeDistrict(pickDragonDifficulty(), pickMinesweeperDifficulty());
        world.addDistrict(d);
        System.out.println("\nYou have discovered the new district " + d.getDistrictName() + ".");
    }

    // EFFECTS: lets the user select a dragon difficulty from the given difficulties
    private DragonType pickDragonDifficulty() {
        System.out.println("\n");
        System.out.println("Pick a dragon: ");
        System.out.println("\t [1] Blaze -     melee attacks only.");
        System.out.println("\t [2] Tsunami -   melee and ranged attacks.");
        System.out.println("\t [3] Downburst - melee, ranged, and blast attacks.");
        return DragonType.values()[returnUserIntegerInput(3) - 1];
    }

    // EFFECTS: lets the user select a minesweeper difficulty from the given difficulties
    private Difficulty pickMinesweeperDifficulty() {
        System.out.println("\n");
        System.out.println("Pick a minesweeper difficulty: ");
        for (int i = 1; i <= Difficulty.values().length; i++) {
            Difficulty difficulty = Difficulty.values()[i - 1];
            System.out.println(String.format("\t [%d] %s - %d  Mines", i, difficulty.name(), difficulty.numMines));
        }
        return Difficulty.values()[returnUserIntegerInput(Difficulty.values().length) - 1];
    }

    // EFFECTS: lets the user select a game district from the list of saved districts
    private GameDistrict getDistrictFromUser() {
        System.out.println("\n");
        System.out.println("Pick one of the following districts:");
        return world.getDistricts().get(returnUserIntegerInput(printAvailableDistricts()) - 1);
    }

    // EFFECTS: prints saved districts and returns how many districts were found
    private int printAvailableDistricts() {
        int i = 1;
        for (GameDistrict district : world.getDistricts()) {
            System.out.format("\t [%d] %s\n", i, district.getDistrictName());
            i++;
        }
        return i - 1;
    }

    // EFFECTS: exits the current world and terminates the program
    private void quitApp() {
        System.out.println("Quitting... hope you had fun:)");
        System.exit(0);
    }

    // MODIFIES: the save json file
    // EFFECTS: saves the current world
    public void saveWorld() {
        try {
            writer.saveWorld(persistence.PersistenceTool.GAME_SAVE_PATH, world.getMage().getMageName(),
                    world.getWorldName(), world.getNumDistrictsMade(), world.getDistricts());
        } catch (FileNotFoundException e) {
            System.out.println("Unable to save file: the save path was not found.");
        }
    }
}
