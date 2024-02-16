package io.github.greatericontop.greatcrafts.commands;

import io.github.greatericontop.greatcrafts.GreatCrafts;
import org.bukkit.GameMode;
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
        if (player.getGameMode() != GameMode.CREATIVE) {
            player.sendMessage("§cYour gamemode must be creative to view and edit recipes!");
            return true;
        }
        if (args.length == 0) {
            plugin.guiRecipeListMenu.openNew(player, null);
            return true;
        } else if (args.length == 1) {
            plugin.guiRecipeListMenu.openNew(player, args[0]);
        } else {
            return false;
        }
        return true;
    }

}
