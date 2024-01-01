package io.github.greatericontop.customcraftingcreator.gui;

import io.github.greatericontop.customcraftingcreator.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EditCraft implements Listener {
    private static final String INV_DESC = "§bEdit Craft";
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
    private static final int SLOT_DISCARD = 51;
    private static final int SLOT_SAVE = 52;
    private static final int SLOT_SAVE_AND_ACTIVATE = 53;
    private static final Set<Integer> VALID_CLICK_SLOTS = Set.of(
            SLOT1, SLOT2, SLOT3, SLOT4, SLOT5, SLOT6, SLOT7, SLOT8, SLOT9,
            SLOT_RESULT, SLOT_ICON, SLOT_DISCARD, SLOT_SAVE, SLOT_SAVE_AND_ACTIVATE
    );

    private final GUIManager guiManager;
    public EditCraft(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public void openNew(Player player, String craftKey) {
        ShapedRecipe recipe = guiManager.getRecipeManager().getRecipeShaped(craftKey);
        UUID inventoryUUID = UUID.randomUUID();
        Inventory gui = Bukkit.createInventory(player, 54, INV_DESC);

        for (int i = 0; i < 54; i++) {
            gui.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        }
        gui.setItem(0, Util.createItemStack(Material.BLACK_STAINED_GLASS_PANE, 1, inventoryUUID.toString()));
        // recipe.getIngredientMap() -> {a: _, b: _, c: _, ..., h: _, i: _} of the grid
        gui.setItem(SLOT1, recipe.getIngredientMap().getOrDefault('a', null));
        gui.setItem(SLOT2, recipe.getIngredientMap().getOrDefault('b', null));
        gui.setItem(SLOT3, recipe.getIngredientMap().getOrDefault('c', null));
        gui.setItem(SLOT4, recipe.getIngredientMap().getOrDefault('d', null));
        gui.setItem(SLOT5, recipe.getIngredientMap().getOrDefault('e', null));
        gui.setItem(SLOT6, recipe.getIngredientMap().getOrDefault('f', null));
        gui.setItem(SLOT7, recipe.getIngredientMap().getOrDefault('g', null));
        gui.setItem(SLOT8, recipe.getIngredientMap().getOrDefault('h', null));
        gui.setItem(SLOT9, recipe.getIngredientMap().getOrDefault('i', null));
        gui.setItem(SLOT_RESULT, recipe.getResult());
        gui.setItem(SLOT_ICON, recipe.getResult());
        gui.setItem(45, Util.createItemStack(
                Material.PAPER, 1, "§bInfo",
                "§7Place items in the grid, result, and icon slots.",
                "§7Discard, save, or save and activate your changes."
        ));
        gui.setItem(SLOT_DISCARD, Util.createItemStack(Material.BARRIER, 1, "§cDiscard Changes"));
        gui.setItem(SLOT_SAVE, Util.createItemStack(Material.LIME_STAINED_GLASS, 1, "§aSave Changes"));
        gui.setItem(SLOT_SAVE_AND_ACTIVATE, Util.createItemStack(Material.LIME_CONCRETE, 1, "§aSave & Activate Changes",
                "§fThis reloads the current recipe and applies the changes immediately.",
                "§eNote: §fPlayers still need to reconnect to see the recipe client-side, but it will work on the server.",
                "§7You can also use §e/reloadrecipes §7to reload all recipes."));

        Map<String, Object> data = new HashMap<>();
        data.put("recipe", recipe);
        guiManager.guiData.put(inventoryUUID, data);
        guiManager.playerMainInventories.put(player.getUniqueId(), player.getInventory());
        player.openInventory(gui);
    }

    @EventHandler()
    public void onClick(InventoryClickEvent event) {
        Inventory gui = event.getClickedInventory();
        if (gui == null)  return;
        if (!event.getView().getTitle().equals(INV_DESC))  return;
        if (!event.getView().getTopInventory().equals(event.getClickedInventory()))  return; // must click top inventory
        int slot = event.getSlot();
        if (!VALID_CLICK_SLOTS.contains(slot)) {
            event.setCancelled(true);
            return;
        }
        Player player = (Player) event.getWhoClicked();
        UUID inventoryUUID = UUID.fromString(gui.getItem(0).getItemMeta().getDisplayName());
        Map<String, Object> data = guiManager.guiData.get(inventoryUUID);

        if (slot == SLOT_DISCARD || slot == SLOT_SAVE || slot == SLOT_SAVE_AND_ACTIVATE) {
            if (slot == SLOT_SAVE || slot == SLOT_SAVE_AND_ACTIVATE) {
                ShapedRecipe recipe = (ShapedRecipe) data.get("recipe");
                ShapedRecipe newRecipe = saveLayout(recipe.getKey(), gui);
                guiManager.getRecipeManager().setRecipeShaped(newRecipe.getKey().toString(), newRecipe);
                if (slot == SLOT_SAVE_AND_ACTIVATE) {
                    Bukkit.removeRecipe(newRecipe.getKey());
                    Bukkit.addRecipe(newRecipe);
                }
                Util.successSound(player);
            }
            guiManager.guiData.remove(inventoryUUID);
            guiManager.playerMainInventories.remove(player.getUniqueId());
            player.closeInventory();
        }
    }

    private ShapedRecipe saveLayout(NamespacedKey namespacedKey, Inventory gui) {
        ShapedRecipe newRecipe = new ShapedRecipe(namespacedKey, gui.getItem(SLOT_RESULT));
        char[] layout = "         ".toCharArray();
        ItemStack[] slots = new ItemStack[]{
                gui.getItem(SLOT1), gui.getItem(SLOT2), gui.getItem(SLOT3),
                gui.getItem(SLOT4), gui.getItem(SLOT5), gui.getItem(SLOT6),
                gui.getItem(SLOT7), gui.getItem(SLOT8), gui.getItem(SLOT9)
        };
        for (int i = 0; i < 9; i++) {
            if (slots[i] == null || slots[i].getType() == Material.AIR)  continue;
            char symbol = (char) ('a' + i);
            layout[i] = symbol;
        }
        newRecipe.shape(
                new String(new char[]{layout[0], layout[1], layout[2]}),
                new String(new char[]{layout[3], layout[4], layout[5]}),
                new String(new char[]{layout[6], layout[7], layout[8]})
        );
        for (int i = 0; i < 9; i++) {
            if (slots[i] == null || slots[i].getType() == Material.AIR)  continue;
            char symbol = (char) ('a' + i);
            newRecipe.setIngredient(symbol, slots[i].getType()); // Ingredient must be set AFTER shape is set
        }
        // TODO: smaller shapes
        return newRecipe;
    }

}
