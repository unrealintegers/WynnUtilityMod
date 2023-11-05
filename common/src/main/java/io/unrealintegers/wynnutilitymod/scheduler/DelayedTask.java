package io.unrealintegers.wynnutilitymod.scheduler;

public abstract class DelayedTask extends BaseTask {
    private final long delay;

    private final long startTime;

    public DelayedTask(long delay) {
        this.delay = delay;

        this.startTime = System.currentTimeMillis();
    }

    protected void tick() {
        if (System.currentTimeMillis() - startTime >= delay) {
            run();
            this.isFinished = true;
        }
    }
}
