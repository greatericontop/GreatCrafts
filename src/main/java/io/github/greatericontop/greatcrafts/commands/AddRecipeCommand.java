package io.github.greatericontop.greatcrafts.commands;

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
import io.github.greatericontop.greatcrafts.internal.Util;
import io.github.greatericontop.greatcrafts.internal.datastructures.IngredientType;
import io.github.greatericontop.greatcrafts.internal.datastructures.RecipeType;
import io.github.greatericontop.greatcrafts.internal.datastructures.SavedRecipe;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class AddRecipeCommand implements CommandExecutor {

    private final GreatCrafts plugin;
    public AddRecipeCommand(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cA player is required!");
            return true;
        }
        if (player.getGameMode() != GameMode.CREATIVE) {
            player.sendMessage("§cYour gamemode must be creative to edit recipes!");
            return true;
        }
        if (args.length == 0) {
            return false;
        }

        String recipeName = args[0];
        String[] recipeNameParts = recipeName.split(":");
        if (recipeNameParts.length != 2) {
            player.sendMessage("§cThe recipe must be in the format §4namespace:name§c!");
            return true;
        }
        NamespacedKey key = new NamespacedKey(recipeNameParts[0], recipeNameParts[1]);
        if (plugin.recipeManager.getRecipe(key.toString()) != null) {
            player.sendMessage("§cThis recipe already exists!");
            return true;
        }

        List<ItemStack> items = Arrays.asList(
                null, null, null,
                null, new ItemStack(Material.EMERALD_ORE, 1), null,
                null, null, null
        );
        ItemStack iconItem = Util.createItemStack(Material.EMERALD_BLOCK, 1, "§dDefault Icon",
                "§7This is the icon for your recipe. This is displayed in the menu", "§7and doesn't affect the craft.");
        plugin.recipeManager.setRecipe(key.toString(), new SavedRecipe(
                key, RecipeType.SHAPED, items, new ItemStack(Material.EMERALD_BLOCK, 1),
                IngredientType.defaults(), Util.defaultMaterialChoiceExtra(), iconItem));
        plugin.guiCraftEditor.openNew(player, recipeName);

        return true;
    }

}
