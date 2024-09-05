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
import io.github.greatericontop.greatcrafts.internal.datastructures.RecipeType;
import io.github.greatericontop.greatcrafts.internal.datastructures.SavedRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class CrafterEvents implements Listener {

    private final GreatCrafts plugin;
    public CrafterEvents(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onCrafterCraft(CrafterCraftEvent event) {
        Recipe _rawRecipe = event.getRecipe();
        if (_rawRecipe instanceof ShapedRecipe _shapedRecipe) {
            NamespacedKey recipeKey = _shapedRecipe.getKey();
            SavedRecipe savedRecipe = plugin.recipeManager.getRecipe(recipeKey.toString());
            if (savedRecipe == null || savedRecipe.type() != RecipeType.STACKED_ITEMS) {
                return;
            }
            // TODO: WIP
            event.setCancelled(true);
            for (Entity e : event.getBlock().getWorld().getNearbyEntities(event.getBlock().getLocation(), 10.0, 10.0, 10.0, entity -> entity instanceof Player)) {
                e.sendMessage("§cCrafters cannot craft stacked items recipes yet!");
            }
        } else if (_rawRecipe instanceof ShapelessRecipe _shapelessRec) {
            NamespacedKey recipeKey = _shapelessRec.getKey();
            SavedRecipe savedRecipe = plugin.recipeManager.getRecipe(recipeKey.toString());
            if (savedRecipe == null || savedRecipe.type() != RecipeType.STACKED_ITEMS_SHAPELESS) {
                return;
            }
            // TODO: WIP
            event.setCancelled(true);
            for (Entity e : event.getBlock().getWorld().getNearbyEntities(event.getBlock().getLocation(), 10.0, 10.0, 10.0, entity -> entity instanceof Player)) {
                e.sendMessage("§cCrafters cannot craft stacked items recipes yet!");
            }
        }
    }

}
