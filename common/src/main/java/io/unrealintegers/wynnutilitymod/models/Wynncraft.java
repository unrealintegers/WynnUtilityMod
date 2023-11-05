package io.unrealintegers.wynnutilitymod.models;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.unrealintegers.wynnutilitymod.WynnUtilityMod;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Wynncraft {
    private static Map<String, Territory> territories = new HashMap<>();
    private static boolean loaded = false;
    public static final ResourceReloader listener = (SynchronousResourceReloader) manager -> {
        // We don't want this to reload each time
        if (loaded) return;
        loaded = true;

        try {
            int updated = Wynncraft.reloadTerritories(manager);
            WynnUtilityMod.LOGGER.info("Loaded " + updated + " territories");
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public static Territory getTerritory(String name) {
        return territories.get(name);
    }

    public static Map<String, Territory> getTerritories() {
        return territories;
    }

    // TODO: Properly download territories
    public static int reloadTerritories(ResourceManager manager) throws IOException {
        Resource resource = manager.getResource(new Identifier("wynnutilitymod", "territories.json")).orElse(null);
        assert resource != null;

        InputStream stream = resource.getInputStream();
        Reader streamReader = new InputStreamReader(stream);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Territory.class, new Territory.Deserializer());
        Gson gson = builder.create();
        Type territoryMapType = new TypeToken<Map<String, Territory>>() {
        }.getType();

        territories = gson.fromJson(streamReader, territoryMapType);
        assert territories != null;

        for (Territory territory : territories.values()) {
            territory.setDistances(territory.bfs());
        }

        return territories.size();
    }
}