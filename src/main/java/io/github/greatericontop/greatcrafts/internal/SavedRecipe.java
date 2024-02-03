package io.github.greatericontop.greatcrafts.internal;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public record SavedRecipe(
        ShapedRecipe recipe,
        IngredientType[] ingredientTypes,
        List<List<Material>>  materialChoiceExtra,
        ItemStack iconItem
) {
}
