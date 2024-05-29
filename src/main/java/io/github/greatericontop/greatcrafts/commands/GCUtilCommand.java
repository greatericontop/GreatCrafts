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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GCUtilCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String subcommand = GreatCommands.argumentString(0, args);
        if (subcommand == null)  return false;

        if (subcommand.equalsIgnoreCase("setcustomname")) {
            String usageMessage = "§c/greatcraftsutil setcustomname <name... (use & for colors)>";
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cSorry, players only!");
                return true;
            }
            String name = GreatCommands.argumentStringConsumeRest(1, args);
            if (name == null) {
                sender.sendMessage(usageMessage);
                return true;
            }
            setCustomName(player, name);
            return true;
        }

        return false;
    }

    private void setCustomName(Player player, String name) {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        ItemMeta im = handItem.getItemMeta();
        if (im == null) {
            player.sendMessage("§cNothing in your hand, or the thing in your hand can't be edited!");
            return;
        }
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        handItem.setItemMeta(im);
        player.sendMessage("§3Set your item's name.");
    }

}
