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

import javax.annotation.Nullable;

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

        String setting = GreatCommands.argumentStringFromChoices(1, args, new String[]{"auto-unlock-setting", "permission-requirement"});
        if (setting == null) {
            plugin.languager.commandErrorMustBeOneOfChoices(sender, "setting to change", "auto-unlock-setting", "permission-requirement");
            return true;
        }
        if (setting.equals("auto-unlock-setting")) {
            String value = GreatCommands.argumentStringFromChoices(2, args, new String[]{"never", "have-each", "have-one", "always", "default"});
            if (value == null) {
                plugin.languager.commandErrorMustBeOneOfChoices(sender, "auto-unlock-setting setting", "never", "have-each", "have-one", "always", "default");
                return true;
            }
            if (value.equals("default")) {
                plugin.getConfig().set(String.format("automatically-unlock-recipes-exceptions.%s", recipeName), null);
            } else {
                plugin.getConfig().set(String.format("automatically-unlock-recipes-exceptions.%s", recipeName), value);
            }
            plugin.saveConfig();
            plugin.updateConfigVars();
            plugin.languager.commandExtraSettingSuccess(sender, recipeName, "auto-unlock-setting", value);
            return true;
        }
        if (setting.equals("permission-requirement")) {
            @Nullable String value = GreatCommands.argumentString(2, args);
            plugin.getConfig().set(String.format("recipe-permission-requirements.%s", recipeName), value);
            plugin.saveConfig();
            plugin.updateConfigVars();
            if (value == null) {
                plugin.languager.commandPermissionReqRemoveSuccess(sender);
            } else {
                plugin.languager.commandExtraSettingSuccess(sender, recipeName, "permission-requirement", value);
            }
            return true;
        }

        return false; // this should never happen
    }

}
