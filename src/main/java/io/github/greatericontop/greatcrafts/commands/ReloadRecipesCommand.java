package io.github.greatericontop.greatcrafts.commands;

import io.github.greatericontop.greatcrafts.GreatCrafts;
import io.github.greatericontop.greatcrafts.internal.RecipeLoader;
import io.github.greatericontop.greatcrafts.internal.SavedRecipeShaped;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public class ReloadRecipesCommand implements CommandExecutor {

    private final GreatCrafts plugin;
    public ReloadRecipesCommand(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        List<SavedRecipeShaped> recipes = plugin.recipeManager.getAllRecipes();
        for (SavedRecipeShaped savedRecipe : recipes) {
            ShapedRecipe recipe = savedRecipe.recipe();
            Bukkit.removeRecipe(recipe.getKey());
            RecipeLoader.addUnshrinkedShapedRecipe(recipe);
        }
        sender.sendMessage(String.format("§3Successfully reloaded %s recipes.", recipes.size()));
        sender.sendMessage("§eNote: §3Players still need to reconnect to see the recipe client-side, but it will work on the server.");

        return true;
    }

}
