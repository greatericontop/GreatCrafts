package io.github.greatericontop.greatcrafts.gui;

import io.github.greatericontop.greatcrafts.Util;
import io.github.greatericontop.greatcrafts.internal.IngredientType;
import io.github.greatericontop.greatcrafts.internal.SavedRecipe;
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
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

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
    private static final int SLOT_DISCARD = 51;
    private static final int SLOT_SAVE = 52;
    private static final int SLOT_SAVE_AND_ACTIVATE = 53;
    private static final Set<Integer> VALID_CLICK_SLOTS = Set.of(
            SLOT1, SLOT2, SLOT3, SLOT4, SLOT5, SLOT6, SLOT7, SLOT8, SLOT9,
            SLOT_RESULT, SLOT_ICON, SLOT_DISCARD, SLOT_SAVE, SLOT_SAVE_AND_ACTIVATE
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
        SavedRecipe savedRecipe = guiManager.getRecipeManager().getRecipeShaped(craftKey);
        ShapedRecipe recipe = savedRecipe.recipe();
        Inventory gui = Bukkit.createInventory(player, 54, INV_NAME);

        for (int i = 0; i < 54; i++) {
            gui.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        }
        fillCraftingSlots(gui, recipe, savedRecipe.ingredientTypes(), savedRecipe.materialChoiceExtra());
        gui.setItem(SLOT_RESULT, recipe.getResult());
        gui.setItem(SLOT_ICON, recipe.getResult());
        gui.setItem(45, Util.createItemStack(
                Material.ENCHANTED_BOOK, 1, "§bInfo",
                "§7Place items in the grid, result, and icon slots.",
                "§eSHIFT LEFT CLICK §7to toggle §fexact choice §7(exact NBT)",
                "§eSHIFT RIGHT CLICK §7to toggle §fmaterial choice §7(multiple items)",
                "§7Discard, save, or save and activate your changes."
        ));
        gui.setItem(SLOT_DISCARD, Util.createItemStack(Material.BARRIER, 1, "§cDiscard Changes"));
        gui.setItem(SLOT_SAVE, Util.createItemStack(Material.LIME_STAINED_GLASS, 1, "§aSave Changes"));
        gui.setItem(SLOT_SAVE_AND_ACTIVATE, Util.createItemStack(Material.LIME_CONCRETE, 1, "§aSave & Activate Changes",
                "§fThis reloads the current recipe and applies the changes immediately.",
                "§eNote: §fPlayers still need to reconnect to see the recipe client-side, but it will work on the server.",
                "§7You can also use §e/reloadrecipes §7to reload all recipes."
        ));

        Map<String, Object> data = new HashMap<>();
        data.put("recipe", recipe);
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

        if (slot == SLOT_DISCARD || slot == SLOT_SAVE || slot == SLOT_SAVE_AND_ACTIVATE) {
            if (slot == SLOT_SAVE || slot == SLOT_SAVE_AND_ACTIVATE) {
                ShapedRecipe recipe = (ShapedRecipe) data.get("recipe");
                ShapedRecipe newRecipe = saveLayout(recipe.getKey(), gui, (IngredientType[]) data.get("ingredientTypes"));
                guiManager.getRecipeManager().setRecipeShaped(newRecipe.getKey().toString(),
                        new SavedRecipe(newRecipe, (IngredientType[]) data.get("ingredientTypes"), (List<List<Material>>) data.get("materialChoiceExtra")));
                if (slot == SLOT_SAVE_AND_ACTIVATE) {
                    Bukkit.removeRecipe(newRecipe.getKey());
                    Bukkit.addRecipe(newRecipe);
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



    private void fillCraftingSlots(Inventory gui, ShapedRecipe recipe, IngredientType[] ingredientTypes, List<List<Material>> materialChoiceExtra) {
        // recipe.getIngredientMap() -> {a: _, b: _, c: _, ..., h: _, i: _} of the grid
        int[] SLOTS = {SLOT1, SLOT2, SLOT3, SLOT4, SLOT5, SLOT6, SLOT7, SLOT8, SLOT9};
        char[] KEYS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};
        for (int i = 0; i < 9; i++) {
            switch (ingredientTypes[i]) {
                case NORMAL -> {
                    gui.setItem(SLOTS[i], recipe.getIngredientMap().get(KEYS[i]));
                }
                case EXACT_CHOICE -> {
                    RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) recipe.getChoiceMap().get(KEYS[i]);
                    gui.setItem(SLOTS[i], exactChoice.getItemStack());
                }
                case MATERIAL_CHOICE -> {
                    List<Material> items = materialChoiceExtra.get(i);
                    String[] names = new String[Math.min(5, items.size())];
                    for (int j = 0; j < names.length; j++) {
                        names[j] = items.get(j).name();
                    }
                    String itemsDisplay = "§7" + String.join(", ", names) + (items.size() > 5 ? ", ..." : "");
                    gui.setItem(SLOTS[i], Util.createItemStack(Material.END_PORTAL_FRAME, 1, "§bMaterial Choice",
                            "§eSHIFT RIGHT CLICK §fto edit!",
                            "§dThis is a placeholder item. It is not actually in the recipe. Removing",
                            "§dthis item from this menu does not have any effect.",
                            "§7Items: §8(possibly outdated)",
                            itemsDisplay
                    ));
                }
            }
        }
    }

    // Save the layout in the GUI into a new recipe.
    // NOTE: This modifies :ingredientTypes: in place.
    //       Empty ExactChoice slots are changed to normal slots
    private ShapedRecipe saveLayout(NamespacedKey namespacedKey, Inventory gui, IngredientType[] ingredientTypes) {
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
        // Ingredient must be set AFTER shape is set
        for (int i = 0; i < 9; i++) {
            if (slots[i] == null || slots[i].getType() == Material.AIR)  continue;
            char symbol = (char) ('a' + i);
            switch (ingredientTypes[i]) {
                case NORMAL -> {
                    newRecipe.setIngredient(symbol, slots[i].getType());
                }
                case EXACT_CHOICE -> {
                    RecipeChoice.ExactChoice exactChoice = new RecipeChoice.ExactChoice(slots[i]);
                    newRecipe.setIngredient(symbol, exactChoice);
                }
                case MATERIAL_CHOICE -> {
                    newRecipe.setIngredient(symbol, slots[i].getType()); // TODO: placeholder
                }
                default -> {
                    throw new RuntimeException();
                }
            }
        }
        // Fix :ingredientType: empty ExactChoice slots converted to normal (MaterialChoice is left unchanged)
        for (int i = 0; i < 9; i++) {
            if ((slots[i] == null || slots[i].getType() == Material.AIR) && ingredientTypes[i] == IngredientType.EXACT_CHOICE) {
                ingredientTypes[i] = IngredientType.NORMAL;
            }
        }
        // TODO: smaller shapes
        return newRecipe;
    }

}
