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
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
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

        String setting = GreatCommands.argumentStringFromChoices(1, args, new String[]{"show-settings", "auto-unlock-setting", "permission-requirement", "crafting-limit"});
        if (setting == null) {
            plugin.languager.commandErrorMustBeOneOfChoices(sender, "setting to change", "show-settings", "auto-unlock-setting", "permission-requirement", "crafting-limit");
            return true;
        }
        if (setting.equals("show-settings")) {
            String autoUnlockSetting;
            if (plugin.autoUnlockExceptions.containsKey(recipeName)) {
                autoUnlockSetting = String.format("§e%s §7(set)", plugin.autoUnlockExceptions.get(recipeName));
            } else {
                autoUnlockSetting = String.format("§e%s §7(plugin-wide default setting)", plugin.autoUnlockSetting);
            }
            String permissionReq;
            if (plugin.recipePermissionRequirements.containsKey(recipeName)) {
                permissionReq = String.format("§e%s", plugin.recipePermissionRequirements.getOrDefault(recipeName, null));
            } else {
                permissionReq = "§7(none)";
            }
            String craftingLimit;
            if (plugin.recipeCraftingLimits.containsKey(recipeName)) {
                craftingLimit = String.format("§e%d §7(per player, stacked items only)", plugin.recipeCraftingLimits.get(recipeName));
            } else {
                craftingLimit = "§7(unlimited)";
            }
            TextComponent auto1 = new TextComponent("§6auto unlock setting§7: " + autoUnlockSetting + " ");
            TextComponent auto2 = new TextComponent("§9[click to edit]");
            auto2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/editrecipe %s auto-unlock-setting", recipeName)));
            auto1.addExtra(auto2);
            sender.spigot().sendMessage(auto1);
            TextComponent permission1 = new TextComponent("§6permission requirement§7: " + permissionReq + " ");
            TextComponent permission2 = new TextComponent("§9[click to edit]");
            permission2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/editrecipe %s permission-requirement", recipeName)));
            permission1.addExtra(permission2);
            sender.spigot().sendMessage(permission1);
            TextComponent limit1 = new TextComponent("§6crafting limit§7: " + craftingLimit + " ");
            TextComponent limit2 = new TextComponent("§9[click to edit]");
            limit2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/editrecipe %s crafting-limit", recipeName)));
            limit1.addExtra(limit2);
            sender.spigot().sendMessage(limit1);
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
        if (setting.equals("crafting-limit")) {
            @Nullable Integer value = GreatCommands.argumentInteger(2, args);
            if (value == null) {
                plugin.languager.commandErrorIntegerRequired(sender);
                return true;
            }
            plugin.getConfig().set(String.format("recipe-crafting-limits.%s", recipeName), value);
            plugin.saveConfig();
            plugin.updateConfigVars();
            if (value == 0) {
                plugin.languager.commandCraftingLimitRemoveSuccess(sender);
            } else {
                plugin.languager.commandExtraSettingSuccess(sender, recipeName, "crafting-limit", String.valueOf(value));
            }
            return true;
        }

        return false; // this should never happen
    }

}
