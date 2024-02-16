package io.github.greatericontop.greatcrafts.internal;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;
import java.util.Map;

public class RecipeSerializationHelper {

    public static Map<Character, ItemStack> serializePreciseIngredientMap(SavedRecipeShaped recipe, ShapedRecipe shapedRecipe) {
        Map<Character, ItemStack> preciseIngredientMap = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            char character = (char) ('a' + i);
            if (recipe.ingredientTypes()[i] == IngredientType.EXACT_CHOICE) {
                RecipeChoice.ExactChoice exactChoice = (RecipeChoice.ExactChoice) shapedRecipe.getChoiceMap().get(character);
                preciseIngredientMap.put(character, exactChoice.getItemStack());
            } else {
                preciseIngredientMap.put(character, shapedRecipe.getIngredientMap().get(character));
            }
        }
        return preciseIngredientMap;
    }

}
