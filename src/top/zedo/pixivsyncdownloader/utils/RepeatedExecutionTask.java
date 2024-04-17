package top.zedo.pixivsyncdownloader.utils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class RepeatedExecutionTask {
    private static final int MAX_SAMPLE_SIZE = 100;

    private final ThreadPoolExecutor executor;
    private long targetTimes;
    private int nThreads;


    private float speed = 0;
    private long currentCount;
    private long startTime;
    private long finishedCount;
    private long totalExecutionTime;
    private final Queue<Long> executionTimes = new ConcurrentLinkedQueue<>();

    public RepeatedExecutionTask(int nThreads, long targetTimes) {
        this.nThreads = nThreads;
        this.targetTimes = targetTimes;
        executor = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    public void run() {

        startTime = System.currentTimeMillis();
        for (int i = 0; i < targetTimes; i++) {
            if (executor.getActiveCount() >= nThreads)
                try {
                    synchronized (executionTimes) {
                        executionTimes.wait(1000);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


            executor.submit(runnable);

        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //log(targetTimes, finishedCount, speed, System.currentTimeMillis() - startTime, 0);

    }

    private final Runnable runnable = () -> {
        try {
            long index;
            long runStartTime, runEndTime, elapsedTime;
            synchronized (executionTimes) {
                index = currentCount++;
            }
            runStartTime = System.currentTimeMillis();

            run(index);

            runEndTime = System.currentTimeMillis();
            elapsedTime = runEndTime - runStartTime;


            synchronized (executionTimes) {
                finishedCount++;
                totalExecutionTime += elapsedTime;
                executionTimes.offer(elapsedTime);
                if (executionTimes.size() > MAX_SAMPLE_SIZE) {
                    totalExecutionTime -= executionTimes.poll();
                }
                int sampleSize = Math.min(executionTimes.size(), MAX_SAMPLE_SIZE);
                speed = 1000f / ((float) totalExecutionTime / sampleSize) * nThreads;
                log(targetTimes,
                        finishedCount,
                        (float) finishedCount / targetTimes, speed,
                        System.currentTimeMillis() - startTime,
                        (long) ((targetTimes - finishedCount) / speed)*1000);

                executionTimes.notify();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };


    protected abstract void run(long index);

    protected abstract void log(long targetTimes, long finishedCount, float progress, float speed, long timeDuration, long timeRemaining);
}
