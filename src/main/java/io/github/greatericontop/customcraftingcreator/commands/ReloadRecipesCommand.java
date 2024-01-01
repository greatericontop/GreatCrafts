package io.github.greatericontop.customcraftingcreator.commands;

import io.github.greatericontop.customcraftingcreator.CustomCraftingCreator;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public class ReloadRecipesCommand implements CommandExecutor {

    private final CustomCraftingCreator plugin;
    public ReloadRecipesCommand(CustomCraftingCreator plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        List<ShapedRecipe> recipes = plugin.recipeManager.getAllRecipesShaped();
        for (ShapedRecipe recipe : recipes) {
            Bukkit.removeRecipe(recipe.getKey());
            Bukkit.addRecipe(recipe);
        }
        sender.sendMessage(String.format("§3Successfully reloaded %s recipes.", recipes.size()));
        sender.sendMessage("§eNote: §3Players still need to reconnect to see the recipe client-side, but it will work on the server.");

        return true;
    }

}
