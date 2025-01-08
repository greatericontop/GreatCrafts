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




}
