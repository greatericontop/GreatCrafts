package io.github.greatericontop.greatcrafts.commands;

import io.github.greatericontop.greatcrafts.GreatCrafts;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        plugin.guiRecipeListMenu.openNew(player);
        return true;
    }

}
