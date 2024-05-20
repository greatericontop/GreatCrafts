package io.github.greatericontop.greatcrafts.gui;

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

public class ExactChoiceToggler implements Listener {
    private static final String INV_NAME = "§bToggle Exact Choice";

    private final GUIManager guiManager;
    public ExactChoiceToggler(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public void openNew(Player player, int slotNumber) {
        Map<String, Object> internalData = guiManager.guiData.get(player.getUniqueId());
        IngredientType currentType = ((IngredientType[]) internalData.get("ingredientTypes"))[slotNumber];
        String descriptionText, toggleText;
        Material toggleMaterial;
        switch (currentType) {
            case NORMAL -> {
                descriptionText = "§fExact Choice is currently §4OFF";
                toggleText = "§aEnable";
                toggleMaterial = Material.LIME_STAINED_GLASS;
            }
            case EXACT_CHOICE -> {
                descriptionText = "§fExact Choice is currently §2ON";
                toggleText = "§cDisable";
                toggleMaterial = Material.RED_STAINED_GLASS;
            }
            case MATERIAL_CHOICE -> {
                descriptionText = "§fExact Choice is currently §4OFF§f, but Material Choice is on";
                toggleText = "§aEnable §7(disables Material Choice)";
                toggleMaterial = Material.LIME_STAINED_GLASS;
            }
            default -> throw new RuntimeException();
        }
        Inventory gui = Bukkit.createInventory(player, 9, INV_NAME);
        gui.setItem(4, Util.createItemStack(Material.ENCHANTED_BOOK, 1, descriptionText));
        gui.setItem(8, Util.createItemStack(toggleMaterial, 1, toggleText));
        player.openInventory(gui);
    }

    @EventHandler()
    public void onClick(InventoryClickEvent event) {
        Inventory gui = event.getClickedInventory();
        if (gui == null)  return;
        if (!event.getView().getTitle().equals(INV_NAME))  return;
        if (!event.getView().getTopInventory().equals(event.getClickedInventory()))  return; // must click top inventory
        if (event.getSlot() != 8) {
            event.setCancelled(true);
            return;
        }
        Player player = (Player) event.getWhoClicked();

        Map<String, Object> internalData = guiManager.guiData.get(player.getUniqueId());
        int slotNumber = (int) internalData.get("currentSlot");
        IngredientType currentType = ((IngredientType[]) internalData.get("ingredientTypes"))[slotNumber];
        IngredientType newType;
        switch (currentType) {
            case NORMAL, MATERIAL_CHOICE -> newType = IngredientType.EXACT_CHOICE;
            case EXACT_CHOICE -> newType = IngredientType.NORMAL;
            default -> throw new RuntimeException();
        }
        ((IngredientType[]) internalData.get("ingredientTypes"))[slotNumber] = newType;
        System.out.println(internalData.get("ingredientTypes"));

        Util.successSound(player);
        player.closeInventory(); // Will automatically return to previous
    }

}
