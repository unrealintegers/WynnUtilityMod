package io.unrealintegers.wynnutilitymod.modules.xp;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class XPManager {
    private Long avg1m, avg5m, avg15m;
    private final Map<Long, Long> xpStates = new TreeMap<>(Collections.reverseOrder());

    void updateXP(long xp) {
        long time = System.currentTimeMillis();
        // 59 instead of 60 to allow for API request time
        avg1m = xpStates.entrySet().stream().filter(e -> time - e.getKey() >= 59 * 1000)
                .findFirst().map(e -> (xp - e.getValue()) * 3600 * 1000 / (time - e.getKey())).orElse(null);
        avg5m = xpStates.entrySet().stream().filter(e -> time - e.getKey() >= 5 * 59 * 1000)
                .findFirst().map(e -> (xp - e.getValue()) * 3600 * 1000 / (time - e.getKey())).orElse(null);
        avg15m = xpStates.entrySet().stream().filter(e -> time - e.getKey() >= 15 * 59 * 1000)
                .findFirst().map(e -> (xp - e.getValue()) * 3600 * 1000 / (time - e.getKey())).orElse(null);

        xpStates.entrySet().removeIf(e -> e.getKey() < time - 17 * 60 * 1000);
        xpStates.put(time, xp);
    }

    public Long getAvgXP1m() {
        return avg1m;
    }

    public Long getAvgXP5m() {
        return avg5m;
    }

    public Long getAvgXP15m() {
        return avg15m;
    }
}
