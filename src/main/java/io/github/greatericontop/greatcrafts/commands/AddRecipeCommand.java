package io.github.greatericontop.greatcrafts.commands;

import io.github.greatericontop.greatcrafts.GreatCrafts;
import io.github.greatericontop.greatcrafts.Util;
import io.github.greatericontop.greatcrafts.internal.IngredientType;
import io.github.greatericontop.greatcrafts.internal.SavedRecipe;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class AddRecipeCommand implements CommandExecutor {

    private final GreatCrafts plugin;
    public AddRecipeCommand(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cA player is required!");
            return true;
        }
        if (player.getGameMode() != GameMode.CREATIVE) {
            player.sendMessage("§cYour gamemode must be creative to edit recipes!");
            return true;
        }
        if (args.length == 0) {
            return false;
        }

        String recipeName = args[0];
        String[] recipeNameParts = recipeName.split(":");
        if (recipeNameParts.length != 2) {
            player.sendMessage("§cThe recipe must be in the format §4namespace:name§c!");
            return true;
        }
        NamespacedKey key = new NamespacedKey(recipeNameParts[0], recipeNameParts[1]);
        ShapedRecipe basicRecipe = new ShapedRecipe(key, new ItemStack(Material.EMERALD_BLOCK));
        basicRecipe.shape(" b ", "d f", " h ");
        basicRecipe.setIngredient('b', Material.EMERALD_ORE);
        basicRecipe.setIngredient('d', Material.EMERALD_ORE);
        basicRecipe.setIngredient('f', Material.EMERALD_ORE);
        basicRecipe.setIngredient('h', Material.EMERALD_ORE);
        ItemStack iconItem = Util.createItemStack(Material.EMERALD_BLOCK, 1, "§dDefault Icon",
                "§7This is the icon for your recipe. This is displayed in the menu", "§7and doesn't affect the craft.");
        plugin.recipeManager.setRecipeShaped(key.toString(), new SavedRecipe(basicRecipe,
                IngredientType.defaults(), Util.defaultMaterialChoiceExtra(), iconItem));
        plugin.guiCraftEditor.openNew(player, recipeName);

        return true;
    }

}
