package io.github.greatericontop.customcraftingcreator.commands;

import io.github.greatericontop.customcraftingcreator.CustomCraftingCreator;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EditRecipeCommand implements CommandExecutor {

    private final CustomCraftingCreator plugin;
    public EditRecipeCommand(CustomCraftingCreator plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cA player is required!");
            return true;
        }
        Player player = (Player) sender;
        if (player.getGameMode() != GameMode.CREATIVE) {
            player.sendMessage("§cYour gamemode must be creative to edit recipes!");
            return true;
        }
        if (args.length == 0) {
            return false;
        }

        String recipeName = args[0]; // TODO: maybe have distinct tagline vs namespace in the future
        plugin.guiEditCraft.openNew(player, recipeName);

        return true;
    }

}
