package io.github.greatericontop.greatcrafts.internal.datastructures;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public record SavedRecipe(
        NamespacedKey key,
        RecipeType type,
        List<ItemStack> items,
        ItemStack result,
        IngredientType[] ingredientTypes,
        List<List<Material>> materialChoiceExtra,
        ItemStack iconItem
) {
}
