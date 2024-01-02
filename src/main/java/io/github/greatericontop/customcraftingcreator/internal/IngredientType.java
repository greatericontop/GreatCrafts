package io.github.greatericontop.customcraftingcreator.internal;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public enum IngredientType {
    NORMAL,
    EXACT_CHOICE,
    MATERIAL_CHOICE,

    ;

    public static IngredientType[] defaults() {
        return new IngredientType[] {
                NORMAL, NORMAL, NORMAL,
                NORMAL, NORMAL, NORMAL,
                NORMAL, NORMAL, NORMAL
        };
    }

    public static List<List<Material>> defaultMaterialChoiceExtra() {
        List<List<Material>> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list.add(new ArrayList<>());
        }
        return list;
    }

}
