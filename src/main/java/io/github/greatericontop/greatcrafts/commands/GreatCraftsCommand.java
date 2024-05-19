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
        sender.sendMessage("§9--------------------------------------------------");
        sender.sendMessage("");
        sender.sendMessage(String.format("§aGreat§bCrafts §7v%s", plugin.getDescription().getVersion()));
        sender.sendMessage("");
        sender.sendMessage(String.format("§b%d §3recipes", plugin.recipeManager.getAllSavedRecipes().size()));
        sender.sendMessage("");
        sender.sendMessage("§e/recipes");
        sender.sendMessage("§e/viewrecipe");
        sender.sendMessage("§e/addrecipe");
        sender.sendMessage("§e/editrecipe");
        sender.sendMessage("§e/reloadrecipes");
        sender.sendMessage("");
        sender.sendMessage("§9--------------------------------------------------");
        return true;
    }

}
