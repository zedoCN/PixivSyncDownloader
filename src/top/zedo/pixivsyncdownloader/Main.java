package top.zedo.pixivsyncdownloader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*DynamicConsoleOutput output = new DynamicConsoleOutput();
        RepeatedExecutionTask task = new RepeatedExecutionTask(1, 200) {
            @Override
            protected void run(long index) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void log(long targetTimes, long finishedCount, float progress, float speed, long timeDuration, long timeRemaining) {
                synchronized (System.out) {
                    output.appendFormat("进度: [%d/%d] \t\t %.2f%% 已用时间: %s 剩余时间: %s 速度: %.2f/s",
                            finishedCount,
                            targetTimes,
                            progress * 100,
                            formatTimestamp(timeDuration),
                            formatTimestamp(timeRemaining),
                            speed);
                    int maxLen = 10;
                    int len = (int) Math.floor(progress * maxLen);
                    int remainder = (int) (progress * 10 * maxLen) % 10;
                    String fillChar = remainder == 0 ? "" : String.valueOf(remainder);
                    String fillStr = "-".repeat(remainder == 0 ? maxLen - len : maxLen - len - 1);
                    output.appendFormat("\t\t[%s%s%s]", "=".repeat(len), fillChar, fillStr);

                    output.update();
                }
            }
        };
        task.run();*/
        List<String> argList = Arrays.asList(args);

        if (argList.contains("-webui")) {
            WebUIServer.start(Config.DATA.serverPort);
        }
    }

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
