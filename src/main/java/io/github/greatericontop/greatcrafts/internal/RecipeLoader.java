package io.github.greatericontop.greatcrafts.internal;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.List;

public class RecipeLoader {

    public static void compileAndAddShapedRecipe(SavedRecipe shapedSavedRecipe) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(shapedSavedRecipe.key(), shapedSavedRecipe.result());
        // Add our ingredients to the shaped recipe
        List<ItemStack> slots = shapedSavedRecipe.items();
        char[] layout = "         ".toCharArray();
        for (int i = 0; i < 9; i++) {
            if (slots.get(i) == null || slots.get(i).getType() == Material.AIR)  continue;
            char symbol = (char) ('a' + i);
            layout[i] = symbol;
        }
        shapedRecipe.shape(
                new String(new char[]{layout[0], layout[1], layout[2]}),
                new String(new char[]{layout[3], layout[4], layout[5]}),
                new String(new char[]{layout[6], layout[7], layout[8]})
        );
        // Ingredient must be set AFTER shape is set
        for (int i = 0; i < 9; i++) {
            if (slots.get(i) == null || slots.get(i).getType() == Material.AIR)  continue;
            char symbol = (char) ('a' + i);
            switch (shapedSavedRecipe.ingredientTypes()[i]) {
                case NORMAL -> {
                    shapedRecipe.setIngredient(symbol, slots.get(i).getType());
                }
                case EXACT_CHOICE -> {
                    RecipeChoice.ExactChoice exactChoice = new RecipeChoice.ExactChoice(slots.get(i));
                    shapedRecipe.setIngredient(symbol, exactChoice);
                }
                case MATERIAL_CHOICE -> {
                    RecipeChoice.MaterialChoice materialChoice = new RecipeChoice.MaterialChoice(shapedSavedRecipe.materialChoiceExtra().get(i));
                    shapedRecipe.setIngredient(symbol, materialChoice);
                }
                default -> {
                    throw new RuntimeException();
                }
            }
        }
        // Post-processing
        String[] newShape = ShapeAnalyzer.shrink(shapedRecipe.getShape());
        shapedRecipe.shape(newShape);
        Bukkit.addRecipe(shapedRecipe);
    }

}
