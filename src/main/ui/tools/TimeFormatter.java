package ui.tools;

public class TimeFormatter {
    // Note: source for "%02d" is at:
    // https://stackoverflow.com/questions/12421444/how-to-format-a-number-0-9-to-display-with-2-digits-its-not-a-date
    // EFFECTS: turns a time in milliseconds to a formatted string
    public static String formatMSAsTime(int time) {
        int timeLeftInSeconds = time / 1000;
        int mins = timeLeftInSeconds / 60;
        int secs = timeLeftInSeconds % 60;
        return String.format("%02d", mins) + ":" + String.format("%02d", secs);
    }
}
