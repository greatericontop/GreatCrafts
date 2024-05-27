package io.github.greatericontop.greatcrafts.gui;

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

import io.github.greatericontop.greatcrafts.internal.Util;
import io.github.greatericontop.greatcrafts.internal.datastructures.IngredientType;
import io.github.greatericontop.greatcrafts.internal.datastructures.SavedRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MaterialChoiceEditor implements Listener {
    private static final String INV_NAME = "§bEdit Material Choice Items";

    private final GUIManager guiManager;
    public MaterialChoiceEditor(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public void openNew(Player player) {
        Inventory gui = Bukkit.createInventory(player, 54, INV_NAME);
        gui.setItem(51, Util.createItemStack(Material.ENCHANTED_BOOK, 1, "§bEdit Material Choice Items",
                "§fPlace items in any of the 51 other slots of this inventory.",
                "§fFor this slot, §eany §fitem placed here (no exact match needed) can be used."));
        gui.setItem(52, Util.createItemStack(Material.RED_STAINED_GLASS, 1, "§cDiscard Changes"));
        gui.setItem(53, Util.createItemStack(Material.LIME_STAINED_GLASS, 1, "§aSave Changes"));

        Map<String, Object> internalData = guiManager.guiData.get(player.getUniqueId());
        int slotNumber = (int) internalData.get("currentSlot");
        List<Material> materialChoices = ((List<List<Material>>) internalData.get("materialChoiceExtra")).get(slotNumber);
        for (int i = 0; i < materialChoices.size(); i++) {
            gui.setItem(i, new ItemStack(materialChoices.get(i), 1));
        }

        player.openInventory(gui);
    }

    @EventHandler()
    public void onClick(InventoryClickEvent event) {
        Inventory gui = event.getClickedInventory();
        if (gui == null)  return;
        if (!event.getView().getTitle().equals(INV_NAME))  return;
        if (!event.getView().getTopInventory().equals(event.getClickedInventory()))  return; // must click top inventory
        Player player = (Player) event.getWhoClicked();

        Map<String, Object> internalData = guiManager.guiData.get(player.getUniqueId());
        int recipeSlotNum = (int) internalData.get("currentSlot");

        if (event.getSlot() == 51) {
            event.setCancelled(true);
        } else if (event.getSlot() == 52) {
            player.closeInventory();
        } else if (event.getSlot() == 53) {
            List<Material> materialChoiceList = new ArrayList<>();
            for (int i = 0; i <= 50; i++) { // NOT 51, 52, 53
                ItemStack stack = gui.getItem(i);
                if (stack != null && stack.getType() != Material.AIR && !materialChoiceList.contains(stack.getType())) {
                    materialChoiceList.add(stack.getType());
                }
            }
            ((List<List<Material>>) internalData.get("materialChoiceExtra")).set(recipeSlotNum, materialChoiceList);
            // Update slot in the crafting menu with end portal frame
            guiManager.getPlugin().guiCraftEditor.fillCraftingSlot(recipeSlotNum,
                    guiManager.playerMainInventories.get(player.getUniqueId()),
                    (SavedRecipe) internalData.get("recipe"),
                    (IngredientType[]) internalData.get("ingredientTypes"),
                    (List<List<Material>>) internalData.get("materialChoiceExtra"));
            Util.successSound(player);
            player.closeInventory();
        }

    }

}
