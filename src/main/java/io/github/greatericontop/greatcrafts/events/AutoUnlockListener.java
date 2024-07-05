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
            player.sendMessage(String.format("§3[§aGreat§bCrafts§3] §f%d §3new recipes were unlocked!", counter));
        }
    }

    // (setting = EACH / ONE) Item pickup check
    @EventHandler()
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (plugin.autoUnlockSetting != AutoUnlockSetting.EACH && plugin.autoUnlockSetting != AutoUnlockSetting.ONE)  return;
        Player player = event.getPlayer();
        ItemStack pickedUpItem = event.getItem().getItemStack();
        for (SavedRecipe rec : plugin.recipeManager.getAllSavedRecipes()) {
            boolean shouldUnlock = false;
            // Check if the picked up item matches any of the required items in the recipe
            outer:
            for (int slot = 0; slot < 9; slot++) {
                IngredientType type = rec.ingredientTypes()[slot];
                switch (type) {
                    case NORMAL -> {
                        ItemStack requiredItem = rec.items().get(slot);
                        if (requiredItem != null && requiredItem.getType() == pickedUpItem.getType()) {
                            shouldUnlock = true;
                            break outer;
                        }
                    }
                    case EXACT_CHOICE -> {
                        ItemStack requiredItem = rec.items().get(slot);
                        if (requiredItem != null && requiredItem.isSimilar(pickedUpItem)) {
                            shouldUnlock = true;
                            break outer;
                        }
                    }
                    case MATERIAL_CHOICE -> {
                        for (Material requiredMat : rec.materialChoiceExtra().get(slot)) {
                            if (requiredMat == pickedUpItem.getType()) {
                                shouldUnlock = true;
                                break outer;
                            }
                        }
                    }
                }
            }
            if (shouldUnlock) {
                // (setting = EACH) Check if we have all items before unlocking
                // TODO ...
                // Unlock
                // TODO: ...
            }

        }
    }

}
