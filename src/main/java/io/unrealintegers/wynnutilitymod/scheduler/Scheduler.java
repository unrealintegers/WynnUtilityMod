package io.unrealintegers.wynnutilitymod.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Scheduler {
    private static final ExecutorService taskExecutor = Executors.newFixedThreadPool(1);
    private static List<BaseTask> taskList = new ArrayList<>();

    public static void addTask(BaseTask task) {
        taskList.add(task);
    }

    public static void cancelTask(BaseTask task) {
        task.cancel();
        taskList.remove(task);
    }

    public static void reset() {
        taskList.forEach(BaseTask::cancel);
        taskList.clear();
    }

    public static void tick() {
        taskExecutor.execute(() -> taskList.forEach(BaseTask::tick));
        taskList = taskList.stream().filter(t -> !t.isFinished).collect(Collectors.toList());
    }
}
