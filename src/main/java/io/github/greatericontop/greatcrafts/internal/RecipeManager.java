package io.github.greatericontop.greatcrafts.internal;

import io.github.greatericontop.greatcrafts.GreatCrafts;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RecipeManager {

    private final GreatCrafts plugin;
    public RecipeManager(GreatCrafts plugin) {
        this.plugin = plugin;
    }


    public List<SavedRecipe> getAllShapedRecipes() {
        Set<String> keys = plugin.recipes.getKeys(false);
        List<SavedRecipe> allRecipes = new ArrayList<>();
        for (String key : keys) {
            allRecipes.add(getRecipeShaped(key));
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
        serialized.add(recipe.key().getNamespace()); // 0 - namespace
        serialized.add(recipe.key().getKey()); // 1 - key
        serialized.add(recipe.items()); // 2 - items
        serialized.add(recipe.result()); // 3 - result
        // IngredientType[] -> List<String> (since enums can't be saved like this)
        List<String> convertedIngredientTypes = new ArrayList<>();
        for (IngredientType ingredientType : recipe.ingredientTypes()) {
            convertedIngredientTypes.add(ingredientType.toString());
        }
        serialized.add(convertedIngredientTypes); // 4 - ingredient types
        // List<List<Material>> -> List<List<String>> (Material enum)
        List<List<String>> materialChoiceStrings = new ArrayList<>();
        for (List<Material> originalList : recipe.materialChoiceExtra()) {
            List<String> newList = new ArrayList<>();
            for (Material material : originalList) {
                newList.add(material.toString());
            }
            materialChoiceStrings.add(newList);
        }
        serialized.add(materialChoiceStrings); // 5 - material choice data
        serialized.add(recipe.iconItem()); // 6 - icon item
        serialized.add(recipe.type().toString()); // 7 - recipe type (as a string)
        return serialized;
    }

    @SuppressWarnings("unchecked")
    public SavedRecipe deserializeShapedRecipe(List<Object> serialized) {
        String namespace = (String) serialized.get(0);
        String key = (String) serialized.get(1);
        NamespacedKey nameKey = new NamespacedKey(namespace, key);
        List<ItemStack> items = (List<ItemStack>) serialized.get(2);
        ItemStack result = (ItemStack) serialized.get(3);
        List<String> ingredientTypesRaw = (List<String>) serialized.get(4);
        IngredientType[] ingredientTypes = new IngredientType[9];
        for (int i = 0; i < 9; i++) {
            ingredientTypes[i] = IngredientType.valueOf(ingredientTypesRaw.get(i));
        }
        List<List<String>> materialChoiceStrings = (List<List<String>>) serialized.get(5);
        List<List<Material>> materialChoiceExtra = new ArrayList<>();
        for (List<String> originalList : materialChoiceStrings) {
            List<Material> newList = new ArrayList<>();
            for (String materialString : originalList) {
                newList.add(Material.valueOf(materialString));
            }
            materialChoiceExtra.add(newList);
        }
        ItemStack iconItem = (ItemStack) serialized.get(6);
        RecipeType type = RecipeType.valueOf((String) serialized.get(7));
        return new SavedRecipe(nameKey, type, items, result, ingredientTypes, materialChoiceExtra, iconItem);
    }


}
