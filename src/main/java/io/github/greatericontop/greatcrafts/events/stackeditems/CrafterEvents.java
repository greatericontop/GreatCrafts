package io.github.greatericontop.greatcrafts.events.stackeditems;

/*
 * Copyright (C) 2023-present greateric.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty  of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import io.github.greatericontop.greatcrafts.GreatCrafts;
import io.github.greatericontop.greatcrafts.internal.datastructures.IngredientType;
import io.github.greatericontop.greatcrafts.internal.datastructures.RecipeType;
import io.github.greatericontop.greatcrafts.internal.datastructures.SavedRecipe;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Crafter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class CrafterEvents implements Listener {

    private final GreatCrafts plugin;
    public CrafterEvents(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onCrafterCraft(CrafterCraftEvent event) {
        Recipe _rawRecipe = event.getRecipe();
        if (_rawRecipe instanceof ShapedRecipe _shapedRecipe) {
            NamespacedKey recipeKey = _shapedRecipe.getKey();
            SavedRecipe savedRecipe = plugin.recipeManager.getRecipe(recipeKey.toString());
            if (savedRecipe == null || savedRecipe.type() != RecipeType.STACKED_ITEMS) {
                return;
            }
            processCrafterShapedStackedItems(event, savedRecipe, recipeKey);
        } else if (_rawRecipe instanceof ShapelessRecipe _shapelessRec) {
            NamespacedKey recipeKey = _shapelessRec.getKey();
            SavedRecipe savedRecipe = plugin.recipeManager.getRecipe(recipeKey.toString());
            if (savedRecipe == null || savedRecipe.type() != RecipeType.STACKED_ITEMS_SHAPELESS) {
                return;
            }
            processCrafterShapelessStackedItems(event, savedRecipe, recipeKey);
        }
    }

    private void processCrafterShapedStackedItems(CrafterCraftEvent event, SavedRecipe savedRecipe, NamespacedKey recipeKey) {
        // Crafters always craft one item at a time
        // Simple adaptation of the code in StackedItemsCraftListener, follows more or less the same logic
        event.setCancelled(true);
        Crafter blockState = (Crafter) event.getBlock().getState();
        Inventory eventInv = blockState.getInventory();
        // Offset
        int minSlotInSavedRec = StackedItemsCraftListener.getMinSlotInSavedRec(savedRecipe);
        int minSlotInEventInv = Integer.MAX_VALUE;
        for (int i = 0; i <= 8; i++) { // grid is 0 to 8 now
            if (eventInv.getItem(i) != null && eventInv.getItem(i).getType() != Material.AIR) {
                minSlotInEventInv = Math.min(minSlotInEventInv, i);
            }
        }
        int savedRecToEventInvOffset = minSlotInEventInv - minSlotInSavedRec;
        // Check sufficiency
        for (int slotNum = 0; slotNum < 9; slotNum++) {
            ItemStack requiredItemStack = savedRecipe.items().get(slotNum);
            if (requiredItemStack == null || requiredItemStack.getType() == Material.AIR) {
                continue;
            }
            ItemStack itemStackInGrid = eventInv.getItem(slotNum+savedRecToEventInvOffset);
            // Mirror check
            if (itemStackInGrid == null
                    || itemStackInGrid.getType() != requiredItemStack.getType()
                    || (savedRecipe.ingredientTypes()[slotNum] == IngredientType.EXACT_CHOICE && !itemStackInGrid.isSimilar(requiredItemStack))
            ) {
                messageNearbyPlayers(event, "§cThe recipe doesn't exactly match!",
                        "§3This is a special §bstacked items §3recipe.",
                        String.format("§3Check §f/viewrecipe %s §3to make the craft.", recipeKey));
                return;
            }
            if (itemStackInGrid.getAmount() < requiredItemStack.getAmount()) {
                // Fail silently when insufficient, don't need to bombard nearby players with messages
                return;
            }
        }
        // Remove - actually remove 1 less, because we are going to uncancel the event so the game removes the final 1
        for (int slotNum = 0; slotNum < 9; slotNum++) {
            ItemStack requiredItemStack = savedRecipe.items().get(slotNum);
            if (requiredItemStack == null) {
                continue;
            }
            int required = requiredItemStack.getAmount();
            ItemStack stack = eventInv.getItem(slotNum+savedRecToEventInvOffset);
            stack.setAmount(stack.getAmount() - required + 1);
            eventInv.setItem(slotNum+savedRecToEventInvOffset, stack);
        }
        event.setCancelled(false);
    }

    private void processCrafterShapelessStackedItems(CrafterCraftEvent event, SavedRecipe savedRecipe, NamespacedKey recipeKey) {
        // Similar to other one, again
        Crafter blockState = (Crafter) event.getBlock().getState();
        Inventory eventInv = blockState.getInventory();
        // Algo: Iterate through all required items (starting from the highest quantity required), and match it to the
        //       highest quantity of corresponding material in the slot. Use this to find out the maximum crafts we
        //       can get, and then remove both from the list.
        event.setCancelled(true);
        List<ItemStack> requiredItems = new ArrayList<>(savedRecipe.items()); // clone
        List<ItemStack> inventorySlotItems = new ArrayList<>();
        for (int i = 0; i <= 8; i++) { // 0 through 8
            inventorySlotItems.add(eventInv.getItem(i));
        }
        int[] requiredItemsSlotMapping = new int[9]; // RISM[inventory slot 0-8] = index to use in requiredItems
        requiredItems.removeIf(item -> (item == null || item.getType() == Material.AIR)); // remove empty
        requiredItems.sort((a, b) -> Integer.compare(b.getAmount(), a.getAmount()));
        // Matching step (greedy-style pairing the highest quantity requirement to the highest quantity of item)
        for (int reqIndex = 0; reqIndex < requiredItems.size(); reqIndex++) {
            ItemStack reqItemStack = requiredItems.get(reqIndex);
            int highestCountSoFar = 0;
            int highestCountSlot = -1;
            for (int slotNum = 0; slotNum < 9; slotNum++) {
                ItemStack inventorySlotItem = inventorySlotItems.get(slotNum);
                if (inventorySlotItem == null) continue;
                if (inventorySlotItem.getType() != reqItemStack.getType()) continue;
                if (highestCountSoFar < inventorySlotItem.getAmount()) {
                    highestCountSoFar = inventorySlotItem.getAmount();
                    highestCountSlot = slotNum;
                }
            }
            if (highestCountSoFar < reqItemStack.getAmount()) {
                // Fail silently again
                return;
            }
            inventorySlotItems.set(highestCountSlot, null); // we can't use this again
            requiredItemsSlotMapping[highestCountSlot] = reqIndex; // Link up this inventory slot to the required ingredient so we know how many to remove later
        }

        // Remove items (again leaving 1) and uncancel
        for (int slotIndex = 0; slotIndex < 9; slotIndex++) {
            ItemStack stack = eventInv.getItem(slotIndex); // these match now (no off by 1 like in the other case)
            if (stack == null || stack.getType() == Material.AIR)  continue;
            int amountToRemove = requiredItems.get(requiredItemsSlotMapping[slotIndex]).getAmount() - 1;
            stack.setAmount(stack.getAmount() - amountToRemove);
            eventInv.setItem(slotIndex, stack);
        }
        event.setCancelled(false);
    }

    private static void messageNearbyPlayers(CrafterCraftEvent event, String... messages) {
        for (Entity e : event.getBlock().getWorld().getNearbyEntities(event.getBlock().getLocation(), 20.0, 20.0, 20.0, entity -> entity instanceof Player)) {
            for (String msg : messages) {
                e.sendMessage(msg);
            }
        }
    }

}
