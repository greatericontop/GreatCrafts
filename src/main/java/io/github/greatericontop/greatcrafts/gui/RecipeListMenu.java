package io.github.greatericontop.greatcrafts.gui;

import io.github.greatericontop.greatcrafts.GreatCrafts;
import io.github.greatericontop.greatcrafts.Util;
import io.github.greatericontop.greatcrafts.internal.SavedRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class RecipeListMenu implements Listener {
    private static final String INV_NAME = "§3Recipes";
    private static final int PREV_PAGE_SLOT = 45;
    private static final int PAGE_NUMBER_INDICATOR_SLOT = 49;
    private static final int NEXT_PAGE_SLOT = 53;
    private static final int CRAFTS_PER_PAGE = 36;

    private final NamespacedKey recipeKeyPDC;
    private final NamespacedKey pageNumberIndicatorPDC;
    private final GreatCrafts plugin;
    public RecipeListMenu(GreatCrafts plugin) {
        this.plugin = plugin;
        this.recipeKeyPDC = new NamespacedKey(plugin, "recipeKey");
        this.pageNumberIndicatorPDC = new NamespacedKey(plugin, "pageNumberIndicator");
    }

    private void updateInventory(List<SavedRecipe> allRecipes, Inventory gui, int visualPageNumber) {
        int indexStart = CRAFTS_PER_PAGE * (visualPageNumber - 1);
        for (int i = 0; i < 54; i++) {
            gui.setItem(i, null);
        }
        for (int i = indexStart; i < indexStart+CRAFTS_PER_PAGE; i++) {
            if (i >= allRecipes.size())  break;
            SavedRecipe savedRecipe = allRecipes.get(i);
            ItemStack icon = savedRecipe.iconItem();
            ItemMeta im = icon.getItemMeta();
            ItemStack resultItem = savedRecipe.recipe().getResult();
            String resultName = (resultItem.hasItemMeta() && resultItem.getItemMeta().hasDisplayName())
                    ? resultItem.getItemMeta().getDisplayName() : "§8§o"+resultItem.getType().getKey().getKey();
            String resultDisplay = String.format("%s x%d%s",
                    resultName, resultItem.getAmount(),
                    resultItem.hasItemMeta() ? " §8§o(+NBT)" : "");
            im.getPersistentDataContainer().set(recipeKeyPDC, PersistentDataType.STRING, savedRecipe.recipe().getKey().toString());
            Util.appendLore(im, "", "", resultDisplay, "§8§o"+savedRecipe.recipe().getKey());
            icon.setItemMeta(im);
            gui.setItem(i, icon);
        }
        int totalPages = (int) Math.ceil(allRecipes.size() / (double) CRAFTS_PER_PAGE);
        // Previous page
        if (visualPageNumber != 0) {
            ItemStack prevPage = Util.createItemStack(Material.ARROW, 1, "Previous Page");
            gui.setItem(PREV_PAGE_SLOT, prevPage);
        }
        // Next page
        if (visualPageNumber != totalPages) {
            ItemStack nextPage = Util.createItemStack(Material.ARROW, 1, "Next Page");
            gui.setItem(NEXT_PAGE_SLOT, nextPage);
        }
        // Page number indicator
        ItemStack nextPage = Util.createItemStack(Material.PAPER, 1, String.format("Page %d / %d", visualPageNumber, totalPages));
        ItemMeta im = nextPage.getItemMeta();
        im.getPersistentDataContainer().set(pageNumberIndicatorPDC, PersistentDataType.INTEGER, visualPageNumber);
        nextPage.setItemMeta(im);
        gui.setItem(PAGE_NUMBER_INDICATOR_SLOT, nextPage);
    }

    public void openNew(Player player) {
        List<SavedRecipe> allRecipes = plugin.recipeManager.getAllRecipes();
        Inventory gui = Bukkit.createInventory(null, 54, INV_NAME);
        updateInventory(allRecipes, gui, 1);
        // (Does not get added to :playerMainInventories:)
        player.openInventory(gui);
    }

    @EventHandler()
    public void onClick(InventoryClickEvent event) {
        Inventory gui = event.getClickedInventory();
        if (gui == null) return;
        if (!event.getView().getTitle().equals(INV_NAME)) return;
        event.setCancelled(true); // cancel all when inventory open (including, e.g., shift-clicking bottom inventory)
        if (!event.getView().getTopInventory().equals(event.getClickedInventory())) return; // must click top inventory
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (slot == PREV_PAGE_SLOT) {
            int pageNum = gui.getItem(PAGE_NUMBER_INDICATOR_SLOT).getItemMeta().getPersistentDataContainer().get(pageNumberIndicatorPDC, PersistentDataType.INTEGER);
            List<SavedRecipe> allRecipes = plugin.recipeManager.getAllRecipes();
            updateInventory(allRecipes, gui, pageNum-1);
        } else if (slot == NEXT_PAGE_SLOT) {
            int pageNum = gui.getItem(PAGE_NUMBER_INDICATOR_SLOT).getItemMeta().getPersistentDataContainer().get(pageNumberIndicatorPDC, PersistentDataType.INTEGER);
            List<SavedRecipe> allRecipes = plugin.recipeManager.getAllRecipes();
            updateInventory(allRecipes, gui, pageNum+1);
        } else if (slot < CRAFTS_PER_PAGE) {
            ItemStack itemClicked = event.getCurrentItem();
            if (itemClicked == null || itemClicked.getType() == Material.AIR) return;
            ItemMeta im = itemClicked.getItemMeta();
            String recipeKey = im.getPersistentDataContainer().get(recipeKeyPDC, PersistentDataType.STRING);
            player.closeInventory();
            player.chat(String.format("/editrecipe %s", recipeKey));
        }
    }

}
