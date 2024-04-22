package top.zedo.pixivsyncdownloader.utils;

import java.util.ArrayList;
import java.util.List;

public class Format {
    public static String formatTimestamp(long milliseconds) {
        long days = milliseconds / (1000 * 60 * 60 * 24);
        long hours = (milliseconds % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (milliseconds % (1000 * 60)) / 1000;

        List<String> formattedTimeParts = new ArrayList<>();

        if (days > 0) {
            formattedTimeParts.add(days + "d");
        }

        if (hours > 0) {
            formattedTimeParts.add(hours + "h");
        }

        if (minutes > 0) {
            formattedTimeParts.add(minutes + "m");
        }

        if (seconds > 0 || formattedTimeParts.isEmpty()) {
            formattedTimeParts.add(seconds + "s");
        }

        return String.join("", formattedTimeParts);
    }

}
