package io.github.greatericontop.greatcrafts.commands;

/*
 * Copyright (C) 2024-present greateric.
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GreatCraftsCommand implements CommandExecutor {

    private final GreatCrafts plugin;
    public GreatCraftsCommand(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§9--------------------------------------------------");
        sender.sendMessage("");
        sender.sendMessage(String.format("§aGreat§bCrafts §7v%s", plugin.getDescription().getVersion()));
        sender.sendMessage("");
        sender.sendMessage(String.format("§b%d §3recipes", plugin.recipeManager.getAllSavedRecipes().size()));
        sender.sendMessage("");
        sender.sendMessage("§e/recipes");
        sender.sendMessage("§e/viewrecipe");
        sender.sendMessage("§e/addrecipe");
        sender.sendMessage("§e/editrecipe");
        sender.sendMessage("§e/reloadrecipes");
        sender.sendMessage("");
        sender.sendMessage("§9--------------------------------------------------");
        return true;
    }

}
