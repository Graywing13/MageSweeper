package persistence;

import exceptions.InvalidInputException;
import model.Difficulty;
import model.DragonType;
import model.GameDistrict;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.GameWorld;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

// this class tests the persistence tools (i.e. everything in the persistence folder)
public class PersistenceToolTest {
    public static final String TESTS_FOR_GAME_SAVE_FOLDER_PATH = "./././data/saveFileTests/";
    final static String SAVE_PATH_FOR_TESTS = TESTS_FOR_GAME_SAVE_FOLDER_PATH + "gameSaveFileTests.json";
    SaveGameWorldWriter writer;
    SaveGameWorldReader reader;
    GameWorld originalWorld;

    @BeforeEach
    public void setup() {
        // The following try/catch block and assertEquals is based on https://www.baeldung.com/java-delete-file-contents
        try { // clears the save file
            new PrintWriter(SAVE_PATH_FOR_TESTS).close();
        } catch (FileNotFoundException e) {
            fail();
        }
        assertEquals(0, (new File(SAVE_PATH_FOR_TESTS)).length()); // makes sure save file is empty
        writer = new SaveGameWorldWriter();
        reader = new SaveGameWorldReader();
    }

    @Test
    public void testSaveWorldEmptyWorld() {
        originalWorld = new GameWorld(GameWorld.MAGE_DEFAULT_NAME, GameWorld.WORLD_DEFAULT_NAME);
        GameWorld readerGeneratedWorld;
        try {
            writer.saveWorld(SAVE_PATH_FOR_TESTS, originalWorld.getMage().getMageName(), originalWorld.getWorldName(),
                    originalWorld.getNumDistrictsMade(), originalWorld.getDistricts());

            readerGeneratedWorld = reader.readWorld(SAVE_PATH_FOR_TESTS);
            assertEquals(originalWorld.getMage().getMageName(), readerGeneratedWorld.getMage().getMageName());
            assertEquals(originalWorld.getNumDistrictsMade(), readerGeneratedWorld.getNumDistrictsMade());
            assertEquals(originalWorld.getWorldName(), readerGeneratedWorld.getWorldName());
            assertEquals(originalWorld.getDistricts().size(), readerGeneratedWorld.getDistricts().size());
        } catch (ParseException | InvalidInputException | IOException e) {
            fail();
        }
    }

    @Test
    public void testSaveWorldEditedWorldOneDistrict() {
        originalWorld = new GameWorld("mageName", "worldName");
        originalWorld.addDistrict(originalWorld.makeDistrict(DragonType.BLAZE, Difficulty.HARD));

        GameWorld readerGeneratedWorld;
        try {
            writer.saveWorld(SAVE_PATH_FOR_TESTS, originalWorld.getMage().getMageName(), originalWorld.getWorldName(),
                    originalWorld.getNumDistrictsMade(), originalWorld.getDistricts());

            // checks general world is the same
            readerGeneratedWorld = reader.readWorld(SAVE_PATH_FOR_TESTS);
            assertEquals(originalWorld.getMage().getMageName(), readerGeneratedWorld.getMage().getMageName());
            assertEquals(originalWorld.getNumDistrictsMade(), readerGeneratedWorld.getNumDistrictsMade());
            assertEquals(originalWorld.getWorldName(), readerGeneratedWorld.getWorldName());
            assertEquals(1, readerGeneratedWorld.getDistricts().size());

            // checks world's district is the same
            GameDistrict readerGD0 = readerGeneratedWorld.getDistricts().get(0);
            GameDistrict originalGD0 = originalWorld.getDistricts().get(0);
            assertEquals(Difficulty.HARD, readerGD0.getDifficultyLevel());
            assertEquals(DragonType.BLAZE, readerGD0.getDragon().getDragonType());
            assertEquals(originalGD0.getMageInitialLocation(), readerGD0.getMageInitialLocation());
            assertEquals(originalGD0.getDragonInitialLocation(), readerGD0.getDragonInitialLocation());
            assertEquals(originalGD0.getBestClearTimeInMS(), readerGD0.getBestClearTimeInMS());
            assertTrue(Arrays.asList(readerGD0.getMineCoords())
                    .containsAll(Arrays.asList(originalGD0.getMineCoords())));
        } catch (ParseException | InvalidInputException | IOException e) {
            fail();
        }
    }

    @Test
    public void testSaveWorldFileNotFoundExceptionExpected() {
        originalWorld = new GameWorld(GameWorld.MAGE_DEFAULT_NAME, GameWorld.WORLD_DEFAULT_NAME);
        try {
            writer.saveWorld(SAVE_PATH_FOR_TESTS + "/nonexistent", originalWorld.getMage().getMageName(), originalWorld.getWorldName(),
                    originalWorld.getNumDistrictsMade(), originalWorld.getDistricts());
            fail();
        } catch (FileNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testReadWorldGarbledDifficultyLevel() {
        assertTrue(invalidInputExpected(TESTS_FOR_GAME_SAVE_FOLDER_PATH + "gameSaveFileTestsGarbledDifficultyLevel.json"));
    }

    @Test
    public void testReadWorldGarbledDragonType() {
        assertTrue(invalidInputExpected(TESTS_FOR_GAME_SAVE_FOLDER_PATH + "gameSaveFileTestsGarbledDragonType.json"));
    }

    @Test
    public void testReadWorldGarbledMineXString() {
        assertTrue(invalidInputExpected(TESTS_FOR_GAME_SAVE_FOLDER_PATH + "gameSaveFileTestsGarbledMineXString.json"));
    }

    @Test
    public void testReadWorldGarbledMineYString() {
        assertTrue(invalidInputExpected(TESTS_FOR_GAME_SAVE_FOLDER_PATH + "gameSaveFileTestsGarbledMineYString.json"));
    }

    @Test
    public void testReadWorldGarbledMineYStringEnd() {
        assertTrue(invalidInputExpected(TESTS_FOR_GAME_SAVE_FOLDER_PATH + "gameSaveFileTestsGarbledMineYStringEnd.json"));
    }

    @Test
    public void testReadWorldGarbledPointBracketOrderWrong() {
        assertTrue(invalidInputExpected(TESTS_FOR_GAME_SAVE_FOLDER_PATH + "gameSaveFileTestsGarbledPointBracketOrderWrong.json"));
    }

    @Test
    public void testReadWorldGarbledPointCoordXOrderWrong() {
        assertTrue(invalidInputExpected(TESTS_FOR_GAME_SAVE_FOLDER_PATH + "gameSaveFileTestsGarbledPointXCoordOrderWrong.json"));
    }

    // EFFECTS: tests whether a given path throws InvalidInputException and returns true if it does; else return false
    private boolean invalidInputExpected(String path) {
        try {
            reader.readWorld(path);
            return false;
        } catch (InvalidInputException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // EFFECTS: repopulates the file gameSaveFileTests.json so that intellij won't complain about pushing blank file
    @AfterAll
    static void repopulateGameSaveFileTests() {
        GameWorld originalWorld = new GameWorld(GameWorld.MAGE_DEFAULT_NAME, GameWorld.WORLD_DEFAULT_NAME);
        SaveGameWorldWriter writer = new SaveGameWorldWriter();
        try {
            writer.saveWorld(SAVE_PATH_FOR_TESTS, originalWorld.getMage().getMageName(), originalWorld.getWorldName(),
                    originalWorld.getNumDistrictsMade(), originalWorld.getDistricts());
        } catch (FileNotFoundException e) {
            fail();
        }
    }
}
