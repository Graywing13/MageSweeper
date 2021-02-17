package persistence;

import exceptions.InvalidInputException;
import model.GameDistrict;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ui.GameWorld;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;

// this class reads the information saved in the json save file and recreates the world.
public class SaveGameWorldReader extends PersistenceTool {

    // MODIFIES: this
    // EFFECTS: reads the json save file and then recreates the world with the json file's specifications
    public GameWorld readWorld(String savePath) throws IOException, ParseException, InvalidInputException {
        JSONObject gameSaveFile = (JSONObject) new JSONParser().parse(new FileReader(savePath));
        String mageName = (String) gameSaveFile.get("mageName");
        String worldName = (String) gameSaveFile.get("worldName");
        int districtsCreated = (int) (long) gameSaveFile.get("districtsCreated");
        JSONArray jsonArrDistricts = (JSONArray) gameSaveFile.get("districts");
        return remakeSavedWorld(mageName, worldName, districtsCreated, jsonArrDistricts);
    }

    // EFFECTS: makes the saved world given raw JSON data
    private GameWorld remakeSavedWorld(String mageName, String worldName, int districtsCreated,
                                       JSONArray jsonArrDistricts) throws InvalidInputException {
        GameWorld gw = new GameWorld(mageName, worldName);
        gw.setNumDistrictsMade(districtsCreated);
        for (Object districtInfoRawJson : jsonArrDistricts) {
            JSONArray districtInfoJArr = (JSONArray) districtInfoRawJson;
            String districtName = (String) districtInfoJArr.get(0);
            Point[] mineCoords = parsePointArray((JSONArray) districtInfoJArr.get(1));
            Point mageInitLocation = parsePoint((String) districtInfoJArr.get(2));
            Point dragonInitLocation = parsePoint((String) districtInfoJArr.get(3));
            int bestClearTimeInMS = (int) (long) districtInfoJArr.get(4);
            GameDistrict gd = new GameDistrict(districtName, mineCoords, mageInitLocation, dragonInitLocation,
                    bestClearTimeInMS, gw);
            gw.addDistrict(gd);
        }
        System.out.println("Setup finished.");
        return gw;
    }

    // EFFECTS: returns a JSON array that stores a list of java.awt.Point as a list of points
    private Point[] parsePointArray(JSONArray jaOfPointStrings) throws InvalidInputException {
        Point[] pointList = new Point[jaOfPointStrings.size()];
        int pointer = 0;
        for (Object pointInfoObj : jaOfPointStrings) {
            String pointInfoString = (String) pointInfoObj;
            pointList[pointer] = parsePoint(pointInfoString);
            pointer++;
        }
        return pointList;
    }

    // EFFECTS: returns the desired point given a string that has coordinates listed as [x=_intX_,y=_intY_]
    private Point parsePoint(String s) throws InvalidInputException {
        int indexOfXEquals = s.indexOf("x=");
        int indexOfCommaYEquals = s.indexOf(",y=");
        int indexOfClosingBrace = s.indexOf("]");
        if (indexOfXEquals == -1 || indexOfCommaYEquals == -1 || indexOfClosingBrace == -1
                || indexOfXEquals > indexOfCommaYEquals || indexOfCommaYEquals > indexOfClosingBrace) {
            throw new InvalidInputException();
        }
        int pointX = Integer.parseInt(s.substring(indexOfXEquals + 2, indexOfCommaYEquals));
        int pointY = Integer.parseInt(s.substring(indexOfCommaYEquals + 3, indexOfClosingBrace));
        return new Point(pointX, pointY);
    }
}
