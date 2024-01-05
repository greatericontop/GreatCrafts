package io.github.greatericontop.customcraftingcreator.internal;

import io.github.greatericontop.customcraftingcreator.CustomCraftingCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecipeManager {

    private final CustomCraftingCreator plugin;
    public RecipeManager(CustomCraftingCreator plugin) {
        this.plugin = plugin;
    }



    public List<ShapedRecipe> getAllRecipesShaped() {
        Set<String> keys = plugin.recipes.getKeys(false);
        List<ShapedRecipe> allRecipes = new ArrayList<>();
        for (String key : keys) {
            allRecipes.add(getRecipeShaped(key).recipe());
        }
        return allRecipes;
    }

    public SavedRecipe getRecipeShaped(String key) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        String string = plugin.recipes.getString(key);
        if (string == null) {
            return null;
        }
        try {
            yamlConfiguration.loadFromString(string);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return deserializeShapedRecipe((List<Object>) yamlConfiguration.get("shapedrecipe"));
    }

    public void setRecipeShaped(String key, SavedRecipe recipe) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("shapedrecipe", serializeShapedRecipe(recipe));
        plugin.recipes.set(key, yamlConfiguration.saveToString());
    }






    public List<Object> serializeShapedRecipe(SavedRecipe recipe) {
        List<Object> serialized = new ArrayList<>();
        ShapedRecipe shapedRecipe = recipe.recipe();
        serialized.add(shapedRecipe.getKey().getNamespace()); // 0 - namespace
        serialized.add(shapedRecipe.getKey().getKey()); // 1 - key
        // similar to ingredient map but each item must have its own nbt
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
        serialized.add(preciseIngredientMap); // 2 - precise ingredient map
        serialized.add(shapedRecipe.getShape()); // 3 - shape
        serialized.add(shapedRecipe.getResult()); // 4 - result
        // IngredientType[] -> List<String> (since enums can't be saved like this)
        List<String> convertedIngredientTypes = new ArrayList<>();
        for (IngredientType ingredientType : recipe.ingredientTypes()) {
            convertedIngredientTypes.add(ingredientType.toString());
        }
        serialized.add(convertedIngredientTypes); // 5 - ingredient types
        serialized.add(recipe.materialChoiceExtra()); // 6 - material choice extra
        return serialized;
    }

    @SuppressWarnings("unchecked")
    public SavedRecipe deserializeShapedRecipe(List<Object> serialized) {
        String namespace = (String) serialized.get(0);
        String key = (String) serialized.get(1);
        Map<String, ItemStack> ingredientMap = (Map<String, ItemStack>) serialized.get(2);
        List<String> shape = (List<String>) serialized.get(3);
        String[] shapeArray = shape.toArray(new String[0]);
        ItemStack result = (ItemStack) serialized.get(4);
        List<String> ingredientTypesRaw = (List<String>) serialized.get(5);
        IngredientType[] ingredientTypes = new IngredientType[9];
        for (int i = 0; i < 9; i++) {
            ingredientTypes[i] = IngredientType.valueOf(ingredientTypesRaw.get(i));
        }
        List<List<Material>> materialChoiceExtra = (List<List<Material>>) serialized.get(6);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(namespace, key), result);
        recipe.shape(shapeArray);
        for (Map.Entry<String, ItemStack> entry : ingredientMap.entrySet()) {
            char keyChar = entry.getKey().charAt(0);
            if (entry.getValue() == null)  continue;
            if (keyChar < 'a' || keyChar > 'i') {
                throw new RuntimeException("Unexpected keyChar! Should be one of abcdefghi");
            }
            int index = keyChar - 'a';
            switch (ingredientTypes[index]) {
                case NORMAL -> {
                    recipe.setIngredient(keyChar, entry.getValue().getType());
                }
                case EXACT_CHOICE -> {
                    recipe.setIngredient(keyChar, new RecipeChoice.ExactChoice(entry.getValue()));
                }
                case MATERIAL_CHOICE -> {
                    recipe.setIngredient(keyChar, entry.getValue().getType()); // TODO: placeholder
                }
                default -> {
                    throw new RuntimeException();
                }
            }
            // TODO: material choice
        }
        return new SavedRecipe(recipe, ingredientTypes, materialChoiceExtra);
    }


}
