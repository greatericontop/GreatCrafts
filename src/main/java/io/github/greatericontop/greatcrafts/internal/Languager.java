package io.github.greatericontop.greatcrafts.internal;

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
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Languager {

    private final GreatCrafts plugin;
    public Languager(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    private List<String> getText(String languageKey) {
        return plugin.getConfig().getStringList(String.format("language-settings.%s", languageKey));
    }

    public void stackedItemsErrorMissedExactMatch(CommandSender sender, String recipeKey) {
        for (String s : getText("stackedItemsErrorMissedExactMatch")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)
                    .replaceAll("%recipekey%", recipeKey));
        }
    }

    public void stackedItemsErrorNotEnoughItems(CommandSender sender, String recipeKey) {
        for (String s : getText("stackedItemsErrorNotEnoughItems")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)
                    .replaceAll("%recipekey%", recipeKey));
        }
    }

    public void commandErrorPlayerRequired(CommandSender sender) {
        for (String s : getText("commandErrorPlayerRequired")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public void commandErrorCreativeRequired(CommandSender sender) {
        for (String s : getText("commandErrorCreativeRequired")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public void commandErrorRecipeKeyFormat(CommandSender sender) {
        for (String s : getText("commandErrorRecipeKeyFormat")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public void commandErrorRecipeKeyNamespace(CommandSender sender) {
        for (String s : getText("commandErrorRecipeKeyNamespace")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public void commandErrorRecipeKeyKey(CommandSender sender) {
        for (String s : getText("commandErrorRecipeKeyKey")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public void commandErrorRecipeExists(CommandSender sender) {
        for (String s : getText("commandErrorRecipeExists")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public void commandConfirmDeletion(CommandSender sender, String recipeKey) {
        for (String s : getText("commandConfirmDeletion")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)
                    .replaceAll("%recipekey%", recipeKey));
        }
    }

    public void commandDeletionSuccess(CommandSender sender, String recipeKey) {
        for (String s : getText("commandDeletionSuccess")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)
                    .replaceAll("%recipekey%", recipeKey));
        }
    }

    public void commandErrorRecipeNotExist(CommandSender sender, String recipeKey) {
        for (String s : getText("commandErrorRecipeNotExist")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)
                    .replaceAll("%recipekey%", recipeKey));
        }
    }

    public void inventoryCloseTooEarly(CommandSender sender) {
        for (String s : getText("inventoryCloseTooEarly")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public void notifyAutoUnlockOnJoin(CommandSender sender, int counter) {
        for (String s : getText("notifyAutoUnlockOnJoin")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s)
                    .replaceAll("%counter%", String.valueOf(counter)));
        }
    }

    public void notifyAutoUnlockEach(CommandSender sender) {
        for (String s : getText("notifyAutoUnlockEach")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public void notifyAutoUnlockOne(CommandSender sender) {
        for (String s : getText("notifyAutoUnlockOne")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public void commandErrorRecipeListEmpty(CommandSender sender) {
        for (String s : getText("commandErrorRecipeListEmpty")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public void commandErrorRecipeListNothingMatches(CommandSender sender) {
        for (String s : getText("commandErrorRecipeListNothingMatches")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }



}
