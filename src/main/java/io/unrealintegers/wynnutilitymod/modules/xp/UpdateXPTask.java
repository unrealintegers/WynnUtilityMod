package io.unrealintegers.wynnutilitymod.modules.xp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.unrealintegers.wynnutilitymod.WynnUtilityMod;
import io.unrealintegers.wynnutilitymod.scheduler.RecurringTask;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class UpdateXPTask extends RecurringTask {
    private static final long INTERVAL = 30 * 1000;

    private final XPManager xpManager;
    private final String guildName;
    private final String url;

    public UpdateXPTask(XPManager xpManager) throws IOException, InterruptedException {
        super(INTERVAL, false);

        UUID uuid = MinecraftClient.getInstance().getSession().getProfile().getId();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.wynncraft.com/v2/player/" + uuid.toString() + "/stats"))
                .build();
        try {
            var body = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString())
                    .body();
            this.guildName = JsonParser.parseString(body)
                    .getAsJsonObject()
                    .getAsJsonArray("data")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("guild")
                    .get("name")
                    .getAsString()
                    .replace(' ', '+');
        } catch (IOException | InterruptedException | IllegalStateException | IndexOutOfBoundsException e) {
            WynnUtilityMod.LOGGER.info("Failed to get guild name, XP functionality will be disabled.");
            throw e;
        }

        this.url = "https://api.wynncraft.com/public_api.php?action=guildStats&command=" + guildName;
        this.xpManager = xpManager;

        this.run();
    }

    @Override
    protected void run() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        JsonArray membersArray;
        try {
            var body = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString())
                    .body();
            membersArray = JsonParser.parseString(body).getAsJsonObject().getAsJsonArray("members");
        } catch (IOException | InterruptedException | IllegalStateException e) {
            e.printStackTrace();
            return;
        }

        for (JsonElement member : membersArray) {
            JsonObject memberObject = member.getAsJsonObject();
            if (memberObject.get("name").getAsString().equals(MinecraftClient.getInstance().getSession().getProfile().getName())) {
                long xp = memberObject.get("contributed").getAsLong();
                xpManager.updateXP(xp);
                return;
            }
        }
    }
}
