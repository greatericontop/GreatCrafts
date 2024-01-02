package io.github.greatericontop.customcraftingcreator.internal;

import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public record SavedRecipe(
        ShapedRecipe recipe,
        IngredientType[] ingredientTypes,
        List<List<Material>>  materialChoiceExtra
) {
}
