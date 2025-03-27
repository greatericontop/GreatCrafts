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
import io.github.greatericontop.greatcrafts.internal.datastructures.SavedRecipe;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditRecipeCommand implements CommandExecutor {

    private final GreatCrafts plugin;
    public EditRecipeCommand(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        String recipeName = args[0];
        SavedRecipe savedRecipe = plugin.recipeManager.getRecipe(recipeName);
        if (savedRecipe == null) {
            plugin.languager.commandErrorRecipeNotExist(sender, recipeName);
            return true;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                plugin.languager.commandErrorPlayerRequired(sender);
                return true;
            }
            if (player.getGameMode() != GameMode.CREATIVE) {
                plugin.languager.commandErrorCreativeRequired(player);
                return true;
            }
            plugin.guiCraftEditor.openNew(player, recipeName);
            return true;
        }

        String setting = GreatCommands.argumentStringFromChoices(1, args, new String[]{"auto-unlock-setting"});
        if (setting == null) {
            sender.sendMessage("The setting must be one of the choices"); // TODO: language
            return true;
        }
        if (setting.equals("auto-unlock-setting")) {
            String value = GreatCommands.argumentStringFromChoices(2, args, new String[]{"never", "have-each", "have-one", "always", "default"});
            if (value == null) {
                sender.sendMessage("The value for auto-unlock-setting must be one of the choices ..."); // TODO: language
                return true;
            }
            if (value.equals("default")) {
                plugin.getConfig().set(String.format("automatically-unlock-recipes-exceptions.%s", recipeName), null);
            } else {
                plugin.getConfig().set(String.format("automatically-unlock-recipes-exceptions.%s", recipeName), value);
            }
            plugin.saveConfig();
            plugin.updateConfigVars();
            sender.sendMessage("§3Setting updated and config reloaded."); // TODO: language
        }

        return true;
    }

}
