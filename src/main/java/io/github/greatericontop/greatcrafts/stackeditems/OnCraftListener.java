package io.github.greatericontop.greatcrafts.stackeditems;

import io.github.greatericontop.greatcrafts.GreatCrafts;
import io.github.greatericontop.greatcrafts.internal.datastructures.RecipeType;
import io.github.greatericontop.greatcrafts.internal.datastructures.SavedRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class OnCraftListener implements Listener {

    private final GreatCrafts plugin;
    public OnCraftListener(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraft(CraftItemEvent event) {
        // Our stacked items recipe is registered as a shaped recipe (in its basic form), so we know that this event
        // will always fire
        event.setCancelled(true);
        Recipe _recipe = event.getRecipe();
        if (!(_recipe instanceof ShapedRecipe recipe)) {
            return;
        }
        NamespacedKey recipeKey = recipe.getKey();
        // Check if it is a stacked items recipe
        SavedRecipe savedRecipe = plugin.recipeManager.getRecipe(recipeKey.toString());
        if (savedRecipe == null) {
            System.out.printf("debug: skipping, savedRecipe == null (%s)\n", recipeKey);
            return;
        }
        if (savedRecipe.type() != RecipeType.STACKED_ITEMS) {
            System.out.printf("debug: skipping, not stacked items (%s)\n", recipeKey);
            return;
        }
        // Check for sufficient items
        for (int slotNum = 0; slotNum < 9; slotNum++) {
            ItemStack requiredItemStack = savedRecipe.items().get(slotNum);
            if (requiredItemStack == null) {
                continue;
            }
            // We already know that the material is right

            // TODO
        }


        // TODO: handle shift click?
    }

}
