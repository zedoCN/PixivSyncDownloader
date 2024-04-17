package top.zedo.pixivsyncdownloader.utils;

/**
 * 可重试任务
 */
public abstract class RetryableTask implements Runnable {
    private final int maxRetries;
    private final int retryInterval;
    private boolean failed = false;

    /**
     * 是否失败了
     *
     * @return 失败了
     */
    public boolean isFailed() {
        return failed;
    }

    /**
     * @param maxRetries    最多重试次数 <0为无限
     * @param retryInterval 重试间隔 ms
     */
    public RetryableTask(int maxRetries, int retryInterval) {
        this.maxRetries = maxRetries;
        this.retryInterval = retryInterval;
    }

    /**
     * 默认最多重试3次间隔100ms
     */
    public RetryableTask() {
        this(3, 100);
    }

    @Override
    public final void run() {
        int count = 0;
        while (true) {
            count++;
            try {
                runTask();
                failed = false;
                return;
            } catch (Exception e) {
                if (count >= maxRetries) {
                    failed = true;
                    failed(e);
                    return;
                }
                retry(count, e);
                if (retryInterval != 0)
                    try {
                        Thread.sleep(retryInterval);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
            }
        }
    }

    /**
     * 需要被执行的任务
     */
    protected abstract void runTask();

    /**
     * 遇到异常重试执行
     *
     * @param retries 重试次数
     * @param e       遇到的异常
     */
    protected abstract void retry(int retries, Exception e);

    /**
     * 达到最大重试次数 执行失败处理
     *
     * @param e 最终的异常
     */
    protected abstract void failed(Exception e);

}
