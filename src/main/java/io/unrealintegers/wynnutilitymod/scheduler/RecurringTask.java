package io.unrealintegers.wynnutilitymod.scheduler;

public abstract class RecurringTask extends BaseTask {
    protected long interval;

    private long startTime;

    public RecurringTask(long interval, boolean runNow) {
        this.interval = interval;

        this.startTime = System.currentTimeMillis();

        if (runNow) {
            run();
        }
    }

    protected void cancel() {
        this.isFinished = true;
    }

    @Override
    protected void tick() {
        if (!isFinished && System.currentTimeMillis() - startTime >= interval) {
            run();
            this.startTime = System.currentTimeMillis();
        }
    }
}
