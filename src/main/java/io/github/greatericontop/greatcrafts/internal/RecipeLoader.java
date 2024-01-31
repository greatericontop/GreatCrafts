package io.github.greatericontop.greatcrafts.internal;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ShapedRecipe;

public class RecipeLoader {

    public static void addUnshrinkedShapedRecipe(ShapedRecipe shapedRecipe) {
        String[] newShape = ShapeAnalyzer.shrink(shapedRecipe.getShape());
        shapedRecipe.shape(newShape);
        Bukkit.addRecipe(shapedRecipe);
    }

}
