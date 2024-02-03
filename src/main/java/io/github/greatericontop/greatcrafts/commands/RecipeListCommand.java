package io.github.greatericontop.greatcrafts.commands;

import io.github.greatericontop.greatcrafts.GreatCrafts;
import io.github.greatericontop.greatcrafts.Util;
import io.github.greatericontop.greatcrafts.internal.SavedRecipe;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RecipeListCommand implements CommandExecutor {

    private final GreatCrafts plugin;
    public RecipeListCommand(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cA player is required!");
            return true;
        }

        List<SavedRecipe> allRecipes = plugin.recipeManager.getAllRecipes();
        Inventory gui = Bukkit.createInventory(null, 54, "§3Recipes");
        int indexStart = 0; // TODO: pagination
        for (int i = 0; i <= 35; i++) {
            if (i >= allRecipes.size())  break;
            SavedRecipe savedRecipe = allRecipes.get(i);
            ItemStack icon = savedRecipe.iconItem();

            ItemStack resultItem = savedRecipe.recipe().getResult();
            String resultName = (icon.hasItemMeta() && icon.getItemMeta().hasDisplayName())
                    ? icon.getItemMeta().getDisplayName() : "§8§o"+resultItem.getType().getKey().getKey();
            String resultDisplay = String.format("%s x%d%s",
                    resultName, resultItem.getAmount(),
                    resultItem.hasItemMeta() ? " §8§o(+NBT)" : "");
            Util.appendLore(icon, "", "", resultDisplay, "§8§o"+savedRecipe.recipe().getKey());

            gui.setItem(i, icon);
        }






        player.openInventory(gui);
        return true;
    }

}
