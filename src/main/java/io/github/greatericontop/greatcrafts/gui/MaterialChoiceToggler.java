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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class MaterialChoiceToggler implements Listener {
    private static final String INV_NAME = "§bToggle Material Choice";

    private final GUIManager guiManager;
    public MaterialChoiceToggler(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public void openNew(Player player, int slotNumber) {
        Map<String, Object> internalData = guiManager.guiData.get(player.getUniqueId());
        IngredientType currentType = ((IngredientType[]) internalData.get("ingredientTypes"))[slotNumber];
        String descriptionText, editText;
        switch (currentType) {
            case NORMAL -> {
                descriptionText = "§fMaterial Choice is currently §4OFF";
                editText = "§aEnable and Edit";
            }
            case EXACT_CHOICE -> {
                descriptionText = "§fMaterial Choice is currently §4OFF§f, but Material Choice is on";
                editText = "§aEnable and Edit §7(disables Exact Choice)";
            }
            case MATERIAL_CHOICE -> {
                descriptionText = "§fMaterial Choice is currently §2ON";
                editText = "§aEdit";
            }
            default -> throw new RuntimeException();
        }
        Inventory gui = Bukkit.createInventory(player, 9, INV_NAME);
        if (currentType == IngredientType.MATERIAL_CHOICE) {
            gui.setItem(0, Util.createItemStack(Material.RED_STAINED_GLASS, 1, "§cDisable"));
        }
        gui.setItem(4, Util.createItemStack(Material.ENCHANTED_BOOK, 1, descriptionText));
        gui.setItem(8, Util.createItemStack(Material.LIME_STAINED_GLASS, 1, editText));
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
        int slotNumber = (int) internalData.get("currentSlot");

        if (event.getSlot() == 0) {
            IngredientType[] types = (IngredientType[]) internalData.get("ingredientTypes");
            if (types[slotNumber] == IngredientType.MATERIAL_CHOICE) {
                types[slotNumber] = IngredientType.NORMAL;
                Util.successSound(player);
                player.closeInventory(); // Will automatically return to previous
            } else {
                event.setCancelled(true);
            }
        } else if (event.getSlot() == 8) {
            ((IngredientType[]) internalData.get("ingredientTypes"))[slotNumber] = IngredientType.MATERIAL_CHOICE;
            Util.successSound(player);

            // TODO: temporarily disable reopening thing
            Inventory save = guiManager.playerMainInventories.get(player.getUniqueId());
            guiManager.playerMainInventories.remove(player.getUniqueId());
            player.closeInventory();
            guiManager.playerMainInventories.put(player.getUniqueId(), save);
            guiManager.getPlugin().guiMaterialChoiceEditor.openNew(player);

        } else {
            event.setCancelled(true);
        }
    }

}
