package io.github.greatericontop.greatcrafts.events;

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
import io.github.greatericontop.greatcrafts.internal.Util;
import io.github.greatericontop.greatcrafts.internal.datastructures.IngredientType;
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
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StackedItemsCraftListener implements Listener {

    private final GreatCrafts plugin;
    public StackedItemsCraftListener(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true) // Doesn't run if cancelled by PermissionRestrictionListener
    public void onCraft(CraftItemEvent event) {
        // Our stacked items recipe is registered as a recipe (in its basic form), so we know that this event
        // will always fire
        Recipe _rawRecipe = event.getRecipe();
        if (_rawRecipe instanceof ShapedRecipe _shapedRecipe) {
            NamespacedKey recipeKey = _shapedRecipe.getKey();
            SavedRecipe savedRecipe = plugin.recipeManager.getRecipe(recipeKey.toString());
            if (savedRecipe == null || savedRecipe.type() != RecipeType.STACKED_ITEMS) {
                return;
            }
            processStackedItems(event, savedRecipe, recipeKey);
        } else if (_rawRecipe instanceof ShapelessRecipe _shapelessRec) {
            NamespacedKey recipeKey = _shapelessRec.getKey();
            SavedRecipe savedRecipe = plugin.recipeManager.getRecipe(recipeKey.toString());
            if (savedRecipe == null || savedRecipe.type() != RecipeType.STACKED_ITEMS_SHAPELESS) {
                return;
            }
            processShapelessStackedItems(event, savedRecipe, recipeKey);
        }
    }

    private void processStackedItems(CraftItemEvent event, SavedRecipe savedRecipe, NamespacedKey recipeKey) {
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        // Figure out our offset
        // Example: In a 2x2 stacked items craft, the actual 2x2 grid could be in the top left, top right, bottom left, bottom right
        // We do this by finding the min/max (giving the top left and bottom right corners) of the items in the grid and comparing it to the min/max of the items in the crafting recipe
        //   This always works since Minecraft already verified for us that the shapes match
        // Also automatically accounts for the fact that slot 0 in the event inventory is the result and that the grid actually starts at getInventory().getItem(1)
        int minSlotInSavedRec = getMinSlotInSavedRec(savedRecipe);
        int minSlotInEventInv = Integer.MAX_VALUE;
        for (int i = 1; i <= 9; i++) { // grid is 1 to 9
            if (event.getInventory().getItem(i) != null && event.getInventory().getItem(i).getType() != Material.AIR) {
                minSlotInEventInv = Math.min(minSlotInEventInv, i);
            }
        }
        int savedRecToEventInvOffset = minSlotInEventInv - minSlotInSavedRec;
        // Check item count
        int maxCraftsAvailable = Integer.MAX_VALUE;
        for (int slotNum = 0; slotNum < 9; slotNum++) {
            ItemStack requiredItemStack = savedRecipe.items().get(slotNum);
            if (requiredItemStack == null || requiredItemStack.getType() == Material.AIR) {
                continue;
            }
            ItemStack itemStackInGrid = event.getInventory().getItem(slotNum+savedRecToEventInvOffset);
            // We actually don't know if the item will match, because Minecraft allows you to mirror the recipe
            // horizontally and still pass its own check. If it doesn't match up, just fail with error.
            if (itemStackInGrid == null
                    || itemStackInGrid.getType() != requiredItemStack.getType()
                    || (savedRecipe.ingredientTypes()[slotNum] == IngredientType.EXACT_CHOICE && !itemStackInGrid.isSimilar(requiredItemStack))
            ) {
                plugin.languager.stackedItemsErrorMissedExactMatch(player, recipeKey.toString());
                return;
            }
            int craftsAvailable = itemStackInGrid.getAmount() / requiredItemStack.getAmount();;
            maxCraftsAvailable = Math.min(maxCraftsAvailable, craftsAvailable);
        }
        if (maxCraftsAvailable == 0) {
            plugin.languager.stackedItemsErrorNotEnoughItems(player, recipeKey.toString());
            return;
        }

        int actualAmountCrafted = processCraft(savedRecipe, player, event, maxCraftsAvailable);

        // Remove them
        for (int slotNum = 0; slotNum < 9; slotNum++) {
            ItemStack requiredItemStack = savedRecipe.items().get(slotNum);
            if (requiredItemStack == null) {
                continue;
            }
            int required = requiredItemStack.getAmount();
            ItemStack stack = event.getInventory().getItem(slotNum+savedRecToEventInvOffset); // see above
            stack.setAmount(stack.getAmount() - required*actualAmountCrafted);
            event.getInventory().setItem(slotNum+savedRecToEventInvOffset, stack);
        }
    }

    public static int getMinSlotInSavedRec(SavedRecipe savedRecipe) {
        int minSlotInSavedRec = Integer.MAX_VALUE;
        for (int i = 0; i < 9; i++) {
            if (savedRecipe.items().get(i) != null && savedRecipe.items().get(i).getType() != Material.AIR) {
                minSlotInSavedRec = Math.min(minSlotInSavedRec, i);
            }
        }
        return minSlotInSavedRec;
    }

    private void processShapelessStackedItems(CraftItemEvent event, SavedRecipe savedRecipe, NamespacedKey recipeKey) {
        // Algo: Iterate through all required items (starting from the highest quantity required), and match it to the
        //       highest quantity of corresponding material in the slot. Use this to find out the maximum crafts we
        //       can get, and then remove both from the list.
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        List<ItemStack> requiredItems = new ArrayList<>(savedRecipe.items()); // clone
        List<ItemStack> inventorySlotItems = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            inventorySlotItems.add(event.getInventory().getItem(i)); // slots 1 to 9 in the inventory are the slots
        }
        int[] requiredItemsSlotMapping = new int[9]; // RISM[inventory slot 0-8] = index to use in requiredItems
        int maxCraftsAvailable = Integer.MAX_VALUE;
        requiredItems.removeIf(item -> (item == null || item.getType() == Material.AIR)); // remove empty
        requiredItems.sort((a, b) -> Integer.compare(b.getAmount(), a.getAmount()));

        // Matching step (greedy-style pairing the highest quantity requirement to the highest quantity of item)
        for (int reqIndex = 0; reqIndex < requiredItems.size(); reqIndex++) {
            ItemStack reqItemStack = requiredItems.get(reqIndex);
            int highestCountSoFar = 0;
            int highestCountSlot = -1;
            for (int slotNum = 0; slotNum < 9; slotNum++) {
                ItemStack inventorySlotItem = inventorySlotItems.get(slotNum);
                if (inventorySlotItem == null)  continue;
                if (inventorySlotItem.getType() != reqItemStack.getType())  continue;
                if (highestCountSoFar < inventorySlotItem.getAmount()) {
                    highestCountSoFar = inventorySlotItem.getAmount();
                    highestCountSlot = slotNum;
                }
            }
            inventorySlotItems.set(highestCountSlot, null); // we can't use this again
            int craftsPossibleHere = highestCountSoFar / reqItemStack.getAmount();
            maxCraftsAvailable = Math.min(maxCraftsAvailable, craftsPossibleHere);
            requiredItemsSlotMapping[highestCountSlot] = reqIndex; // Link up this inventory slot to the required ingredient so we know how many to remove later
        }

        if (maxCraftsAvailable == 0) {
            plugin.languager.stackedItemsErrorNotEnoughItems(player, recipeKey.toString());
            return;
        }

        int actualAmountCrafted = processCraft(savedRecipe, player, event, maxCraftsAvailable);

        // Remove items
        for (int slotIndex = 0; slotIndex < 9; slotIndex++) {
            int slotIndexInInvSetItem = slotIndex + 1;
            ItemStack stack = event.getInventory().getItem(slotIndexInInvSetItem);
            if (stack == null || stack.getType() == Material.AIR)  continue;
            int amountToRemove = requiredItems.get(requiredItemsSlotMapping[slotIndex]).getAmount() * actualAmountCrafted;
            stack.setAmount(stack.getAmount() - amountToRemove);
            event.getInventory().setItem(slotIndexInInvSetItem, stack);
        }
    }

    private int processCraft(SavedRecipe savedRecipe, Player player, CraftItemEvent event, int maxCraftsAvailable) {
        // First, apply the limit to maxCraftsAvailable
        String recKey = savedRecipe.key().toString();
        if (plugin.recipeCraftingLimits.containsKey(recKey)) {
            if (!plugin.recipeCraftingLimitsPlayers.containsKey(player.getUniqueId())) {
                plugin.recipeCraftingLimitsPlayers.put(player.getUniqueId(), new HashMap<>());
            }
            Map<String, Integer> playerCraftsMap = plugin.recipeCraftingLimitsPlayers.get(player.getUniqueId());
            int craftsMadePreviously = playerCraftsMap.getOrDefault(recKey, 0);
            int limit = plugin.recipeCraftingLimits.get(recKey);
            // Apply limit
            if (craftsMadePreviously >= limit) { // zero crafts left, exit now
                plugin.languager.craftingLimitFailure(player, limit);
                return 0;
            } else if (craftsMadePreviously + maxCraftsAvailable > limit) {
                maxCraftsAvailable = limit - craftsMadePreviously;
                // Notification sent & limit applied later
            }
        }

        ItemStack result = savedRecipe.result().clone();
        int actualAmountCrafted;
        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
            // Crafting limits handled in Util
            actualAmountCrafted = Util.performShiftClickCraft(player, result, maxCraftsAvailable);
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
                return 0;
            }
        }

        // Notification & limit application
        if (plugin.recipeCraftingLimits.containsKey(recKey)) {
            Map<String, Integer> playerCraftsMap = plugin.recipeCraftingLimitsPlayers.get(player.getUniqueId());
            int craftsMadePreviously = playerCraftsMap.getOrDefault(recKey, 0);
            int limit = plugin.recipeCraftingLimits.get(recKey);
            playerCraftsMap.put(recKey, craftsMadePreviously + actualAmountCrafted);
            plugin.languager.craftingLimitNotification(player, craftsMadePreviously + actualAmountCrafted, limit);
        }

        return actualAmountCrafted;
    }

}
