package io.unrealintegers.wynnutilitymod.models;

import com.google.common.base.Functions;
import com.google.gson.*;
import io.unrealintegers.wynnutilitymod.WynnUtilityMod;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

import java.awt.*;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Territory implements Serializable {
    public static class Deserializer implements JsonDeserializer<Territory> {
        @Override
        public Territory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonObject resourcesObject = jsonObject.getAsJsonObject("resources");
            JsonArray connectionsArray = jsonObject.getAsJsonArray("connections");

            EnumMap<ResourceType, Integer> baseProductions = new EnumMap<>(ResourceType.class);
            List<String> connections = new ArrayList<>();

            for (Map.Entry<String, JsonElement> entry : resourcesObject.entrySet()) {
                ResourceType key = ResourceType.valueOf(entry.getKey().toUpperCase(Locale.ROOT));
                baseProductions.put(key, entry.getValue().getAsInt());
            }

            for (int i = 0; i < connectionsArray.size(); i++) {
                connections.add(connectionsArray.get(i).getAsString());
            }

            return new Territory(jsonObject.get("name").getAsString(), baseProductions, connections);
        }
    }

    private static class AdvancementPatterns {
        private static final Pattern GUILD_PATTERN = Pattern.compile("(?<name>[A-Za-z ]+) \\[(?<tag>[A-Za-z]{3,4})]");
        private static final List<Pattern> RESOURCE_PATTERNS = Stream.of(ResourceType.values()).map((TYPE) -> {
            Character symbol = TYPE.symbol;
            String prefix = (symbol == null) ? "" : symbol + " ";
            Pattern generationPattern = Pattern.compile(prefix + "\\+(?<generated>\\d+) \\w+ per Hour",
                    Pattern.CASE_INSENSITIVE);
            Pattern storagePattern = Pattern.compile(prefix + "(?<current>\\d+)/(?<max>\\d+) stored",
                    Pattern.CASE_INSENSITIVE);
            return Stream.of(generationPattern, storagePattern);
        }).flatMap(Functions.identity()).toList();
        private static final Pattern TREASURY_PATTERN = Pattern.compile(".* Treasury: .*(?<treasury>(Very )?(Low|Medium|High))");
        private static final Pattern DEFENCES_PATTERN = Pattern.compile("Territory Defences: .*(?<def>(Very )?(Low|Medium|High))");
    }

    private static final Pattern treasuryPattern = Pattern.compile(".*?Treasury Bonus:(\\s|§[A-Fa-f0-9K-Ok-oRr])*(?<bonus>[\\d.]+)%");
    private static final Pattern upgradePattern = Pattern.compile("-(\\s|§[A-Fa-f0-9K-Ok-oRr])*(?<name>[A-Za-z\\- ]+?)(\\s|§[A-Fa-f0-9K-Ok-oRr])*\\[Lv. (?<level>\\d+)]");


    private final String name;
    public boolean isHQ;
    public int distanceToHQ = -1;
    private String owner;

    private float treasuryBonusPct = 0.0f;
    public TerritoryUpgrades upgrades;
    public final TerritoryResources resources;
    private final List<String> connections;
    private final Map<String, Integer> distances;
    private final Map<String, Integer> guildDistances;


    public Territory(String name, EnumMap<ResourceType, Integer> baseProductions, List<String> connections) {
        this.name = name;
        this.isHQ = false;
        this.upgrades = new TerritoryUpgrades();
        this.resources = new TerritoryResources(baseProductions);
        this.connections = connections;
        this.distances = new HashMap<>();
        this.guildDistances = new HashMap<>();
    }

    public void parseItemText(List<Text> textLines) {
        // [,
        // �7�a+10800 Emeralds per Hour,
        // �7�a24/3000 stored,
        // �7�f? 19/300 stored,
        // �7�6? 17/300 stored,
        // �7�b? +4320 Fish per Hour,
        // �7�b? 26/300 stored,
        // �7�e? 15/300 stored,
        // ,
        // �d? Treasury Bonus: �f20%,
        // ,
        // �dUpgrades:,
        // �d- �7Damage�8 [Lv. 4],
        // �d- �7Attack�8 [Lv. 2],
        // �d- �7Health�8 [Lv. 4],
        // �d- �7Defence�8 [Lv. 4],
        // �d- �7Tower Aura�8 [Lv. 1],
        // �d- �7Tower Volley�8 [Lv. 1],
        // ,
        // �7Left-Click to view territory,
        // �7Right-Click to transfer away]

        List<String> lines = textLines.stream().map(Text::getString).collect(Collectors.toList());

        upgrades.reset();

        Iterator<String> it = lines.listIterator();
        for (String line = it.next(); it.hasNext(); line = it.next()) {
            if (line.contains("Treasury")) {
                Matcher treasuryMatcher = treasuryPattern.matcher(line);
                if (treasuryMatcher.find()) {
                    treasuryBonusPct = Float.parseFloat(treasuryMatcher.group("bonus"));
                } else {
                    WynnUtilityMod.LOGGER.warn("Treasury match error on string <" + line + ">.");
                }
                break;
            }
        }
        if (!it.hasNext()) {
            // Treasury not found, set it to 0 and reset iterator
            treasuryBonusPct = 0f;
            it = lines.listIterator();
        }

        while (it.hasNext() && !it.next().contains("Upgrades")) ; // Skip until we find the upgrades
        if (!it.hasNext()) return;  // If there is no upgrades

        for (String line = it.next(); !line.isEmpty() && it.hasNext(); line = it.next()) {
            Matcher upgradeMatcher = upgradePattern.matcher(line);
            if (upgradeMatcher.find()) {
                TerritoryUpgradeType type = TerritoryUpgradeType.fromGameName(upgradeMatcher.group("name"));
                int level = Integer.parseInt(upgradeMatcher.group("level"));
                if (type == null) {
                    WynnUtilityMod.LOGGER.warn("Match error on name <" + upgradeMatcher.group("name") + ">.");
                } else {
                    upgrades.set(type, level);
                }
            } else {
                WynnUtilityMod.LOGGER.warn("Upgrades match error on string <" + line + ">.");
            }
        }

    }

    public void parseAdvancementDisplay(AdvancementDisplay display) {
        if (display.getFrame() == AdvancementFrame.CHALLENGE) {
            this.isHQ = true;
        }

        String description = display.getDescription().getString();

        WynnUtilityMod.LOGGER.info(description);

        // Remove Formatting
        description = description.replaceAll("§[0-9a-f]", "");
        String[] lines = description.split("\n");

        // Line 0: Guild
        Matcher matcher = AdvancementPatterns.GUILD_PATTERN.matcher(lines[0]);
        if (matcher.find()) {
            this.owner = matcher.group("tag");
        } else {
            this.owner = "null";
        }

        // Line 2 -- I: Resources
        int i = 2, resourceIndex = -1;
        for (Pattern pattern : AdvancementPatterns.RESOURCE_PATTERNS) {
            resourceIndex++;

            if (i >= lines.length) {
                break;
            }

            // Empty string means we reached the end of resource block
            String content = lines[i];
            if (content.equals("")) {
                break;
            }

            matcher = pattern.matcher(content);
            if (!matcher.find()) {
                // We try parsing this line with a different resource
                continue;
            }

//            // Little hack to get the resource we want
//            ResourceInfo resourceInfo = resources.getResource(ResourceType.values()[resourceIndex / 2]);
//
//            try {
//                int generated = Integer.parseInt(matcher.group("generated"));
//                resourceInfo.setCurrentProduction(generated);
//            } catch (IllegalStateException | IllegalArgumentException ignored) {
//            }
//
//            try {
//                int current = Integer.parseInt(matcher.group("current"));
//                resourceInfo.setCurrentLevel(current);
//            } catch (IllegalStateException | IllegalArgumentException ignored) {
//            }
//
//            try {
//                int max = Integer.parseInt(matcher.group("max"));
//                resourceInfo.setMaxLevel(max);
//            } catch (IllegalStateException | IllegalArgumentException ignored) {
//            }

            i++;
        }

        // Line I+2: Treasury
        // We should be on line i+1 so we simply increment
        i++;
//        matcher = AdvancementPatterns.TREASURY_PATTERN.matcher(lines[i]);
//        if (matcher.find()) {
//            String treasury = matcher.group("treasury");
//            this.treasury = TreasuryLevel.parse(treasury);
//        }

        // Line I+3: Defences
//        i++;
//        WynnUtilityMod.LOGGER.info(lines[i]);
//        matcher = AdvancementPatterns.DEFENCES_PATTERN.matcher(lines[i]);
//        if (matcher.find()) {
//            String def = matcher.group("def");
//            this.defence = DefenceLevel.parse(def);
//            WynnUtilityMod.LOGGER.log(def);
//        }

        // Lines I+5 -- end: Connections
    }

    public String getName() {
        return this.name;
    }

    public List<String> getConnections() {
        return this.connections;
    }

    public int getDistance(Territory other) {
        return getDistance(other.getName());
    }

    public int getDistance(String other) {
        return distances.getOrDefault(other, -1);
    }

    public int getGuildDistance(Territory other) {
        return getGuildDistance(other.getName());
    }

    public int getGuildDistance(String other) {
        return guildDistances.getOrDefault(other, -1);
    }

    public String getOwner() {
        return this.owner;
    }

    public float getTreasuryPct() {
        return this.treasuryBonusPct;
    }

    public TerritoryProductionType getPrimaryProduction() {
        TerritoryProductionType resourceType = null;
        for (ResourceType type : ResourceType.NON_EMERALD_TYPES) {
            if (resources.getResource(type).getBaseProduction() > 0) {
                if (resourceType != null) {
                    return TerritoryProductionType.OASIS;
                }
                resourceType = TerritoryProductionType.fromResourceType(type);
            }
        }

        return resourceType;
    }

    public DefenceLevel getDefenceLevel() {
        int sumLevels = upgrades.get(TerritoryUpgradeType.TOWER_DAMAGE, 0) + upgrades.get(TerritoryUpgradeType.TOWER_ATTACK, 0) + upgrades.get(TerritoryUpgradeType.TOWER_HEALTH, 0) + upgrades.get(TerritoryUpgradeType.TOWER_DEFENCE, 0);

        int aura = upgrades.get(TerritoryUpgradeType.TOWER_AURA, 0);
        int volley = upgrades.get(TerritoryUpgradeType.TOWER_VOLLEY, 0);

        sumLevels += aura > 0 ? aura : -5;
        sumLevels += volley > 0 ? volley : -3;

        if (!isHQ) {
            if (sumLevels < -2) {
                return DefenceLevel.VERY_LOW;
            } else if (sumLevels < 11) {
                return DefenceLevel.LOW;
            } else if (sumLevels < 23) {
                return DefenceLevel.MEDIUM;
            } else if (sumLevels < 41) {
                return DefenceLevel.HIGH;
            } else {
                return DefenceLevel.VERY_HIGH;
            }
        } else if (sumLevels < -8) {
            return DefenceLevel.VERY_LOW;
        } else if (sumLevels < 0) {
            return DefenceLevel.LOW;
        } else if (sumLevels < 10) {
            return DefenceLevel.MEDIUM;
        } else if (sumLevels < 22) {
            return DefenceLevel.HIGH;
        } else {
            return DefenceLevel.VERY_HIGH;
        }
    }

    public boolean isCity() {
        return resources.getResource(ResourceType.EMERALDS).getBaseProduction() > 9000;
    }

    public boolean isDouble() {
        ResourceType primaryProduction = ResourceType.fromProductionType(getPrimaryProduction());
        if (primaryProduction == null) return false;
        return resources.getResource(primaryProduction).getBaseProduction() > 3600;
    }

    public void setDistances(Map<String, Integer> distances) {
        this.distances.putAll(distances);
    }

    public void setGuildDistances(Map<String, Integer> guildDistances) {
        this.guildDistances.putAll(guildDistances);
    }

    public Map<String, Integer> bfs() {
        return bfs((territory, distance) -> true);
    }

    public Map<String, Integer> bfs(BiPredicate<Territory, Integer> check) {
        Queue<Pair<Territory, Integer>> territoryQueue = new ArrayDeque<>();
        Map<String, Integer> distances = new HashMap<>();

        territoryQueue.add(new Pair<>(this, 0));

        while (!territoryQueue.isEmpty()) {
            var pair = territoryQueue.remove();
            Territory territory = pair.getLeft();
            int distance = pair.getRight();

            if (!check.test(territory, distance)) {
                continue;
            }

            if (distances.containsKey(territory.getName())) {
                continue;
            }

            distances.put(territory.getName(), distance);

            for (String connection : territory.getConnections()) {
                // If it has been visited
                if (distances.containsKey(connection)) {
                    continue;
                }

                territoryQueue.add(new Pair<>(Wynncraft.getTerritory(connection), distance + 1));
            }
        }

        return distances;
    }
}
