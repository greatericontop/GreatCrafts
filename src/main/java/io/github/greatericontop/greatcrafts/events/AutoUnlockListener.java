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

import java.util.ArrayList;
import java.util.List;

public class AutoUnlockListener implements Listener {

    private final GreatCrafts plugin;
    public AutoUnlockListener(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    private boolean playerHasPermission(Player player, SavedRecipe rec) {
        String permission = plugin.recipePermissionRequirements.get(rec.key().toString());
        if (permission == null) {
            return true;
        }
        return player.hasPermission(permission);
    }

    // (setting = ALWAYS) Unlock on player join
    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int counter = 0;
        for (SavedRecipe rec : plugin.recipeManager.getAllSavedRecipes()) {
            AutoUnlockSetting setting = plugin.autoUnlockExceptions.getOrDefault(rec.key().toString(), plugin.autoUnlockSetting);
            if (setting == AutoUnlockSetting.ALWAYS && playerHasPermission(player, rec)) {
                if (player.discoverRecipe(rec.key())) {
                    counter++;
                }
            }
        }
        if (counter > 0) {
            plugin.languager.notifyAutoUnlockOnJoin(player, counter);
        }
    }

    // (setting = EACH / ONE) Item pickup check
    @EventHandler()
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack pickedUpItem = event.getItem().getItemStack();
        for (SavedRecipe rec : plugin.recipeManager.getAllSavedRecipes()) {
            if (player.hasDiscoveredRecipe(rec.key()))  continue;
            if (!playerHasPermission(player, rec))  continue;
            AutoUnlockSetting setting = plugin.autoUnlockExceptions.getOrDefault(rec.key().toString(), plugin.autoUnlockSetting);

            if (plugin.autoUnlockSetting == AutoUnlockSetting.EACH) {
                boolean shouldUnlockEach = true;
                // The picked up item hasn't been added to the inventory yet, so need to make our own
                List<ItemStack> invContents = new ArrayList<>(60);
                for (ItemStack invItem : player.getInventory().getContents()) {
                    if (invItem != null) {
                        invContents.add(invItem);
                    }
                }
                invContents.add(pickedUpItem);
                // Check that every ingredient is satisfied
                outer:
                for (int slot = 0; slot < 9; slot++) {
                    IngredientType type = rec.ingredientTypes()[slot];
                    switch (type) {
                        case NORMAL -> {
                            ItemStack requiredItem = rec.items().get(slot);
                            if (requiredItem == null || requiredItem.getType() == Material.AIR) {
                                continue outer; // Empty requirement
                            }
                            boolean matched = false;
                            primary:
                            for (ItemStack invItem : invContents) {
                                if (invItem != null && invItem.getType() == requiredItem.getType()) {
                                    matched = true;
                                    break primary;
                                }
                            }
                            if (!matched) {
                                shouldUnlockEach = false;
                                break outer;
                            }
                        }
                        case EXACT_CHOICE -> {
                            ItemStack requiredItem = rec.items().get(slot);
                            if (requiredItem == null || requiredItem.getType() == Material.AIR) {
                                continue outer; // Empty requirement
                            }
                            boolean matched = false;
                            primary:
                            for (ItemStack invItem : invContents) {
                                if (invItem != null && invItem.isSimilar(requiredItem)) {
                                    matched = true;
                                    break primary;
                                }
                            }
                            if (!matched) {
                                shouldUnlockEach = false;
                                break outer;
                            }
                        }
                        case MATERIAL_CHOICE -> {
                            boolean matched = false;
                            primary:
                            for (Material requiredMat : rec.materialChoiceExtra().get(slot)) {
                                for (ItemStack invItem : invContents) {
                                    if (invItem != null && invItem.getType() == requiredMat) {
                                        matched = true;
                                        break primary;
                                    }
                                }
                            }
                            if (!matched) {
                                shouldUnlockEach = false;
                                break outer;
                            }
                        }
                    }
                }
                if (shouldUnlockEach) {
                    player.discoverRecipe(rec.key());
                    plugin.languager.notifyAutoUnlockEach(player);
                }
            } else if (setting == AutoUnlockSetting.ONE) {
                boolean hasAtLeastOne = false;
                outer:
                for (int slot = 0; slot < 9; slot++) {
                    IngredientType type = rec.ingredientTypes()[slot];
                    switch (type) {
                        case NORMAL -> {
                            ItemStack requiredItem = rec.items().get(slot);
                            if (requiredItem != null && requiredItem.getType() == pickedUpItem.getType()) {
                                hasAtLeastOne = true;
                                break outer;
                            }
                        }
                        case EXACT_CHOICE -> {
                            ItemStack requiredItem = rec.items().get(slot);
                            if (requiredItem != null && requiredItem.isSimilar(pickedUpItem)) {
                                hasAtLeastOne = true;
                                break outer;
                            }
                        }
                        case MATERIAL_CHOICE -> {
                            for (Material requiredMat : rec.materialChoiceExtra().get(slot)) {
                                if (requiredMat == pickedUpItem.getType()) {
                                    hasAtLeastOne = true;
                                    break outer;
                                }
                            }
                        }
                    }
                }
                if (hasAtLeastOne) {
                    player.discoverRecipe(rec.key());
                    plugin.languager.notifyAutoUnlockOne(player);
                }
            }
        }
    }

}
