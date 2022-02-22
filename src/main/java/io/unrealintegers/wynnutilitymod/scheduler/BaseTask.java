package io.unrealintegers.wynnutilitymod.scheduler;

public abstract class BaseTask {
    protected boolean isFinished = false;

    abstract protected void cancel();

    abstract protected void run();

    abstract protected void tick();
}
