package io.github.greatericontop.greatcrafts.internal;

/*
 * Copyright (C) 2023-present greateric.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty  of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import io.github.greatericontop.greatcrafts.internal.datastructures.IngredientType;
import io.github.greatericontop.greatcrafts.internal.datastructures.SavedRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import javax.annotation.Nullable;
import java.util.List;

public class RecipeLoader {

    public static void compileAndAddRecipe(SavedRecipe recipe, @Nullable CommandSender commandSender) {
        switch (recipe.type()) {
            case SHAPED -> compileAndAddShapedRecipe(recipe);
            case SHAPELESS -> compileAndAddShapelessRecipe(recipe);
            case STACKED_ITEMS -> compileAndAddStackedItemsRecipe(recipe, commandSender);
            case STACKED_ITEMS_SHAPELESS -> compileAndAddStackedItemsShapelessRecipe(recipe, commandSender);
        }
    }

    private static void compileAndAddShapedRecipe(SavedRecipe shapedSavedRecipe) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(shapedSavedRecipe.key(), shapedSavedRecipe.result());
        List<ItemStack> slots = shapedSavedRecipe.items();
        // Kind of hacky fix: if a material choice is active, we need to put a placeholder item into the slot
        // If it is air/null, we assume that the slot is empty even though there is a material choice there
        for (int i = 0; i < 9; i++) {
            if (shapedSavedRecipe.ingredientTypes()[i] == IngredientType.MATERIAL_CHOICE) {
                if (shapedSavedRecipe.materialChoiceExtra().get(i).isEmpty()) {
                    slots.set(i, null);
                } else {
                    slots.set(i, new ItemStack(Material.END_PORTAL_FRAME));
                }
            }
        }
        // Add our ingredients to the shaped recipe
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

    private static void compileAndAddShapelessRecipe(SavedRecipe shapelessSavedRecipe) {
        ShapelessRecipe shapelessRec = new ShapelessRecipe(shapelessSavedRecipe.key(), shapelessSavedRecipe.result());
        List<ItemStack> slots = shapelessSavedRecipe.items();
        // Same fix as above
        for (int i = 0; i < 9; i++) {
            if (shapelessSavedRecipe.ingredientTypes()[i] == IngredientType.MATERIAL_CHOICE) {
                if (shapelessSavedRecipe.materialChoiceExtra().get(i).isEmpty()) {
                    slots.set(i, null);
                } else {
                    slots.set(i, new ItemStack(Material.END_PORTAL_FRAME));
                }
            }
        }
        // Add ingredients
        for (int i = 0; i < 9; i++) {
            if (slots.get(i) == null || slots.get(i).getType() == Material.AIR)  continue;
            switch (shapelessSavedRecipe.ingredientTypes()[i]) {
                case NORMAL -> {
                    shapelessRec.addIngredient(slots.get(i).getType());
                }
                case EXACT_CHOICE -> {
                    RecipeChoice.ExactChoice exactChoice = new RecipeChoice.ExactChoice(slots.get(i));
                    shapelessRec.addIngredient(exactChoice);
                }
                case MATERIAL_CHOICE -> {
                    RecipeChoice.MaterialChoice materialChoice = new RecipeChoice.MaterialChoice(shapelessSavedRecipe.materialChoiceExtra().get(i));
                    shapelessRec.addIngredient(materialChoice);
                }
                default -> {
                    throw new RuntimeException();
                }
            }
        }
        Bukkit.addRecipe(shapelessRec);
    }

    private static void compileAndAddStackedItemsRecipe(SavedRecipe shapedSavedRecipe, @Nullable CommandSender commandSender) {
        // This is almost a copy of compileAndAddShapedRecipe
        ShapedRecipe shapedRecipe = new ShapedRecipe(shapedSavedRecipe.key(), shapedSavedRecipe.result());
        List<ItemStack> slots = shapedSavedRecipe.items();
        // Material choice here is not used here, but we still have to put something in the grid so the stacked items
        // verifier doesn't break, so we just put the end portal frame in (the craft will be impossible to use until
        // it is fixed)
        for (int i = 0; i < 9; i++) {
            if (shapedSavedRecipe.ingredientTypes()[i] == IngredientType.MATERIAL_CHOICE) {
                if (shapedSavedRecipe.materialChoiceExtra().get(i).isEmpty()) {
                    slots.set(i, null);
                } else {
                    slots.set(i, new ItemStack(Material.END_PORTAL_FRAME, 64));
                }
            }
        }
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
                    shapedRecipe.setIngredient(symbol, slots.get(i).getType()); // see above note
                    if (commandSender != null) {
                        commandSender.sendMessage("§c("+shapedSavedRecipe.key()+") material choice used in a stacked items craft!");
                    }
                }
                default -> {
                    throw new RuntimeException();
                }
            }
        }
        String[] newShape = ShapeAnalyzer.shrink(shapedRecipe.getShape());
        shapedRecipe.shape(newShape);
        Bukkit.addRecipe(shapedRecipe);
    }

    private static void compileAndAddStackedItemsShapelessRecipe(SavedRecipe shapelessSavedRecipe, @Nullable CommandSender commandSender) {
        // Similar to add shapeless recipe but with no material choice
        ShapelessRecipe shapelessRec = new ShapelessRecipe(shapelessSavedRecipe.key(), shapelessSavedRecipe.result());
        List<ItemStack> slots = shapelessSavedRecipe.items();
        // See shaped stacked items recipe note
        for (int i = 0; i < 9; i++) {
            if (shapelessSavedRecipe.ingredientTypes()[i] == IngredientType.MATERIAL_CHOICE) {
                if (shapelessSavedRecipe.materialChoiceExtra().get(i).isEmpty()) {
                    slots.set(i, null);
                } else {
                    slots.set(i, new ItemStack(Material.END_PORTAL_FRAME, 64));
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            if (slots.get(i) == null || slots.get(i).getType() == Material.AIR)  continue;
            switch (shapelessSavedRecipe.ingredientTypes()[i]) {
                case NORMAL -> {
                    shapelessRec.addIngredient(slots.get(i).getType());
                }
                case EXACT_CHOICE -> {
                    RecipeChoice.ExactChoice exactChoice = new RecipeChoice.ExactChoice(slots.get(i));
                    shapelessRec.addIngredient(exactChoice);
                }
                case MATERIAL_CHOICE -> {
                    shapelessRec.addIngredient(slots.get(i).getType()); // see above note
                    if (commandSender != null) {
                        commandSender.sendMessage("§c("+shapelessSavedRecipe.key()+") material choice used in a stacked items craft!");
                    }
                }
                default -> {
                    throw new RuntimeException();
                }
            }
        }
        Bukkit.addRecipe(shapelessRec);
    }

}
