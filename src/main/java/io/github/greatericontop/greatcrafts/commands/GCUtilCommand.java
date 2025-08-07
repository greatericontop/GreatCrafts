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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GCUtilCommand implements CommandExecutor {

    private final GreatCrafts plugin;
    public GCUtilCommand(GreatCrafts plugin) {
        this.plugin = plugin;
    }

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
                plugin.languager.commandErrorPlayerRequired(sender);
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
                plugin.languager.commandErrorPlayerRequired(sender);
                return true;
            }
            setLoreLine(player, lineNum, loreLine);
            return true;
        }

        if (subcommand.equalsIgnoreCase("deletelorelines")) {
            int[] lineNums = GreatCommands.argumentIntegerConsumeRest(1, args);
            if (lineNums == null) {
                sender.sendMessage("§c/greatcraftsutil deletelorelines <line # (starts from 0)> [<line #> <line #> ...]");
                return true;
            }
            if (!(sender instanceof Player player)) {
                plugin.languager.commandErrorPlayerRequired(sender);
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
                plugin.languager.commandErrorPlayerRequired(sender);
                return true;
            }
            enchant(player, enchant, level);
            return true;
        }

        if (subcommand.equalsIgnoreCase("duplicaterecipe")) {
            String sourceRecKey = GreatCommands.argumentString(1, args);
            String targetRecKey = GreatCommands.argumentString(2, args);
            if (sourceRecKey == null || targetRecKey == null) {
                sender.sendMessage("§c/greatcraftsutil duplicaterecipe <source namespace:name> <target namespace:name>");
                return true;
            }
            duplicateRecipe(sender, sourceRecKey, targetRecKey);
            return true;
        }

        if (subcommand.equalsIgnoreCase("resetlimits")) {
            String targetPlayerName = GreatCommands.argumentString(1, args);
            resetLimits(sender, targetPlayerName);
            return true;
        }

        return false;
    }

    private void setCustomName(Player player, String name) {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        ItemMeta im = handItem.getItemMeta();
        if (im == null) {
            plugin.languager.commandErrorNoItemMeta(player);
            return;
        }
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        handItem.setItemMeta(im);
        plugin.languager.commandCustomNameSuccess(player);
    }

    private void setLoreLine(Player player, int lineNum, String loreLine) {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        ItemMeta im = handItem.getItemMeta();
        if (im == null) {
            plugin.languager.commandErrorNoItemMeta(player);
            return;
        }
        List<String> lore = im.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        // Pad empty lines (up to 10 at a time)
        if (lore.size() <= lineNum) {
            if (lore.size() + 10 <= lineNum) {
                plugin.languager.commandErrorLoreLineTooBig(player, lore.size());
                return;
            }
            for (int i = lore.size(); i <= lineNum; i++) {
                lore.add("");
            }
        }
        lore.set(lineNum, ChatColor.translateAlternateColorCodes('&', loreLine));
        im.setLore(lore);
        handItem.setItemMeta(im);
        plugin.languager.commandLoreLineSuccess(player);
    }

    private void deleteLoreLines(Player player, int[] lineNums) {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        ItemMeta im = handItem.getItemMeta();
        if (im == null) {
            plugin.languager.commandErrorNoItemMeta(player);
            return;
        }
        List<String> lore = im.getLore();
        if (lore == null) {
            plugin.languager.commandErrorNoLoreToDelete(player);
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
        plugin.languager.commandLoreDeleteSuccess(player);
    }

    private void enchant(Player player, Enchantment enchant, int level) {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta im = (EnchantmentStorageMeta) handItem.getItemMeta();
            if (level <= 0) {
                im.removeStoredEnchant(enchant);
                handItem.setItemMeta(im);
                plugin.languager.commandRemoveEnchantSuccess(player);
            } else {
                im.addStoredEnchant(enchant, level, true);
                handItem.setItemMeta(im);
                plugin.languager.commandAddEnchantSuccess(player);
            }
        } else {
            ItemMeta im = handItem.getItemMeta();
            if (im == null) {
                plugin.languager.commandErrorNoItemMeta(player);
                return;
            }
            if (level <= 0) {
                im.removeEnchant(enchant);
                handItem.setItemMeta(im);
                plugin.languager.commandRemoveEnchantSuccess(player);
            } else {
                im.addEnchant(enchant, level, true);
                handItem.setItemMeta(im);
                plugin.languager.commandAddEnchantSuccess(player);
            }
        }
    }

    private void duplicateRecipe(CommandSender sender, String sourceRecKey, String targetRecKey) {
        if (sourceRecKey.split(":").length != 2 || targetRecKey.split(":").length != 2) {
            plugin.languager.commandErrorRecipeKeyFormat(sender);
            return;
        }
        SavedRecipe rec = plugin.recipeManager.getRecipe(sourceRecKey);
        if (rec == null) {
            plugin.languager.commandErrorRecipeNotExist(sender, sourceRecKey);
            return;
        }
        if (plugin.recipeManager.getRecipe(targetRecKey) != null) {
            plugin.languager.commandErrorDuplicationRecipeExists(sender);
            return;
        }

        // Prepare new recipe (copying directly causes a lot of issues, especially with the namespaced key mismatch
        // in config vs SavedRecipe), everything else won't be shadowed since we call setRecipe which saves it to disk.
        String[] targetRecParts = targetRecKey.split(":");
        NamespacedKey targetKey = new NamespacedKey(targetRecParts[0], targetRecParts[1]);

        SavedRecipe newSavedRec = new SavedRecipe(
                targetKey,
                rec.type(),
                rec.items(),
                rec.result(),
                rec.ingredientTypes(),
                rec.materialChoiceExtra(),
                rec.iconItem()
        );
        plugin.recipeManager.setRecipe(targetRecKey, newSavedRec);
        plugin.languager.commandDuplicationSuccess(sender, sourceRecKey, targetRecKey);
    }

    private void resetLimits(CommandSender sender, String targetPlayerName) {
        if (targetPlayerName == null || targetPlayerName.isEmpty()) {
            plugin.playerCraftCounts.clear();
            plugin.languager.craftingLimitResetSuccess(sender);
        } else {
            Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                plugin.languager.commandErrorPlayerNotFound(sender, targetPlayerName);
                return;
            }
            Map<String, Integer> targetPlayerMap = plugin.playerCraftCounts.get(targetPlayer.getUniqueId());
            if (targetPlayerMap != null) {
                targetPlayerMap.clear();
            }
            plugin.languager.craftingLimitResetPlayerSuccess(sender, targetPlayer.getName());
        }
    }

}
