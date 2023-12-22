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
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

public class UpdateXPTask extends RecurringTask {
    private static final long INTERVAL = 30 * 1000;

    private final XPManager xpManager;
    private final String guildName;
    private final URI uri;

    private final UUID uuid = MinecraftClient.getInstance().getSession().getUuidOrNull();

    public UpdateXPTask(XPManager xpManager) throws IOException, InterruptedException, URISyntaxException {
        super(INTERVAL, false);

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
                    .getAsString();
        } catch (IOException | InterruptedException | IllegalStateException | IndexOutOfBoundsException e) {
            WynnUtilityMod.LOGGER.info("Failed to get guild name, XP functionality will be disabled.");
            throw e;
        }

        try {
            this.uri = new URI("https",
                    "api.wynncraft.com",
                    "/v3/guild/" + guildName,
                    "identifier=uuid",
                    null);
        } catch (URISyntaxException e) {
            WynnUtilityMod.LOGGER.info("Failed to get guild name, XP functionality will be disabled.");
            throw e;
        }
        this.xpManager = xpManager;

        this.run();
    }

    @Override
    protected void run() {
        if (this.uuid == null) {
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .build();

        JsonObject members;
        try {
            var body = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString())
                    .body();
            members = JsonParser.parseString(body).getAsJsonObject().getAsJsonObject("members");
        } catch (IOException | InterruptedException | IllegalStateException e) {
            e.printStackTrace();
            return;
        }

        for (Map.Entry<String, JsonElement> rankEntry : members.entrySet()) {
            if (rankEntry.getKey().equals("total")) continue;

            JsonObject rankedMembers = rankEntry.getValue().getAsJsonObject();
            for (Map.Entry<String, JsonElement> memberEntry : rankedMembers.entrySet()) {
                JsonObject member = memberEntry.getValue().getAsJsonObject();
                String uuid = member.get("uuid").getAsString();
                if (uuid.equals(this.uuid.toString())) {
                    int xp = member.get("contributed").getAsInt();
                    this.xpManager.updateXP(xp);
                    return;
                }
            }
        }
    }
}
