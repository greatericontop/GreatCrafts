package io.github.greatericontop.greatcrafts.events;

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
import io.github.greatericontop.greatcrafts.internal.datastructures.AutoUnlockSetting;
import io.github.greatericontop.greatcrafts.internal.datastructures.IngredientType;
import io.github.greatericontop.greatcrafts.internal.datastructures.SavedRecipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class AutoUnlockListener implements Listener {

    private final GreatCrafts plugin;
    public AutoUnlockListener(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    // (setting = ALWAYS) Unlock on player join
    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.autoUnlockSetting != AutoUnlockSetting.ALWAYS)  return;
        Player player = event.getPlayer();
        int counter = 0;
        for (SavedRecipe rec : plugin.recipeManager.getAllSavedRecipes()) {
            if (player.discoverRecipe(rec.key())) {
                counter++;
            }
        }
        if (counter > 0) {
            player.sendMessage(String.format("§a[Great§bCrafts] §f%d §3new recipes were unlocked!", counter));
        }
    }

    // (setting = EACH / ONE) Item pickup check
    @EventHandler()
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (plugin.autoUnlockSetting != AutoUnlockSetting.EACH && plugin.autoUnlockSetting != AutoUnlockSetting.ONE)  return;
        Player player = event.getPlayer();
        ItemStack pickedUpItem = event.getItem().getItemStack();
        for (SavedRecipe rec : plugin.recipeManager.getAllSavedRecipes()) {
            if (player.hasDiscoveredRecipe(rec.key()))  continue;
            boolean shouldUnlock = false;
            boolean shouldUnlockEach = true;
            // Check if the picked up item matches any of the required items in the recipe
            for (int slot = 0; slot < 9; slot++) {
                IngredientType type = rec.ingredientTypes()[slot];
                switch (type) {
                    case NORMAL -> {
                        ItemStack requiredItem = rec.items().get(slot);
                        if (requiredItem != null && requiredItem.getType() == pickedUpItem.getType()) {
                            shouldUnlock = true;
                        } else {
                            shouldUnlockEach = false;
                        }
                    }
                    case EXACT_CHOICE -> {
                        ItemStack requiredItem = rec.items().get(slot);
                        if (requiredItem != null && requiredItem.isSimilar(pickedUpItem)) {
                            shouldUnlock = true;
                        } else {
                            shouldUnlockEach = false;
                        }
                    }
                    case MATERIAL_CHOICE -> {
                        boolean match = false;
                        for (Material requiredMat : rec.materialChoiceExtra().get(slot)) {
                            if (requiredMat == pickedUpItem.getType()) {
                                match = true;
                                shouldUnlock = true;
                            }
                        }
                        if (!match) {
                            shouldUnlockEach = false;
                        }
                    }
                }
            }
            if (plugin.autoUnlockSetting == AutoUnlockSetting.EACH) {
                if ((!shouldUnlock) && shouldUnlockEach)  throw new RuntimeException();
                if (shouldUnlockEach) {
                    player.discoverRecipe(rec.key());
                    player.sendMessage("§a[Great§bCrafts] §3You have all the ingredients used in a new recipe! Check the recipe book for more!");
                }
            } else { // AutoUnlockSetting.ONE
                if (shouldUnlock) {
                    player.discoverRecipe(rec.key());
                    player.sendMessage("§a[Great§bCrafts] §3You picked up an item used in a new recipe! Check the recipe book for more!");
                }
            }
        }
    }

}
