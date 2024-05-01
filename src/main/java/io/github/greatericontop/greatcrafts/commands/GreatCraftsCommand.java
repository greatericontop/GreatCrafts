package io.github.greatericontop.greatcrafts.commands;

import io.github.greatericontop.greatcrafts.GreatCrafts;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GreatCraftsCommand implements CommandExecutor {

    private final GreatCrafts plugin;
    public GreatCraftsCommand(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§9------------------------------------------------------------");
        sender.sendMessage("");
        sender.sendMessage(String.format("§aGreat§bCrafts §7v%s", plugin.getDescription().getVersion()));
        sender.sendMessage("");
        sender.sendMessage(String.format("§b%d §3recipes", plugin.recipeManager.getAllSavedRecipes().size()));
        sender.sendMessage("");
        sender.sendMessage("§3/recipes");
        sender.sendMessage("§3/viewrecipe ...");
        sender.sendMessage("§3/addrecipe ...");
        sender.sendMessage("§3/editrecipe ...");
        sender.sendMessage("§3/reloadrecipes");
        sender.sendMessage("");
        sender.sendMessage("§9------------------------------------------------------------");
        return true;
    }

}
