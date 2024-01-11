package io.github.greatericontop.greatcrafts;

import io.github.greatericontop.greatcrafts.commands.AddRecipeCommand;
import io.github.greatericontop.greatcrafts.commands.EditRecipeCommand;
import io.github.greatericontop.greatcrafts.commands.ReloadRecipesCommand;
import io.github.greatericontop.greatcrafts.gui.CraftEditor;
import io.github.greatericontop.greatcrafts.gui.ExactChoiceToggler;
import io.github.greatericontop.greatcrafts.gui.GUIManager;
import io.github.greatericontop.greatcrafts.gui.InventoryCloseListener;
import io.github.greatericontop.greatcrafts.gui.MaterialChoiceToggler;
import io.github.greatericontop.greatcrafts.internal.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class GreatCrafts extends JavaPlugin {

    public YamlConfiguration recipes;

    public RecipeManager recipeManager;

    public CraftEditor guiCraftEditor;
    public ExactChoiceToggler guiExactChoiceToggler;
    public MaterialChoiceToggler guiMaterialChoiceToggler;

    @Override
    public void onEnable() {

        File recipeFile = new File(this.getDataFolder(), "recipes.yml");
        recipes = YamlConfiguration.loadConfiguration(recipeFile);

        recipeManager = new RecipeManager(this);

        this.getCommand("addrecipe").setExecutor(new AddRecipeCommand(this));
        this.getCommand("editrecipe").setExecutor(new EditRecipeCommand(this));
        this.getCommand("reloadrecipes").setExecutor(new ReloadRecipesCommand(this));

        GUIManager guiManager = new GUIManager(this);
        guiCraftEditor = new CraftEditor(guiManager);
        this.getServer().getPluginManager().registerEvents(guiCraftEditor, this);
        guiExactChoiceToggler = new ExactChoiceToggler(guiManager);
        this.getServer().getPluginManager().registerEvents(guiExactChoiceToggler, this);
        guiMaterialChoiceToggler = new MaterialChoiceToggler(guiManager);
        this.getServer().getPluginManager().registerEvents(guiMaterialChoiceToggler, this);
        this.getServer().getPluginManager().registerEvents(new InventoryCloseListener(guiManager), this);




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