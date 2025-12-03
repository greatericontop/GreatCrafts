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
import io.github.greatericontop.greatcrafts.internal.datastructures.RecipeType;
import io.github.greatericontop.greatcrafts.internal.datastructures.SavedRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class CraftReadOnlyViewer implements Listener {
    private static final String INV_NAME = "§bView Crafting Recipe";
    private static final int SLOT1 = 10;
    private static final int SLOT2 = 11;
    private static final int SLOT3 = 12;
    private static final int SLOT4 = 19;
    private static final int SLOT5 = 20;
    private static final int SLOT6 = 21;
    private static final int SLOT7 = 28;
    private static final int SLOT8 = 29;
    private static final int SLOT9 = 30;
    private static final int SLOT_RESULT = 23;
    private static final int SLOT_RECIPE_LIST = 45;
    private static final int SLOT_EDIT = 53;

    private final GUIManager guiManager;
    private final NamespacedKey recipeKeyPDC;
    public CraftReadOnlyViewer(GUIManager guiManager) {
        this.guiManager = guiManager;
        this.recipeKeyPDC = new NamespacedKey(guiManager.getPlugin(), "recipeKey");
    }

    public void openNew(Player player, String craftKey) {
        SavedRecipe savedRecipe = guiManager.getRecipeManager().getRecipe(craftKey);
        if (savedRecipe == null) {
            guiManager.getPlugin().languager.commandErrorRecipeNotExist(player, craftKey);
            return;
        }
        Inventory gui = Bukkit.createInventory(player, 54, INV_NAME);

        for (int i = 0; i < 54; i++) {
            gui.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        }
        fillViewCraftingSlots(gui, savedRecipe, savedRecipe.ingredientTypes(), savedRecipe.materialChoiceExtra());
        gui.setItem(SLOT_RESULT, savedRecipe.result());
        gui.setItem(SLOT_RECIPE_LIST, Util.createItemStack(Material.ENCHANTED_BOOK, 1, "§aRecipe List §eCLICK HERE"));
        if (player.hasPermission("greatcrafts.modifyrecipes")) {
            gui.setItem(SLOT_EDIT, Util.createItemStackWithPDC(Material.WRITABLE_BOOK, 1, recipeKeyPDC, PersistentDataType.STRING, savedRecipe.key().toString(), "§aEdit §eCLICK HERE"));
        }
        player.openInventory(gui);
    }

    @EventHandler()
    public void onClick(InventoryClickEvent event) {
        Inventory gui = event.getClickedInventory();
        if (gui == null)  return;
        if (!event.getView().getTitle().equals(INV_NAME))  return;
        event.setCancelled(true);
        if (event.getSlot() == SLOT_RECIPE_LIST) {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
            player.chat("/recipes");
        }
        if (event.getSlot() == SLOT_EDIT && event.getCurrentItem().getType() != Material.BLACK_STAINED_GLASS_PANE) {
            Player player = (Player) event.getWhoClicked();
            String recipeKey = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(recipeKeyPDC, PersistentDataType.STRING);
            player.closeInventory();
            player.chat(String.format("/editrecipe %s", recipeKey));
        }
    }

    private void fillViewCraftingSlots(Inventory gui, SavedRecipe recipe, IngredientType[] ingredientTypes, List<List<Material>> materialChoiceExtra) {
        // recipe.getIngredientMap() -> {a: _, b: _, c: _, ..., h: _, i: _} of the grid
        int[] SLOTS = {SLOT1, SLOT2, SLOT3, SLOT4, SLOT5, SLOT6, SLOT7, SLOT8, SLOT9};
        for (int i = 0; i < 9; i++) {
            switch (ingredientTypes[i]) {
                case NORMAL -> {
                    // Strip special stuff
                    if (recipe.items().get(i) == null) {
                        gui.setItem(SLOTS[i], null);
                    } else {
                        gui.setItem(SLOTS[i], new ItemStack(recipe.items().get(i).getType(), recipe.items().get(i).getAmount()));
                    }
                }
                case EXACT_CHOICE -> {
                    gui.setItem(SLOTS[i], recipe.items().get(i));
                }
                case MATERIAL_CHOICE -> {
                    if (recipe.type() == RecipeType.STACKED_ITEMS) {
                        // Error message
                        gui.setItem(SLOTS[i], Util.createItemStack(Material.END_PORTAL_FRAME, 64, "§cMaterial Choice",
                                "§fMaterial choice is unsupported! Please fix this recipe!"
                        ));
                    } else {
                        List<Material> items = materialChoiceExtra.get(i);
                        String[] names = new String[Math.min(8, items.size())];
                        for (int j = 0; j < names.length; j++) {
                            names[j] = items.get(j).name().toLowerCase().replace('_', ' ');
                        }
                        String itemsDisplay = "§f" + String.join("§7,§f ", names) + (items.size() > 8 ? "§7,§f ..." : "");
                        gui.setItem(SLOTS[i], Util.createItemStack(Material.END_PORTAL_FRAME, 1, "§bMaterial Choice",
                                "§8Items:",
                                itemsDisplay
                        ));
                    }
                }
            }
        }
    }

}
