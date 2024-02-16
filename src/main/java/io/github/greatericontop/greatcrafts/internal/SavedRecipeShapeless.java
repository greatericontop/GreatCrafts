package io.github.greatericontop.greatcrafts.internal;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.List;

public record SavedRecipeShapeless(
        ShapelessRecipe recipe,
        IngredientType[] ingredientTypes,
        List<List<Material>> materialChoiceExtra,
        ItemStack iconItem
) {
}
