package io.github.greatericontop.greatcrafts.events;

import io.github.greatericontop.greatcrafts.GreatCrafts;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class PermissionRestrictionListener implements Listener {

    private final GreatCrafts plugin;
    public PermissionRestrictionListener(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW) // This cancels the event before the stacked items listener gets to it
    public void onCraftPermissionCheck(CraftItemEvent event) {
        Recipe rawRecipe = event.getRecipe();
        String stringKey;
        if (rawRecipe instanceof ShapedRecipe rawRecipeShaped) {
            stringKey = rawRecipeShaped.getKey().toString();
        } else if (rawRecipe instanceof ShapelessRecipe rawRecipeShapeless) {
            stringKey = rawRecipeShapeless.getKey().toString();
        } else {
            return; // May be some other type we don't care about
        }
        if (!plugin.recipeManager.isRecipeCustom(stringKey)) {
            return;
        }
        String permissionReq = plugin.recipePermissionRequirements.get(stringKey);
        if (permissionReq == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (!player.hasPermission(permissionReq)) {
            event.setCancelled(true);
            player.sendMessage("§cNo permission"); // TODO: LANG
        }

    }

    // Permission restriction for crafters is located in CrafterEvents

}
