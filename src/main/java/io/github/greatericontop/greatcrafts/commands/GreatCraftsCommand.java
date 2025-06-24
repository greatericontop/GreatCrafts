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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class GreatCraftsCommand implements CommandExecutor, TabCompleter {

    private final GreatCrafts plugin;
    public GreatCraftsCommand(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String arg = GreatCommands.argumentString(0, args);

        if (arg == null) {
            String ver = plugin.getDescription().getVersion();
            sender.sendMessage("§9--------------------------------------------------");
            sender.sendMessage("");
            sender.sendMessage(String.format("§aGreat§bCrafts §7v%s", ver));
            if (ver.contains("---")) {
                sender.sendMessage("");
                sender.sendMessage("§6This build of GreatCrafts was made for earlier versions of Minecraft.");
                sender.sendMessage(String.format("§6Intended version(s): §e%s", ver.split("---")[1]));
                sender.sendMessage("§6If this looks wrong to you, please go back and download the correct build.");
            }
            sender.sendMessage("");
            sender.sendMessage(String.format("§b%d §3recipes", plugin.recipeManager.getAllSavedRecipes().size()));
            sender.sendMessage("");
            sender.sendMessage("§e/greatcrafts reload §3to reload config");
            sender.sendMessage("");
            sender.sendMessage("§e/recipes");
            sender.sendMessage("§e/viewrecipe");
            sender.sendMessage("§e/addrecipe");
            sender.sendMessage("§e/editrecipe");
            sender.sendMessage("§e/deleterecipe");
            sender.sendMessage("§e/reloadrecipes");
            sender.sendMessage("§e/greatcraftsutil");
            sender.sendMessage("");
            sender.sendMessage("§9--------------------------------------------------");
            return true;
        }

        if (arg.equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.updateConfigVars();
            sender.sendMessage("§3GreatCrafts config reloaded.");
            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return List.of("reload");
        }
        return null;
    }

}
