package ui;

import model.Difficulty;
import model.DragonType;
import model.GameDistrict;
import model.Mage;

import java.util.ArrayList;

// A world that has a mage and can theoretically contain an infinite amount of game districts
public class GameWorld {
    public static final String MAGE_DEFAULT_NAME = "Djorn";
    public static final String WORLD_DEFAULT_NAME = "Earth";

    private ArrayList<GameDistrict> districts = new ArrayList<>();
    private final Mage mage;
    private final String worldName;
    private int numDistrictsMade = 0;

    // EFFECTS: sets the world up using custom mage and world names
    public GameWorld(String mageName, String worldName) {
        System.out.println(String.format("Setting up the world %s for your mage %s...", worldName, mageName));
        mage = new Mage(mageName);
        this.worldName = worldName;
    }

    // MODIFIES: this
    // EFFECTS: Add the given district to the list of saved districts in this if it isn't in the list; else do nothing
    public void addDistrict(GameDistrict gd) {
        if (!districts.contains(gd)) {
            districts.add(gd);
        }
    }

    // MODIFIES: this
    // EFFECTS: removes the given district to the list of saved districts in this if it exists; else does nothing.
    public void removeDistrict(GameDistrict gd) {
        districts.remove(gd);
    }

    // MODIFIES: this
    // EFFECTS: creates a new district and increases the amount of districts made in this world by 1
    public GameDistrict makeDistrict(DragonType dt, Difficulty d) {
        numDistrictsMade++;
        return new GameDistrict(dt, d, this);
    }

    // EFFECTS: lists and returns the saved districts' names
    public String[] listAvailableDistricts() {
        int numDistricts = districts.size();
        String[] strings = new String[numDistricts];
        int i = 0;
        for (GameDistrict district : districts) {
            strings[i] = String.format("[%d] %s", i + 1, district.getDistrictName());
            i++;
        }
        return strings;
    }

    // getters
    public int getNumDistrictsMade() {
        return numDistrictsMade;
    }

    public ArrayList<GameDistrict> getDistricts() {
        return districts;
    }

    public Mage getMage() {
        return mage;
    }

    public String getWorldName() {
        return worldName;
    }

    // setters
    public void setNumDistrictsMade(int districtsMade) {
        numDistrictsMade = districtsMade;
    }
}
