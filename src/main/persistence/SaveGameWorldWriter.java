package persistence;

import model.GameDistrict;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

// A tool that saves the current game into a json file
public class SaveGameWorldWriter extends PersistenceTool {
    protected JSONObject gameSaveFile;

    // MODIFIES: this
    // EFFECTS: Saves the current world into a file
    public void saveWorld(String savePath, String mageName, String worldName, int districtsMade,
                          ArrayList<GameDistrict> districts) throws FileNotFoundException {
        gameSaveFile = new JSONObject();
        gameSaveFile.put("mageName", mageName);
        gameSaveFile.put("worldName", worldName);
        gameSaveFile.put("districtsCreated", districtsMade);
        JSONArray districtsArray = new JSONArray();
        for (GameDistrict district : districts) {
            JSONArray districtInfo = new JSONArray();
            districtInfo.put(0, district.getDistrictName());
            districtInfo.put(1, district.getMineCoords());
            districtInfo.put(2, district.getMageInitialLocation());
            districtInfo.put(3, district.getDragonInitialLocation());
            districtInfo.put(4, district.getBestClearTimeInMS());
            districtsArray.put(districtInfo);
        }
        gameSaveFile.put("districts", districtsArray);

        PrintWriter pw = new PrintWriter(savePath);
        pw.write(gameSaveFile.toString());
        pw.flush();
        pw.close();
    }
}
