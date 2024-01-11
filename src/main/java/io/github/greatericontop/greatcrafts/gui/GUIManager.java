package io.github.greatericontop.greatcrafts.gui;

import io.github.greatericontop.greatcrafts.GreatCrafts;
import io.github.greatericontop.greatcrafts.internal.RecipeManager;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager {

    public final Map<UUID, Map<String, Object>> guiData = new HashMap<>();
    public final Map<UUID, Inventory> playerMainInventories = new HashMap<>();

    private final GreatCrafts plugin;
    public GreatCrafts getPlugin() {
        return plugin;
    }

    public GUIManager(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    public RecipeManager getRecipeManager() {
        return plugin.recipeManager;
    }

}
