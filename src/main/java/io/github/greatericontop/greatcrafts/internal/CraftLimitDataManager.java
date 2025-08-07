package io.github.greatericontop.greatcrafts.internal;

/*
 * Copyright (C) 2023-present greateric.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty  of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import io.github.greatericontop.greatcrafts.GreatCrafts;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CraftLimitDataManager {
    private static final String DATAFILE = "craft_counts_save.yml";

    public static Map<UUID, Map<String, Integer>> fromYamlConfiguration(GreatCrafts plugin) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), DATAFILE));
        Map<UUID, Map<String, Integer>> playerCraftCounts = new HashMap<>();
        for (String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            Map<String, Integer> craftCounts = new HashMap<>();
            ConfigurationSection section = config.getConfigurationSection(key);
            for (String craftKey : section.getKeys(false)) {
                int count = section.getInt(craftKey);
                craftCounts.put(craftKey, count);
            }
            playerCraftCounts.put(uuid, craftCounts);
        }
        return playerCraftCounts;
    }

    public static void saveToYamlConfiguration(GreatCrafts plugin, Map<UUID, Map<String, Integer>> playerCraftCounts) {
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Integer>> entry : playerCraftCounts.entrySet()) {
            UUID uuid = entry.getKey();
            Map<String, Integer> craftCounts = entry.getValue();
            ConfigurationSection section = config.createSection(uuid.toString());
            for (Map.Entry<String, Integer> craftEntry : craftCounts.entrySet()) {
                section.set(craftEntry.getKey(), craftEntry.getValue());
            }
        }
        try {
            config.save(new File(plugin.getDataFolder(), DATAFILE));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save craft counts due to IOException");
            e.printStackTrace();
        }
    }

}
