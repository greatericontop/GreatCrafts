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
import io.github.greatericontop.greatcrafts.internal.RecipeLoader;
import io.github.greatericontop.greatcrafts.internal.datastructures.RecipeType;
import io.github.greatericontop.greatcrafts.internal.datastructures.SavedRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CraftEditor implements Listener {
    private static final String INV_NAME = "§bEdit Craft";
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
    private static final int SLOT_ICON = 16;
    private static final int SLOT_CHANGE_TYPE = 26;
    private static final int SLOT_DELETER = 46;
    private static final int SLOT_DISCARD = 51;
    private static final int SLOT_SAVE = 52;
    private static final int SLOT_SAVE_AND_ACTIVATE = 53;
    private static final Set<Integer> VALID_CLICK_SLOTS = Set.of(
            SLOT1, SLOT2, SLOT3, SLOT4, SLOT5, SLOT6, SLOT7, SLOT8, SLOT9,
            SLOT_RESULT, SLOT_ICON, SLOT_CHANGE_TYPE, SLOT_DELETER, SLOT_DISCARD, SLOT_SAVE, SLOT_SAVE_AND_ACTIVATE
    );
    private static final Map<Integer, Integer> SLOT_INDEXER = Map.of(
            SLOT1, 0, SLOT2, 1, SLOT3, 2,
            SLOT4, 3, SLOT5, 4, SLOT6, 5,
            SLOT7, 6, SLOT8, 7, SLOT9, 8
    );

    private final GUIManager guiManager;
    public CraftEditor(GUIManager guiManager) {
        this.guiManager = guiManager;
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
        fillCraftingSlots(gui, savedRecipe, savedRecipe.ingredientTypes(), savedRecipe.materialChoiceExtra());
        gui.setItem(SLOT_RESULT, savedRecipe.result());
        gui.setItem(SLOT_ICON, savedRecipe.iconItem());
        gui.setItem(45, Util.createItemStack(
                Material.ENCHANTED_BOOK, 1, "§bInfo",
                "§7Place items in the grid, result, and icon slots.",
                "§eSHIFT LEFT CLICK §7to toggle §fexact choice §7(exact NBT)",
                "§eSHIFT RIGHT CLICK §7to toggle §fmaterial choice §7(multiple items)",
                "§7Discard, save, or save and activate your changes."
        ));
        gui.setItem(SLOT_DELETER, Util.createItemStack(Material.REDSTONE, 1, "§cItem Deleter",
                "§7Place items here to delete them without dropping them on the ground."));
        gui.setItem(SLOT_CHANGE_TYPE, getDisplayItemStackForRecipeType(savedRecipe.type()));
        gui.setItem(SLOT_DISCARD, Util.createItemStack(Material.BARRIER, 1, "§cDiscard Changes"));
        gui.setItem(SLOT_SAVE, Util.createItemStack(Material.LIME_STAINED_GLASS, 1, "§aSave Changes"));
        gui.setItem(SLOT_SAVE_AND_ACTIVATE, Util.createItemStack(Material.LIME_CONCRETE, 1, "§aSave & Activate Changes",
                "§fThis reloads the current recipe and applies the changes immediately.",
                "§eNote: §fPlayers still need to reconnect to see the recipe client-side, but it will work on the server.",
                "§7You can also use §e/reloadrecipes §7to reload all recipes."
        ));

        Map<String, Object> data = new HashMap<>();
        data.put("recipe", savedRecipe);
        data.put("type", savedRecipe.type());
        // Stores the type of ingredient (e.g. whether to match NBT exactly or not)
        data.put("ingredientTypes", savedRecipe.ingredientTypes());
        // Stores the valid materials for material choice (has no effect if ingredientType is not set to material choice)
        data.put("materialChoiceExtra", savedRecipe.materialChoiceExtra());
        // For use with ExactChoiceToggler / MaterialChoiceToggler (initializing this is unnecessary)
        //data.put("currentSlot", -1);
        guiManager.guiData.put(player.getUniqueId(), data);
        guiManager.playerMainInventories.put(player.getUniqueId(), gui);
        player.openInventory(gui);
    }

    @EventHandler()
    public void onClick(InventoryClickEvent event) {
        Inventory gui = event.getClickedInventory();
        if (gui == null)  return;
        if (!event.getView().getTitle().equals(INV_NAME))  return;
        if (!event.getView().getTopInventory().equals(event.getClickedInventory()))  return; // must click top inventory
        int slot = event.getSlot();
        if (!VALID_CLICK_SLOTS.contains(slot)) {
            event.setCancelled(true);
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Map<String, Object> data = guiManager.guiData.get(player.getUniqueId());

        if (slot == SLOT_CHANGE_TYPE) {
            RecipeType currentType = (RecipeType) data.get("type");
            RecipeType newType = switch (currentType) {
                case SHAPED -> RecipeType.SHAPELESS;
                case SHAPELESS -> RecipeType.STACKED_ITEMS;
                case STACKED_ITEMS -> RecipeType.STACKED_ITEMS_SHAPELESS;
                case STACKED_ITEMS_SHAPELESS -> RecipeType.SHAPED;
            };
            data.put("type", newType);
            gui.setItem(SLOT_CHANGE_TYPE, getDisplayItemStackForRecipeType(newType));
            event.setCancelled(true);
        }

        if (slot == SLOT_DELETER) {
            player.setItemOnCursor(null);
            event.setCancelled(true);
        }

        if (slot == SLOT_DISCARD || slot == SLOT_SAVE || slot == SLOT_SAVE_AND_ACTIVATE) {
            if (slot == SLOT_SAVE || slot == SLOT_SAVE_AND_ACTIVATE) {
                SavedRecipe oldRecipe = (SavedRecipe) data.get("recipe");
                SavedRecipe newRecipe = saveIntoNewRecipe(gui, oldRecipe.key(), data);
                guiManager.getRecipeManager().setRecipe(oldRecipe.key().toString(), newRecipe);
                if (slot == SLOT_SAVE_AND_ACTIVATE) {
                    Bukkit.removeRecipe(newRecipe.key());
                    RecipeLoader.compileAndAddRecipe(newRecipe, player);
                }
                Util.successSound(player);
            }
            guiManager.guiData.remove(player.getUniqueId());
            guiManager.playerMainInventories.remove(player.getUniqueId());
            player.closeInventory();
        }

        if (SLOT_INDEXER.containsKey(slot)) {
            int index = SLOT_INDEXER.get(slot);
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                // Temporarily "forget" the inventory so it doesn't get reopened
                Inventory save = guiManager.playerMainInventories.get(player.getUniqueId());
                guiManager.playerMainInventories.remove(player.getUniqueId());
                player.closeInventory();
                guiManager.playerMainInventories.put(player.getUniqueId(), save);
                guiManager.guiData.get(player.getUniqueId()).put("currentSlot", index); // for use with the toggler
                guiManager.getPlugin().guiExactChoiceToggler.openNew(player, index);
                event.setCancelled(true);
            } else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                Inventory save = guiManager.playerMainInventories.get(player.getUniqueId());
                guiManager.playerMainInventories.remove(player.getUniqueId());
                player.closeInventory();
                guiManager.playerMainInventories.put(player.getUniqueId(), save);
                guiManager.guiData.get(player.getUniqueId()).put("currentSlot", index);
                guiManager.getPlugin().guiMaterialChoiceToggler.openNew(player, index);
                event.setCancelled(true);
            }
        }
    }

    private SavedRecipe saveIntoNewRecipe(Inventory gui, NamespacedKey key, Map<String, Object> data) {
        List<ItemStack> items = Arrays.asList(
                gui.getItem(SLOT1), gui.getItem(SLOT2), gui.getItem(SLOT3),
                gui.getItem(SLOT4), gui.getItem(SLOT5), gui.getItem(SLOT6),
                gui.getItem(SLOT7), gui.getItem(SLOT8), gui.getItem(SLOT9)
        );
        // FYI: the end portal frame placeholder (or something else) gets saved in here, but it is unused because
        //      the recipe will be compiled using the actual material choice items
        RecipeType type = (RecipeType) data.get("type");
        IngredientType[] ingredientTypes = (IngredientType[]) data.get("ingredientTypes");
        List<List<Material>> materialChoiceExtra = (List<List<Material>>) data.get("materialChoiceExtra");
        ItemStack resultItem = gui.getItem(SLOT_RESULT);
        if (resultItem == null || resultItem.getType() == Material.AIR) { // prevent breaking from empty slot here
            resultItem = new ItemStack(Material.GRASS_BLOCK, 1);
        }
        ItemStack slotIconItem = gui.getItem(SLOT_ICON);
        if (slotIconItem == null || slotIconItem.getType() == Material.AIR) {
            slotIconItem = new ItemStack(Material.GRASS_BLOCK, 1);
        }
        return new SavedRecipe(key, type, items, resultItem, ingredientTypes, materialChoiceExtra, slotIconItem);
    }

    void fillCraftingSlots(Inventory gui, SavedRecipe recipe, IngredientType[] ingredientTypes, List<List<Material>> materialChoiceExtra) {
        // recipe.getIngredientMap() -> {a: _, b: _, c: _, ..., h: _, i: _} of the grid
        for (int i = 0; i < 9; i++) {
            fillCraftingSlot(i, gui, recipe, ingredientTypes, materialChoiceExtra);
        }
    }

    void fillCraftingSlot(int slot, Inventory gui, SavedRecipe recipe, IngredientType[] ingredientTypes, List<List<Material>> materialChoiceExtra) {
        int[] SLOTS = {SLOT1, SLOT2, SLOT3, SLOT4, SLOT5, SLOT6, SLOT7, SLOT8, SLOT9};
        switch (ingredientTypes[slot]) {
            case NORMAL, EXACT_CHOICE -> {
                gui.setItem(SLOTS[slot], recipe.items().get(slot));
            }
            case MATERIAL_CHOICE -> {
                List<Material> items = materialChoiceExtra.get(slot);
                String[] names = new String[Math.min(5, items.size())];
                for (int j = 0; j < names.length; j++) {
                    names[j] = items.get(j).name();
                }
                String itemsDisplay = "§7" + String.join(", ", names) + (items.size() > 5 ? ", ..." : "");
                gui.setItem(SLOTS[slot], Util.createItemStack(Material.END_PORTAL_FRAME, 1, "§bMaterial Choice",
                        "§eSHIFT RIGHT CLICK §fto edit!",
                        "§dThis is a placeholder item. It is not actually in the recipe. Removing",
                        "§dthis item from this menu does not have any effect.",
                        "§7Items:",
                        itemsDisplay
                ));
            }
        }
    }

    private ItemStack getDisplayItemStackForRecipeType(RecipeType type) {
        switch (type) {
            case SHAPED -> {
                return Util.createItemStack(Material.CRAFTING_TABLE, 1, "§3Recipe Type",
                        "§f>> SHAPED",
                        "  §7The required items must be in this shape.",
                        "  §7If the grid is less than 3x3, the empty space is ignored.",
                        "§7>> SHAPELESS",
                        "§7>> STACKED ITEMS",
                        "§7>> STACKED ITEMS (SHAPELESS)",
                        "§eCLICK §7to toggle"
                );
            }
            case SHAPELESS -> {
                return Util.createItemStack(Material.CRAFTING_TABLE, 1, "§3Recipe Type",
                        "§7>> SHAPED",
                        "§f>> SHAPELESS",
                        "  §7The required items can be in any configuration.",
                        "§7>> STACKED ITEMS",
                        "§7>> STACKED ITEMS (SHAPELESS)",
                        "§eCLICK §7to toggle"
                );
            }
            case STACKED_ITEMS -> {
                return Util.createItemStack(Material.CRAFTING_TABLE, 1, "§3Recipe Type",
                        "§7>> SHAPED",
                        "§7>> SHAPELESS",
                        "§f>> STACKED ITEMS",
                        "  §7The required items in the grid can be stacked, so rather than",
                        "  §7requiring 9 items, you can require 9 stacks of items.",
                        "  §7The recipe is shaped and only normal and exact choice are supported.",
                        "§7>> STACKED ITEMS (SHAPELESS)",
                        "§eCLICK §7to toggle"
                );
            }
            case STACKED_ITEMS_SHAPELESS -> {
                return Util.createItemStack(Material.CRAFTING_TABLE, 1, "§3Recipe Type",
                        "§7>> SHAPED",
                        "§7>> SHAPELESS",
                        "§7>> STACKED ITEMS",
                        "§f>> STACKED ITEMS (SHAPELESS)",
                        "  §7Shapeless version of stacked items.",
                        "  §7Different quantities of the same item are allowed, but players will",
                        "  §7need the exact amounts in those stacks (not just the total amount).",
                        "§eCLICK §7to toggle"
                );
            }
            default -> {
                throw new IllegalArgumentException();
            }
        }
    }

}
