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

import io.github.greatericontop.greatcrafts.GreatCrafts;
import io.github.greatericontop.greatcrafts.internal.Util;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class RecipeListMenu implements Listener {
    private static final String INV_NAME = "§3Recipes";
    private static final int PREV_PAGE_SLOT = 45;
    private static final int INDICATOR_SLOT = 49;
    private static final int NEXT_PAGE_SLOT = 53;
    private static final int CRAFTS_PER_PAGE = 36;

    private final NamespacedKey recipeKeyPDC;
    private final NamespacedKey pageNumberIndicatorPDC;
    private final NamespacedKey searchQueryPDC;
    private final GreatCrafts plugin;
    public RecipeListMenu(GreatCrafts plugin) {
        this.plugin = plugin;
        this.recipeKeyPDC = new NamespacedKey(plugin, "recipeKey");
        this.pageNumberIndicatorPDC = new NamespacedKey(plugin, "pageNumberIndicator");
        this.searchQueryPDC = new NamespacedKey(plugin, "searchQuery");
    }

    private void updateInventory(Player player, List<SavedRecipe> allRecipes, Inventory gui, int visualPageNumber, boolean shouldLookupSearchQuery, @Nullable String searchQuery) {
        if (shouldLookupSearchQuery) {
            // read it from slot INDICATOR_SLOT
            searchQuery = gui.getItem(INDICATOR_SLOT).getItemMeta().getPersistentDataContainer().get(searchQueryPDC, PersistentDataType.STRING);
        }
        int indexStart = CRAFTS_PER_PAGE * (visualPageNumber - 1);
        for (int i = 0; i < 54; i++) {
            gui.setItem(i, null);
        }
        // Take sub-list that contains our search query
        String finalSearchQuery = searchQuery;
        Stream<SavedRecipe> allViewableRecipes = allRecipes.stream().filter(
                r -> plugin.recipePermissionRequirements.get(r.key().toString()) == null
                        || player.hasPermission(plugin.recipePermissionRequirements.get(r.key().toString())));
        List<SavedRecipe> searchResults;
        if (searchQuery == null) {
            searchResults = allViewableRecipes.toList();
        } else {
            searchResults = allViewableRecipes
                    .filter(savedRecipe -> savedRecipe.key().toString().contains(finalSearchQuery))
                    .toList();
        }
        for (int i = indexStart; i < indexStart+CRAFTS_PER_PAGE; i++) {
            if (i >= searchResults.size())  break;
            SavedRecipe savedRecipe = searchResults.get(i);
            ItemStack icon = savedRecipe.iconItem();
            ItemMeta im = icon.getItemMeta();
            ItemStack resultItem = savedRecipe.result();
            String resultName = (resultItem.hasItemMeta() && resultItem.getItemMeta().hasDisplayName())
                    ? resultItem.getItemMeta().getDisplayName() : resultItem.getType().getKey().getKey();
            String resultDisplay = String.format("  §f%s §7x§f%d%s",
                    resultName, resultItem.getAmount(),
                    resultItem.hasItemMeta() ? " §7(+NBT)" : "");
            im.getPersistentDataContainer().set(recipeKeyPDC, PersistentDataType.STRING, savedRecipe.key().toString());
            Util.appendLore(im, "", "", "§dResult item:", resultDisplay, "§dKey:", "  §7"+savedRecipe.key());
            icon.setItemMeta(im);
            gui.setItem(i % CRAFTS_PER_PAGE, icon);
        }
        int totalPages = (int) Math.ceil(searchResults.size() / (double) CRAFTS_PER_PAGE);
        // Previous page
        if (visualPageNumber != 1) {
            ItemStack prevPage = Util.createItemStack(Material.ARROW, 1, "§fPrevious Page");
            gui.setItem(PREV_PAGE_SLOT, prevPage);
        }
        // Next page
        if (visualPageNumber != totalPages) {
            ItemStack nextPage = Util.createItemStack(Material.ARROW, 1, "§fNext Page");
            gui.setItem(NEXT_PAGE_SLOT, nextPage);
        }
        // Page number indicator
        String searchQueryLine = searchQuery == null ? "§7Showing all recipes" : "§7Currently searching for: §f"+searchQuery;
        ItemStack nextPage = Util.createItemStack(Material.PAPER, 1,
                String.format("§6Page §e%d §6/ §e%d", visualPageNumber, totalPages),
                "§3Left click to view, right click to edit",
                searchQueryLine,
                "§8You can use §7/recipes <search keyword> §8to",
                "§8search for specific recipes!");
        ItemMeta im = nextPage.getItemMeta();
        im.getPersistentDataContainer().set(pageNumberIndicatorPDC, PersistentDataType.INTEGER, visualPageNumber);
        if (searchQuery != null) {
            im.getPersistentDataContainer().set(searchQueryPDC, PersistentDataType.STRING, searchQuery);
        }
        nextPage.setItemMeta(im);
        gui.setItem(INDICATOR_SLOT, nextPage);
    }

    public void openNew(Player player, String searchQuery) {
        List<SavedRecipe> allRecipes = plugin.recipeManager.getAllSavedRecipes();
        if (allRecipes.isEmpty()) {
            plugin.languager.commandErrorRecipeListEmpty(player);
            return;
        }
        if (searchQuery != null) {
            if (allRecipes.stream().filter(savedRecipe -> savedRecipe.key().toString().contains(searchQuery)).toList().isEmpty()) {
                plugin.languager.commandErrorRecipeListNothingMatches(player);
                return;
            }
        }
        Inventory gui = Bukkit.createInventory(null, 54, INV_NAME);
        updateInventory(player, allRecipes, gui, 1, false, searchQuery);
        // (Does not get added to :playerMainInventories:)
        player.openInventory(gui);
    }

    @EventHandler()
    public void onClick(InventoryClickEvent event) {
        Inventory gui = event.getClickedInventory();
        if (gui == null) return;
        if (!event.getView().getTitle().equals(INV_NAME)) return;
        event.setCancelled(true); // cancel all when inventory open (including, e.g., shift-clicking bottom inventory) to prevent dupes in survival
        if (!event.getView().getTopInventory().equals(event.getClickedInventory())) return; // must click top inventory
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (slot == PREV_PAGE_SLOT) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                // If the arrow was removed and this slot is blank, then don't change the page
                return;
            }
            int pageNum = gui.getItem(INDICATOR_SLOT).getItemMeta().getPersistentDataContainer().get(pageNumberIndicatorPDC, PersistentDataType.INTEGER);
            List<SavedRecipe> allRecipes = plugin.recipeManager.getAllSavedRecipes();
            updateInventory(player, allRecipes, gui, pageNum-1, true, null);
        } else if (slot == NEXT_PAGE_SLOT) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            int pageNum = gui.getItem(INDICATOR_SLOT).getItemMeta().getPersistentDataContainer().get(pageNumberIndicatorPDC, PersistentDataType.INTEGER);
            List<SavedRecipe> allRecipes = plugin.recipeManager.getAllSavedRecipes();
            updateInventory(player, allRecipes, gui, pageNum+1, true, null);
        } else if (slot < CRAFTS_PER_PAGE) {
            ItemStack itemClicked = event.getCurrentItem();
            if (itemClicked == null || itemClicked.getType() == Material.AIR) return;
            ItemMeta im = itemClicked.getItemMeta();
            String recipeKey = im.getPersistentDataContainer().get(recipeKeyPDC, PersistentDataType.STRING);
            player.closeInventory();
            if (event.getClick() == ClickType.RIGHT) {
                player.chat(String.format("/editrecipe %s", recipeKey));
            } else {
                player.chat(String.format("/viewrecipe %s", recipeKey));
            }
        }
    }

}
