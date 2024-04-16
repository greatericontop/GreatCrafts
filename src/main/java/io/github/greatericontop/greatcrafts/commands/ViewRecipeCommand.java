package io.github.greatericontop.greatcrafts.commands;

import io.github.greatericontop.greatcrafts.GreatCrafts;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ViewRecipeCommand implements CommandExecutor {

    private final GreatCrafts plugin;
    public ViewRecipeCommand(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cA player is required!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            return false;
        }

        String recipeName = args[0]; // TODO: maybe have distinct tagline vs namespace in the future
        //plugin.guiCraftReadOnlyViewer.openNew(player, recipeName);

        return true;
    }

}
