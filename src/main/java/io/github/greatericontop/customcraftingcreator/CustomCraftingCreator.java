package io.github.greatericontop.customcraftingcreator;

import io.github.greatericontop.customcraftingcreator.commands.AddRecipeCommand;
import io.github.greatericontop.customcraftingcreator.commands.EditRecipeCommand;
import io.github.greatericontop.customcraftingcreator.commands.ReloadRecipesCommand;
import io.github.greatericontop.customcraftingcreator.gui.CraftEditor;
import io.github.greatericontop.customcraftingcreator.gui.GUIManager;
import io.github.greatericontop.customcraftingcreator.internal.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CustomCraftingCreator extends JavaPlugin {

    public YamlConfiguration recipes;

    public RecipeManager recipeManager;

    public CraftEditor guiEditCraft;

    @Override
    public void onEnable() {

        File recipeFile = new File(this.getDataFolder(), "recipes.yml");
        recipes = YamlConfiguration.loadConfiguration(recipeFile);

        recipeManager = new RecipeManager(this);

        this.getCommand("addrecipe").setExecutor(new AddRecipeCommand(this));
        this.getCommand("editrecipe").setExecutor(new EditRecipeCommand(this));
        this.getCommand("reloadrecipes").setExecutor(new ReloadRecipesCommand(this));

        GUIManager guiManager = new GUIManager(this);
        guiEditCraft = new CraftEditor(guiManager);
        this.getServer().getPluginManager().registerEvents(guiEditCraft, this);
        // TODO: InventoryCloseListener




        Bukkit.getScheduler().runTaskTimer(this, this::saveAll, 1200L, 1200L);

    }

    @Override
    public void onDisable() {
        saveAll();
    }

    public void saveAll() {
        try {
            recipes.save(new File(this.getDataFolder(), "recipes.yml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}