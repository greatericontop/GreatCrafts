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

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Util {

    public static ItemStack createItemStack(Material mat, int amount, String name, String... lore) {
        return createItemStack(mat, amount, name, Arrays.asList(lore));
    }
    public static ItemStack createItemStack(Material mat, int amount, String name, List<String> lore) {
        ItemStack stack = new ItemStack(mat, amount);
        ItemMeta im = stack.getItemMeta();
        im.setDisplayName(name);
        im.setLore(lore);
        stack.setItemMeta(im);
        return stack;
    }

    public static ItemStack createItemStackWithPDC(Material mat, int amount, NamespacedKey key, PersistentDataType dtype, Object value, String name, String... lore) {
        ItemStack stack = new ItemStack(mat, amount);
        ItemMeta im = stack.getItemMeta();
        im.setDisplayName(name);
        im.getPersistentDataContainer().set(key, dtype, value);
        im.setLore(java.util.Arrays.asList(lore));
        stack.setItemMeta(im);
        return stack;
    }

    public static void appendLore(ItemMeta im, String... moreLore) {
        List<String> lore = im.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.addAll(Arrays.asList(moreLore));
        im.setLore(lore);
    }

    public static void successSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1L, 1L);
    }

    public static List<List<Material>> defaultMaterialChoiceExtra() {
        List<List<Material>> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list.add(new ArrayList<>());
        }
        return list;
    }

    private static final int LINES = 5;
    private static final int PER_LINE = 4;

    public static ItemStack renderMaterialChoiceIcon(List<Material> items, boolean showEditTooltip) {
        List<String> lore = new ArrayList<>();
        if (showEditTooltip) {
            lore.add("§eSHIFT RIGHT CLICK §fto edit!");
            lore.add("§dThis is a placeholder item. It is not actually in the recipe. Removing");
            lore.add("§dthis item from this menu does not have any effect.");
        }
        lore.add("§7Items:");
        int numLines = Math.min(LINES, (int) Math.ceil(items.size() / (double)PER_LINE));
        for (int line = 0; line < numLines; line++) {
            int indexStart = line * PER_LINE;
            String[] names = new String[Math.min(PER_LINE, items.size() - indexStart)];
            for (int j = 0; j < names.length; j++) {
                names[j] = items.get(indexStart + j).name().toLowerCase().replace('_', ' ');
            }
            String itemsDisplay = "§f" + String.join("§7;§f  ", names);
            if (line == LINES-1 && items.size() > LINES*PER_LINE) {
                    itemsDisplay = itemsDisplay + "§7;§f  ...";
            }
            if (line != numLines-1) {
                itemsDisplay = itemsDisplay + "§7;";
            }
            lore.add(itemsDisplay);
        }
        return createItemStack(Material.END_PORTAL_FRAME, 1, "§bMaterial Choice", lore);
    }

    public static int performShiftClickCraft(Player player, ItemStack result, int maxCraftsAvailable) {
        int actualAmountCrafted = 0;
        while (maxCraftsAvailable > 0) {
            // The .clone is necessary because spigot is dumb
            Map<Integer, ItemStack> unadded = player.getInventory().addItem(result.clone());
            if (!unadded.isEmpty()) {
                if (unadded.size() != 1)  throw new RuntimeException();
                if (!unadded.containsKey(0))  throw new RuntimeException();
                ItemStack failedToAdd = unadded.get(0);
                if (failedToAdd.getAmount() == result.getAmount()) {
                    // All the items failed to add - pretend this craft never happened
                    break;
                } else {
                    // Some added - drop the ones that didn't get added (this is the vanilla behavior)
                    player.getWorld().dropItemNaturally(player.getLocation(), failedToAdd);
                    actualAmountCrafted++;
                    maxCraftsAvailable--;
                    break;
                }
            }
            actualAmountCrafted++;
            maxCraftsAvailable--;
        }
        return actualAmountCrafted;
    }

}
