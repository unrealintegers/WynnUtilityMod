package io.unrealintegers.wynnutilitymod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.unrealintegers.wynnutilitymod.model.Player;
import io.unrealintegers.wynnutilitymod.modules.tower.TowerManager;
import io.unrealintegers.wynnutilitymod.modules.xp.XPManager;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;


@Mod(modid = WUMMod.MODID, name = WUMMod.NAME, version = WUMMod.VERSION)
public class WUMMod {
    public static final String MODID = "wynnutilitymod";
    public static final String NAME = "Wynn Utility Mod";
    public static final String VERSION = "1.0";

    private static final XPManager xpManager = new XPManager();
    private static final TowerManager towerManager = new TowerManager();

    public static XPManager getXpManager() {
        return xpManager;
    }

    public static TowerManager getTowerManager() {
        return towerManager;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        KeybindManager.init();

        InputStreamReader responseReader;
        try {
            InputStream stream = new URL("https://api.wynncraft.com/v2/player/" + Player.uuid + "/stats").openStream();
            responseReader = new InputStreamReader(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            JsonObject data = new JsonParser().parse(responseReader).getAsJsonObject().getAsJsonArray("data").get(0).getAsJsonObject();
            Player.guildName = data.getAsJsonObject("guild").get("name").getAsString();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }
}
