package io.unrealintegers.wynnutilitymod.modules.xp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.unrealintegers.wynnutilitymod.model.Player;
import io.unrealintegers.wynnutilitymod.scheduler.RecurringTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateXPTask extends RecurringTask {
    private static final long INTERVAL = 30 * 1000;

    private final XPManager xpManager;
    private final String url;

    public UpdateXPTask(XPManager xpManager) {
        super(INTERVAL, false);

        String formattedName = Player.guildName.replace(' ', '+');
        this.url = "https://api.wynncraft.com/public_api.php?action=guildStats&command=" + formattedName;
        this.xpManager = xpManager;

        this.run();
    }

    @Override
    protected void run() {
        InputStreamReader responseReader;
        try {
            InputStream stream = new URL(this.url).openStream();
            responseReader = new InputStreamReader(stream);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return;
        }

        JsonArray membersArray = new JsonParser().parse(responseReader).getAsJsonObject().getAsJsonArray("members");
        for (JsonElement member : membersArray) {
            JsonObject memberObject = member.getAsJsonObject();
            if (memberObject.get("name").getAsString().equals(Player.name)) {
                long xp = memberObject.get("contributed").getAsLong();
                xpManager.updateXP(xp);
                return;
            }
        }
    }
}
