package io.github.greatericontop.greatcrafts;

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

import io.github.greatericontop.greatcrafts.commands.AddRecipeCommand;
import io.github.greatericontop.greatcrafts.commands.DeleteRecipeCommand;
import io.github.greatericontop.greatcrafts.commands.EditRecipeCommand;
import io.github.greatericontop.greatcrafts.commands.GCUtilCommand;
import io.github.greatericontop.greatcrafts.commands.GreatCraftsCommand;
import io.github.greatericontop.greatcrafts.commands.RecipeListCommand;
import io.github.greatericontop.greatcrafts.commands.ReloadRecipesCommand;
import io.github.greatericontop.greatcrafts.commands.ViewRecipeCommand;
import io.github.greatericontop.greatcrafts.commands.tabcompleters.AddRecipeTabCompleter;
import io.github.greatericontop.greatcrafts.commands.tabcompleters.GCUtilTabCompleter;
import io.github.greatericontop.greatcrafts.commands.tabcompleters.RecipeListTabCompleter;
import io.github.greatericontop.greatcrafts.commands.tabcompleters.ViewEditRecipeTabCompleter;
import io.github.greatericontop.greatcrafts.events.AutoUnlockListener;
import io.github.greatericontop.greatcrafts.events.InventoryCloseListener;
import io.github.greatericontop.greatcrafts.events.stackeditems.CrafterEvents;
import io.github.greatericontop.greatcrafts.events.stackeditems.StackedItemsCraftListener;
import io.github.greatericontop.greatcrafts.gui.CraftEditor;
import io.github.greatericontop.greatcrafts.gui.CraftReadOnlyViewer;
import io.github.greatericontop.greatcrafts.gui.ExactChoiceToggler;
import io.github.greatericontop.greatcrafts.gui.GUIManager;
import io.github.greatericontop.greatcrafts.gui.MaterialChoiceEditor;
import io.github.greatericontop.greatcrafts.gui.MaterialChoiceToggler;
import io.github.greatericontop.greatcrafts.gui.RecipeListMenu;
import io.github.greatericontop.greatcrafts.internal.Languager;
import io.github.greatericontop.greatcrafts.internal.RecipeManager;
import io.github.greatericontop.greatcrafts.internal.datastructures.AutoUnlockSetting;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GreatCrafts extends JavaPlugin {

    public AutoUnlockSetting autoUnlockSetting;
    public Map<String, AutoUnlockSetting> autoUnlockExceptions;
    public Languager languager;

    public YamlConfiguration recipes;

    public RecipeManager recipeManager;

    public CraftEditor guiCraftEditor;
    public CraftReadOnlyViewer guiCraftReadOnlyViewer;
    public ExactChoiceToggler guiExactChoiceToggler;
    public MaterialChoiceToggler guiMaterialChoiceToggler;
    public MaterialChoiceEditor guiMaterialChoiceEditor;
    public RecipeListMenu guiRecipeListMenu;

    @Override
    public void onEnable() {

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        updateConfigVars();

        File recipeFile = new File(this.getDataFolder(), "recipes.yml");
        recipes = YamlConfiguration.loadConfiguration(recipeFile);

        recipeManager = new RecipeManager(this);

        GreatCraftsCommand greatcraftscommand = new GreatCraftsCommand(this);
        this.getCommand("greatcrafts").setExecutor(greatcraftscommand);
        this.getCommand("greatcrafts").setTabCompleter(greatcraftscommand);
        this.getCommand("recipes").setExecutor(new RecipeListCommand(this));
        this.getCommand("recipes").setTabCompleter(new RecipeListTabCompleter());
        this.getCommand("viewrecipe").setExecutor(new ViewRecipeCommand(this));
        this.getCommand("viewrecipe").setTabCompleter(new ViewEditRecipeTabCompleter(this));
        this.getCommand("addrecipe").setExecutor(new AddRecipeCommand(this));
        this.getCommand("addrecipe").setTabCompleter(new AddRecipeTabCompleter());
        this.getCommand("editrecipe").setExecutor(new EditRecipeCommand(this));
        this.getCommand("editrecipe").setTabCompleter(new ViewEditRecipeTabCompleter(this));
        this.getCommand("deleterecipe").setExecutor(new DeleteRecipeCommand(this));
        this.getCommand("deleterecipe").setTabCompleter(new ViewEditRecipeTabCompleter(this));
        this.getCommand("reloadrecipes").setExecutor(new ReloadRecipesCommand(this));
        this.getCommand("greatcraftsutil").setExecutor(new GCUtilCommand(this));
        this.getCommand("greatcraftsutil").setTabCompleter(new GCUtilTabCompleter(this));

        GUIManager guiManager = new GUIManager(this);
        guiCraftEditor = new CraftEditor(guiManager);
        this.getServer().getPluginManager().registerEvents(guiCraftEditor, this);
        guiCraftReadOnlyViewer = new CraftReadOnlyViewer(guiManager);
        this.getServer().getPluginManager().registerEvents(guiCraftReadOnlyViewer, this);
        guiExactChoiceToggler = new ExactChoiceToggler(guiManager);
        this.getServer().getPluginManager().registerEvents(guiExactChoiceToggler, this);
        guiMaterialChoiceToggler = new MaterialChoiceToggler(guiManager);
        this.getServer().getPluginManager().registerEvents(guiMaterialChoiceToggler, this);
        guiMaterialChoiceEditor = new MaterialChoiceEditor(guiManager);
        this.getServer().getPluginManager().registerEvents(guiMaterialChoiceEditor, this);
        guiRecipeListMenu = new RecipeListMenu(this);
        this.getServer().getPluginManager().registerEvents(guiRecipeListMenu, this);

        this.getServer().getPluginManager().registerEvents(new AutoUnlockListener(this), this);
        this.getServer().getPluginManager().registerEvents(new InventoryCloseListener(guiManager), this);
        this.getServer().getPluginManager().registerEvents(new StackedItemsCraftListener(this), this);

        // Paper & Spigot: getBukkitVersion() -> "1.21-R0.1-SNAPSHOT"
        String rawVersion = Bukkit.getServer().getBukkitVersion();
        String minecraftVersion = rawVersion.split("-")[0];
        int majorVersion = Integer.parseInt(minecraftVersion.split("\\.")[1]);
        this.getLogger().info("Minecraft (major) version: "+majorVersion);

        if (majorVersion >= 21) {
            this.getLogger().info("(1.21+) Enabling support for crafter block");
            this.getServer().getPluginManager().registerEvents(new CrafterEvents(this), this);
        }




        Bukkit.getScheduler().runTaskTimer(this, this::saveAll, 1200L, 1200L);

        // Recipes are not loaded in by default, so do this (later)
        Bukkit.getScheduler().runTaskLater(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "greatcrafts:reloadrecipes"), 20L);

    }

    @Override
    public void onDisable() {
        saveAll();
    }

    public void updateConfigVars() {
        autoUnlockSetting = AutoUnlockSetting.fromConfig(this.getConfig().getString("automatically-unlock-recipes"));
        if (autoUnlockSetting == null) {
            this.getLogger().warning("config option automatically-unlock-recipes was missing or invalid");
            autoUnlockSetting = AutoUnlockSetting.NEVER;
        }
        this.getLogger().info(String.format("  autoUnlockSetting = %s", autoUnlockSetting.name()));
        Map<String, String> autoUnlockExceptionsRaw = (Map<String, String>) this.getConfig().get("automatically-unlock-recipes-exceptions");
        autoUnlockExceptions = new HashMap<>();
        for (Map.Entry<String, String> entry : autoUnlockExceptionsRaw.entrySet()) {
            AutoUnlockSetting setting = AutoUnlockSetting.fromConfig(entry.getValue());
            if (setting == null) {
                this.getLogger().warning(String.format("automatically-unlock-recipes-exceptions: invalid value for %s ('%s')", entry.getKey(), entry.getValue()));
            } else {
                this.getLogger().info(String.format("  automatically-unlock-recipes-exceptions: %s = %s", entry.getKey(), setting.name()));
                autoUnlockExceptions.put(entry.getKey(), setting);
            }
        }
        languager = new Languager(this);
        this.getLogger().info("  Languager ready!");
    }

    public void saveAll() {
        try {
            recipes.save(new File(this.getDataFolder(), "recipes.yml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}