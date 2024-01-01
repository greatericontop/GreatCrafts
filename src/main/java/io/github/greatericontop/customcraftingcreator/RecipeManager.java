package io.github.greatericontop.customcraftingcreator;

import io.github.greatericontop.customcraftingcreator.CustomCraftingCreator;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
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
            allRecipes.add(getRecipeShaped(key));
        }
        return allRecipes;
    }

    public ShapedRecipe getRecipeShaped(String key) {
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

    public void setRecipeShaped(String key, ShapedRecipe recipe) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("shapedrecipe", serializeShapedRecipe(recipe));
        plugin.recipes.set(key, yamlConfiguration.saveToString());
    }






    public List<Object> serializeShapedRecipe(ShapedRecipe recipe) {
        List<Object> serialized = new ArrayList<>();
        serialized.add(recipe.getKey().getNamespace()); // 0 - namespace
        serialized.add(recipe.getKey().getKey()); // 1 - key
        serialized.add(recipe.getIngredientMap()); // 2 - ingredient map
        serialized.add(recipe.getShape()); // 3 - shape
        serialized.add(recipe.getResult()); // 4 - result
        return serialized;
    }

    public ShapedRecipe deserializeShapedRecipe(List<Object> serialized) {
        String namespace = (String) serialized.get(0);
        String key = (String) serialized.get(1);
        Map<String, ItemStack> ingredientMap = (Map<String, ItemStack>) serialized.get(2);
        List<String> shape = (List<String>) serialized.get(3);
        String[] shapeArray = shape.toArray(new String[0]);
        ItemStack result = (ItemStack) serialized.get(4);
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(namespace, key), result);
        recipe.shape(shapeArray);
        for (Map.Entry<String, ItemStack> entry : ingredientMap.entrySet()) {
            char keyChar = entry.getKey().charAt(0);
            if (entry.getValue() == null)  continue;
            recipe.setIngredient(keyChar, entry.getValue().getType());
        }
        return recipe;
    }


}
