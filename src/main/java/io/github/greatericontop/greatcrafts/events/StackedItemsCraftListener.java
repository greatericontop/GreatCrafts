package io.github.greatericontop.greatcrafts.events;

import io.github.greatericontop.greatcrafts.GreatCrafts;
import io.github.greatericontop.greatcrafts.internal.datastructures.RecipeType;
import io.github.greatericontop.greatcrafts.internal.datastructures.SavedRecipe;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class StackedItemsCraftListener implements Listener {

    private final GreatCrafts plugin;
    public StackedItemsCraftListener(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(CraftItemEvent event) {
        // Our stacked items recipe is registered as a shaped recipe (in its basic form), so we know that this event
        // will always fire
        Recipe _rawRecipe = event.getRecipe();
        if (!(_rawRecipe instanceof ShapedRecipe _shapedRecipe)) {
            return;
        }
        NamespacedKey recipeKey = _shapedRecipe.getKey();
        // Check if it is a stacked items recipe
        SavedRecipe savedRecipe = plugin.recipeManager.getRecipe(recipeKey.toString());
        if (savedRecipe == null) {
            return;
        }
        if (savedRecipe.type() != RecipeType.STACKED_ITEMS) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        // Check for sufficient items
        for (int slotNum = 0; slotNum < 9; slotNum++) {
            ItemStack requiredItemStack = savedRecipe.items().get(slotNum);
            if (requiredItemStack == null) {
                continue;
            }
            // We already know that the material is right (including exact choice if applicable), just check counts
            int required = requiredItemStack.getAmount();
            if (event.getInventory().getItem(slotNum+1).getAmount() < required) { // slot 0 in the event inventory is the result
                player.sendMessage("§cYou don't have enough items in the crafting table!");
                player.sendMessage("§3This is a special §bstacked items §3recipe.");
                player.sendMessage("§3Check §f<will be implemented later> §3for more information.");
                return;
            }
        }
        // Remove them
        for (int slotNum = 0; slotNum < 9; slotNum++) {
            ItemStack requiredItemStack = savedRecipe.items().get(slotNum);
            if (requiredItemStack == null) {
                continue;
            }
            int required = requiredItemStack.getAmount();
            ItemStack stack = event.getInventory().getItem(slotNum+1); // see above
            stack.setAmount(stack.getAmount() - required);
            event.getInventory().setItem(slotNum+1, stack);
        }

        // Set result
        ItemStack result = savedRecipe.result().clone();

        //if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)





        if (player.getItemOnCursor() == null || player.getItemOnCursor().getType() == Material.AIR) {
            // Empty, simply set
            player.setItemOnCursor(result);
        } else if (player.getItemOnCursor().isSimilar(result)
                && player.getItemOnCursor().getAmount() + result.getAmount() <= result.getMaxStackSize()) {
            // Combine (sufficient stack size)
            player.getItemOnCursor().setAmount(result.getAmount() + player.getItemOnCursor().getAmount());
        }
//        else {
//            // Incompatible, place item in inventory
//            if (!player.getInventory().addItem(result).isEmpty()) {
//                // Drop item
//                player.getWorld().dropItemNaturally(player.getLocation(), result);
//            }
//        }

        // TODO: handle shift click?
    }

}
