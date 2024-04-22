import top.zedo.pixivsyncdownloader.utils.DynamicConsoleOutput;
import top.zedo.pixivsyncdownloader.utils.RepeatedExecutionTask;

import java.util.Random;

import static top.zedo.pixivsyncdownloader.utils.Format.formatTimestamp;

public class RepeatedExecutionTaskTest {
    static int a = 0;
    static long t = 0;

    public static void main(String[] args) {
        DynamicConsoleOutput output = new DynamicConsoleOutput();
        RepeatedExecutionTask task = new RepeatedExecutionTask(400, 20000) {
            @Override
            protected void run(long index) {
                try {
                    Thread.sleep(Math.abs(new Random().nextInt() % 2000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void log(long targetTimes, long finishedCount, float progress, float speed, long timeDuration, long timeRemaining) {
                if (!(t + 200 < System.currentTimeMillis())) {
                    return;
                }
                a++;

                output.appendFormat("进度: [%d/%d]    %.2f%% 已用时间: %s 剩余时间: %s 速度: %.2f/s",
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
                output.appendFormat("  [%s%s%s]", "=".repeat(len), fillChar, fillStr);
                output.appendString("   " + a);
                output.update();


                if (t + 200 < System.currentTimeMillis()) {
                    t = System.currentTimeMillis();
                    a = 0;
                }
            }
        };
        task.run();
    }
}
