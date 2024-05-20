package io.github.greatericontop.greatcrafts.gui;

/*
 * Copyright (C) 2024-present greateric.
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
import io.github.greatericontop.greatcrafts.internal.RecipeManager;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager {

    public final Map<UUID, Map<String, Object>> guiData = new HashMap<>();
    public final Map<UUID, Inventory> playerMainInventories = new HashMap<>();

    private final GreatCrafts plugin;
    public GreatCrafts getPlugin() {
        return plugin;
    }

    public GUIManager(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    public RecipeManager getRecipeManager() {
        return plugin.recipeManager;
    }

}
