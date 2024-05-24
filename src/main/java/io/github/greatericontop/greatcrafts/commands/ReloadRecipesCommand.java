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
import io.github.greatericontop.greatcrafts.internal.RecipeLoader;
import io.github.greatericontop.greatcrafts.internal.datastructures.SavedRecipe;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadRecipesCommand implements CommandExecutor {

    private final GreatCrafts plugin;
    public ReloadRecipesCommand(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        List<SavedRecipe> recipes = plugin.recipeManager.getAllSavedRecipes();
        for (SavedRecipe savedRecipe : recipes) {
            Bukkit.removeRecipe(savedRecipe.key());
            RecipeLoader.compileAndAddRecipe(savedRecipe, sender);
        }
        sender.sendMessage(String.format("§3Successfully reloaded %s recipes.", recipes.size()));
        sender.sendMessage("§eNote: §3Players still need to reconnect to see the recipe client-side, but it will work on the server.");

        return true;
    }

}
