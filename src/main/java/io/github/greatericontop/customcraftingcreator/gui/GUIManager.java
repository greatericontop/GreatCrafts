package io.github.greatericontop.customcraftingcreator.gui;

import io.github.greatericontop.customcraftingcreator.CustomCraftingCreator;
import io.github.greatericontop.customcraftingcreator.internal.RecipeManager;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager {

    public final Map<UUID, Map<String, Object>> guiData = new HashMap<>();
    public final Map<UUID, Inventory> playerMainInventories = new HashMap<>();

    private final CustomCraftingCreator plugin;
    public CustomCraftingCreator getPlugin() {
        return plugin;
    }

    public GUIManager(CustomCraftingCreator plugin) {
        this.plugin = plugin;
    }

    public RecipeManager getRecipeManager() {
        return plugin.recipeManager;
    }

}
