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

    private final NamespacedKey recipeKeyPDC;
    private final GreatCrafts plugin;
    public RecipeListMenu(GreatCrafts plugin) {
        this.plugin = plugin;
        this.recipeKeyPDC = new NamespacedKey(plugin, "recipeKey");
    }

    public void openNew(Player player) {
        List<SavedRecipe> allRecipes = plugin.recipeManager.getAllRecipes();
        Inventory gui = Bukkit.createInventory(null, 54, INV_NAME);
        int indexStart = 0; // TODO: pagination
        for (int i = 0; i <= 35; i++) {
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

        // (Does not get added to :playerMainInventories:)
        player.openInventory(gui);
    }

    @EventHandler()
    public void onClick(InventoryClickEvent event) {
        Inventory gui = event.getClickedInventory();
        if (gui == null)  return;
        if (!event.getView().getTitle().equals(INV_NAME))  return;
        if (!event.getView().getTopInventory().equals(event.getClickedInventory()))  return; // must click top inventory
        Player player = (Player) event.getWhoClicked();

        int slot = event.getSlot();
        // TODO: special slots for pagination

        ItemStack itemClicked = event.getCurrentItem();
        if (itemClicked == null || itemClicked.getType() == Material.AIR)  return;
        ItemMeta im = itemClicked.getItemMeta();
        String recipeKey = im.getPersistentDataContainer().get(recipeKeyPDC, PersistentDataType.STRING);
        event.setCancelled(true);
        player.closeInventory();
        player.chat(String.format("/editrecipe %s", recipeKey));
    }

}
