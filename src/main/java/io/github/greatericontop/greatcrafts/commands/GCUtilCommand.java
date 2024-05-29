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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GCUtilCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String subcommand = GreatCommands.argumentString(0, args);
        if (subcommand == null)  return false;

        if (subcommand.equalsIgnoreCase("setcustomname")) {
            String name = GreatCommands.argumentStringConsumeRest(1, args);
            if (name == null) {
                sender.sendMessage("§c/greatcraftsutil setcustomname <name... (use & for colors)>");
                return true;
            }
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cSorry, players only!");
                return true;
            }
            setCustomName(player, name);
            return true;
        }

        if (subcommand.equalsIgnoreCase("setloreline")) {
            Integer lineNum = GreatCommands.argumentInteger(1, args);
            String loreLine = GreatCommands.argumentStringConsumeRest(2, args);
            if (loreLine == null) {
                loreLine = "";
            }
            if (lineNum == null) {
                sender.sendMessage("§c/greatcraftsutil setloreline <line # (starts from 0)> [<lore line... (use & for colors)>]");
                return true;
            }
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cSorry, players only!");
                return true;
            }
            setLoreLine(player, lineNum, loreLine);
            return true;
        }

        if (subcommand.equalsIgnoreCase("deleteloreline") || subcommand.equalsIgnoreCase("deletelorelines")) {
            int[] lineNums = GreatCommands.argumentIntegerConsumeRest(1, args);
            if (lineNums == null) {
                sender.sendMessage("§c/greatcraftsutil deleteloreline(s) <line # (starts from 0)> [<line #> <line #> ...]");
                return true;
            }
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cSorry, players only!");
                return true;
            }
            deleteLoreLines(player, lineNums);
            return true;
        }

        if (subcommand.equalsIgnoreCase("enchant")) {
            Enchantment enchant = GreatCommands.argumentMinecraftEnchantment(1, args);
            Integer level = GreatCommands.argumentInteger(2, args);
            if (enchant == null || level == null) {
                sender.sendMessage("§c/greatcraftsutil enchant <enchantment (Minecraft ID)> <level (0 to remove)>");
                return true;
            }
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cSorry, players only!");
                return true;
            }
            enchant(player, enchant, level);
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

    private void setLoreLine(Player player, int lineNum, String loreLine) {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        ItemMeta im = handItem.getItemMeta();
        if (im == null) {
            player.sendMessage("§cNothing in your hand, or the thing in your hand can't be edited!");
            return;
        }
        List<String> lore = im.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        // Pad empty lines (up to 10 at a time)
        if (lore.size() <= lineNum) {
            if (lore.size() + 10 <= lineNum) {
                player.sendMessage(String.format("§cDid you make a typo? The lore is currently only §f%d§c lines long.", lore.size()));
                return;
            }
            for (int i = lore.size(); i <= lineNum; i++) {
                lore.add("");
            }
        }
        lore.set(lineNum, ChatColor.translateAlternateColorCodes('&', loreLine));
        im.setLore(lore);
        handItem.setItemMeta(im);
        player.sendMessage("§3Set your item's lore.");
    }

    private void deleteLoreLines(Player player, int[] lineNums) {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        ItemMeta im = handItem.getItemMeta();
        if (im == null) {
            player.sendMessage("§cNothing in your hand, or the thing in your hand can't be edited!");
            return;
        }
        List<String> lore = im.getLore();
        if (lore == null) {
            player.sendMessage("§cNo lore to delete!");
            return;
        }
        lineNums = Arrays.stream(lineNums).distinct().sorted().toArray(); // distinct, sorted, and in reverse
        for (int i = lineNums.length - 1; i >= 0; i--) {
            int lineNum = lineNums[i];
            if (lineNum >= 0 && lineNum < lore.size()) {
                lore.remove(lineNum);
            }
        }
        im.setLore(lore);
        handItem.setItemMeta(im);
        player.sendMessage("§3Deleted your item's lore lines.");
    }

    private void enchant(Player player, Enchantment enchant, int level) {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        ItemMeta im = handItem.getItemMeta();
        if (im == null) {
            player.sendMessage("§cNothing in your hand, or the thing in your hand can't be edited!");
            return;
        }
        if (level <= 0) {
            im.removeEnchant(enchant);
            handItem.setItemMeta(im);
            player.sendMessage("§3Removed enchantment.");
        } else {
            im.addEnchant(enchant, level, true);
            handItem.setItemMeta(im);
            player.sendMessage("§3Enchanted your item.");
        }
    }

}
