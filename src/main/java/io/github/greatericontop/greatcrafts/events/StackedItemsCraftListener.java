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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Map;

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
        // Check item count
        int maxCraftsAvailable = Integer.MAX_VALUE;
        for (int slotNum = 0; slotNum < 9; slotNum++) {
            ItemStack requiredItemStack = savedRecipe.items().get(slotNum);
            if (requiredItemStack == null) {
                continue;
            }
            // We already know that the material is right (including exact choice if applicable), just check counts
            int required = requiredItemStack.getAmount();
            int craftsAvailable = event.getInventory().getItem(slotNum+1).getAmount() / required; // slot 0 in the event inventory is the result
            maxCraftsAvailable = Math.min(maxCraftsAvailable, craftsAvailable);
        }
        if (maxCraftsAvailable == 0) {
            player.sendMessage("§cYou don't have enough items in the crafting table!");
            player.sendMessage("§3This is a special §bstacked items §3recipe.");
            player.sendMessage("§3Check §f<will be implemented later> §3for more information.");
            return;
        }

        // Check how much we are actually making & give the items
        ItemStack result = savedRecipe.result().clone();
        int actualAmountCrafted;
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            actualAmountCrafted = 0;
            while (maxCraftsAvailable > 0) {
                Map<Integer, ItemStack> unadded = player.getInventory().addItem(result);
                if (!unadded.isEmpty()) {
                    if (unadded.size() != 1)  throw new RuntimeException();
                    if (!unadded.containsKey(0))  throw new RuntimeException();
                    ItemStack failedToAdd = unadded.get(0);
                    if (failedToAdd.getAmount() == result.getAmount()) {
                        // All the items failed to add - pretend this craft never happened
                        break;
                    } else {
                        // Some added - drop the ones that didn't get added (this is the vanilla behavior)
                        player.getWorld().dropItemNaturally(player.getLocation(), failedToAdd);
                        actualAmountCrafted++;
                        maxCraftsAvailable--;
                        break;
                    }
                }
                actualAmountCrafted++;
                maxCraftsAvailable--;
            }
        } else {
            if (player.getItemOnCursor() == null || player.getItemOnCursor().getType() == Material.AIR) {
                actualAmountCrafted = 1;
                player.setItemOnCursor(result);
            } else if (player.getItemOnCursor().isSimilar(result)
                    && player.getItemOnCursor().getAmount() + result.getAmount() <= result.getMaxStackSize()) {
                actualAmountCrafted = 1;
                player.getItemOnCursor().setAmount(result.getAmount() + player.getItemOnCursor().getAmount());
            } else {
                // No space
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
            stack.setAmount(stack.getAmount() - required*actualAmountCrafted);
            event.getInventory().setItem(slotNum+1, stack);
        }
    }

}
